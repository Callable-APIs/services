package com.callableapis.api.config;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;
import software.amazon.awssdk.services.ssm.model.SsmException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Service for reading configuration values from AWS Systems Manager Parameter Store.
 * Includes caching to avoid repeated API calls and fallback to environment variables.
 */
public final class ParameterStoreService {
    private static final Logger logger = Logger.getLogger(ParameterStoreService.class.getName());
    private static final ParameterStoreService INSTANCE = new ParameterStoreService();
    
    private final SsmClient ssmClient;
    private final ConcurrentHashMap<String, CachedParameter> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MINUTES = 5; // Cache for 5 minutes
    private static final long CRITICAL_CACHE_TTL_MINUTES = 1; // Critical parameters cache for 1 minute
    
    private ParameterStoreService() {
        logger.info("Initializing Parameter Store Service...");
        SsmClient client = null;
        
        // In test environments, skip AWS client creation entirely to avoid hangs
        // Tests should use environment variables or mocks
        if (isTestEnvironment()) {
            logger.info("Test environment detected - skipping AWS client creation (will use environment variables only)");
            this.ssmClient = null;
            return;
        }
        
        try {
            // Use region from environment variable AWS_DEFAULT_REGION, or default to US_EAST_1
            String regionStr = System.getenv("AWS_DEFAULT_REGION");
            Region region = (regionStr != null && !regionStr.isEmpty()) 
                    ? Region.of(regionStr) 
                    : Region.US_EAST_1;
            logger.info("Using AWS region: " + region.id());
            
            // Create client without blocking - credentials will be resolved lazily
            // This prevents hangs during initialization, especially in test environments
            client = SsmClient.builder()
                    .region(region)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
            
            // Skip the blocking test call during initialization to prevent hangs
            // The client will be tested on first actual use, and failures will fall back gracefully
            logger.info("Parameter Store Service initialized (will test credentials on first use)");
        } catch (Exception e) {
            logger.warning("Failed to initialize SSM client (will use environment variables only): " + e.getMessage());
            client = null; // Mark as unavailable if initialization failed
        }
        this.ssmClient = client; // Set to null if initialization failed
    }
    
    /**
     * Detect if we're running in a test environment.
     * This prevents AWS client creation in tests, avoiding hangs and credential resolution issues.
     */
    private static boolean isTestEnvironment() {
        // Check for common test environment indicators
        String classPath = System.getProperty("java.class.path", "");
        boolean hasJUnit = classPath.contains("junit") || classPath.contains("test");
        boolean hasGradleTest = System.getProperty("org.gradle.test.worker") != null;
        boolean hasTestProperty = System.getProperty("test.environment") != null;
        boolean hasJUnitProperty = System.getProperty("junit.jupiter.execution.enabled") != null;
        
        return hasJUnit || hasGradleTest || hasTestProperty || hasJUnitProperty;
    }
    
    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "Intentional singleton service returned by accessor")
    public static ParameterStoreService getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get a parameter value from Parameter Store with caching and fallback.
     * Uses version-based cache invalidation to detect parameter updates.
     * 
     * @param parameterName The parameter name (e.g., "/callableapis/github/client-id")
     * @param fallbackValue Fallback value if parameter is not found or service is unavailable
     * @return The parameter value or fallback value
     */
    public String getParameter(String parameterName, String fallbackValue) {
        logger.info("Attempting to get parameter: " + parameterName);
        
        // If SSM client is not available, skip Parameter Store and use fallback
        if (ssmClient == null) {
            logger.warning("SSM client not available - Parameter Store disabled. Using fallback value for: " + parameterName);
            logger.warning("This typically means AWS credentials are not configured. In production, ensure IAM role has Parameter Store access.");
            return fallbackValue;
        }
        
        // Check cache first - but also check version to detect parameter updates
        // Version checking is especially important for critical parameters like redirect-uri
        CachedParameter cached = cache.get(parameterName);
        boolean isCritical = isCriticalParameter(parameterName);
        
        if (cached != null && !cached.isExpired(isCritical)) {
            // For critical parameters, always check version to detect updates immediately
            // For non-critical parameters, rely on TTL-based expiration
            if (isCritical && cached.version != null) {
                // Check if parameter version has changed (for critical parameters only)
                // This allows immediate detection of parameter updates
                try {
                    GetParameterRequest versionRequest = GetParameterRequest.builder()
                            .name(parameterName)
                            .build();
                    GetParameterResponse versionResponse = ssmClient.getParameter(versionRequest);
                    Long currentVersion = versionResponse.parameter().version();
                    
                    if (cached.version.equals(currentVersion)) {
                        // Version matches, use cached value
                        logger.info("Using cached parameter (version " + currentVersion + "): " + parameterName + " = " + cached.value);
                        return cached.value;
                    } else {
                        // Version changed, force refresh
                        logger.info("Parameter version changed from " + cached.version + " to " + currentVersion + 
                                   ", refreshing cache for: " + parameterName);
                        // Fall through to fetch new value
                    }
                } catch (Exception versionCheckException) {
                    // If version check fails, fall back to TTL-based cache
                    logger.warning("Failed to check parameter version, using TTL-based cache: " + versionCheckException.getMessage());
                    logger.info("Using cached parameter: " + parameterName + " = " + cached.value);
                    return cached.value;
                }
            } else {
                // Non-critical parameter or no version info - use cached value
                logger.info("Using cached parameter: " + parameterName + " = " + cached.value);
                return cached.value;
            }
        }
        
        try {
            logger.info("Fetching parameter from Parameter Store: " + parameterName);
            
            // Fetch from Parameter Store
            GetParameterRequest request = GetParameterRequest.builder()
                    .name(parameterName)
                    .withDecryption(true) // Decrypt SecureString parameters
                    .build();
            
            GetParameterResponse response = ssmClient.getParameter(request);
            String value = response.parameter().value();
            Long version = response.parameter().version();
            
            // Cache the result with version information
            cache.put(parameterName, new CachedParameter(value, System.currentTimeMillis(), version));
            
            logger.info("Successfully retrieved parameter from Parameter Store (version " + version + "): " + 
                       parameterName + " = " + value);
            return value;
            
        } catch (ParameterNotFoundException e) {
            logger.warning("Parameter not found in Parameter Store: " + parameterName + ", using fallback: " + fallbackValue);
            return fallbackValue;
        } catch (SsmException e) {
            logger.warning("SSM error retrieving parameter: " + parameterName + 
                          ", error: " + e.getMessage() + ", error code: " + e.awsErrorDetails().errorCode() + 
                          ", using fallback: " + fallbackValue);
            return fallbackValue;
        } catch (Exception e) {
            logger.severe("Unexpected error retrieving parameter: " + parameterName + 
                         ", error: " + e.getMessage() + ", using fallback: " + fallbackValue);
            e.printStackTrace();
            return fallbackValue;
        }
    }
    
    /**
     * Get a parameter value from Parameter Store, with fallback to environment variable.
     * 
     * @param parameterName The parameter name
     * @param envVarName The environment variable name as fallback
     * @param defaultValue Default value if both parameter store and env var are unavailable
     * @return The parameter value, env var value, or default value
     */
    public String getParameterWithEnvFallback(String parameterName, String envVarName, String defaultValue) {
        String paramValue = getParameter(parameterName, null);
        if (paramValue != null) {
            return paramValue;
        }
        
        String envValue = System.getenv(envVarName);
        if (envValue != null && !envValue.isBlank()) {
            logger.info("Using environment variable fallback: " + envVarName);
            return envValue;
        }
        
        return defaultValue;
    }
    
    /**
     * Clear the cache (useful for testing or when parameters are updated)
     */
    public void clearCache() {
        cache.clear();
    }
    
    /**
     * Cached parameter with expiration time and version tracking.
     * Version tracking allows detection of parameter updates even if TTL hasn't expired.
     */
    private static class CachedParameter {
        final String value;
        final long timestamp;
        final Long version; // Parameter version from AWS SSM (null if not available)
        
        @SuppressWarnings("unused") // Kept for backward compatibility
        CachedParameter(String value, long timestamp) {
            this(value, timestamp, null);
        }
        
        CachedParameter(String value, long timestamp, Long version) {
            this.value = value;
            this.timestamp = timestamp;
            this.version = version;
        }
        
        boolean isExpired(boolean isCritical) {
            long ttlMinutes = isCritical ? CRITICAL_CACHE_TTL_MINUTES : CACHE_TTL_MINUTES;
            return System.currentTimeMillis() - timestamp > TimeUnit.MINUTES.toMillis(ttlMinutes);
        }
    }
    
    /**
     * Clear the cache for a specific parameter or all parameters.
     * 
     * @param parameterName The parameter name to clear, or null to clear all
     */
    public void clearCache(String parameterName) {
        if (parameterName == null) {
            logger.info("Clearing all parameter cache");
            cache.clear();
        } else {
            logger.info("Clearing cache for parameter: " + parameterName);
            cache.remove(parameterName);
        }
    }
    
    /**
     * Check if Parameter Store is available (SSM client initialized successfully).
     * 
     * @return true if Parameter Store is available, false if using fallback values only
     */
    public boolean isParameterStoreAvailable() {
        return ssmClient != null;
    }
    
    /**
     * Check if a parameter is considered critical (should use shorter cache TTL).
     * 
     * @param parameterName The parameter name
     * @return true if the parameter is critical
     */
    private boolean isCriticalParameter(String parameterName) {
        return parameterName != null && (
            parameterName.contains("redirect-uri") || 
            parameterName.contains("callback") ||
            parameterName.contains("client-id") ||
            parameterName.contains("client-secret")
        );
    }
}
