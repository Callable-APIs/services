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
        try {
            client = SsmClient.builder()
                    .region(Region.US_EAST_1) // Adjust region as needed
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
            
            // Test credentials by making a simple call
            logger.info("Testing Parameter Store credentials...");
            try {
                GetParameterRequest testRequest = GetParameterRequest.builder()
                        .name("/callableapis/github/oauth-scope")
                        .withDecryption(true)
                        .build();
                client.getParameter(testRequest);
                logger.info("Parameter Store Service initialized successfully with working credentials");
            } catch (Exception testException) {
                logger.warning("Parameter Store client created but credentials test failed: " + testException.getMessage());
                logger.warning("Will use fallback values only");
                client = null; // Mark as unavailable if credentials don't work
            }
        } catch (Exception e) {
            logger.warning("Failed to initialize SSM client (will use environment variables only): " + e.getMessage());
        }
        this.ssmClient = client; // Set to null if initialization failed or credentials don't work
    }
    
    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "Intentional singleton service returned by accessor")
    public static ParameterStoreService getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get a parameter value from Parameter Store with caching and fallback.
     * 
     * @param parameterName The parameter name (e.g., "/callableapis/github/client-id")
     * @param fallbackValue Fallback value if parameter is not found or service is unavailable
     * @return The parameter value or fallback value
     */
    public String getParameter(String parameterName, String fallbackValue) {
        logger.info("Attempting to get parameter: " + parameterName);
        
        // Check cache first
        CachedParameter cached = cache.get(parameterName);
        if (cached != null && !cached.isExpired(isCriticalParameter(parameterName))) {
            logger.info("Using cached parameter: " + parameterName + " = " + cached.value);
            return cached.value;
        }
        
        // If SSM client is not available, skip Parameter Store and use fallback
        if (ssmClient == null) {
            logger.warning("SSM client not available - Parameter Store disabled. Using fallback value for: " + parameterName);
            logger.warning("This typically means AWS credentials are not configured. In production, ensure IAM role has Parameter Store access.");
            return fallbackValue;
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
            
            // Cache the result
            cache.put(parameterName, new CachedParameter(value, System.currentTimeMillis()));
            
            logger.info("Successfully retrieved parameter from Parameter Store: " + parameterName + " = " + value);
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
     * Cached parameter with expiration time
     */
    private static class CachedParameter {
        final String value;
        final long timestamp;
        
        CachedParameter(String value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
        
        boolean isExpired() {
            return isExpired(false);
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
