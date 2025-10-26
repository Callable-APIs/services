package com.callableapis.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

/**
 * Service for tracking authentication statistics per OIDC provider.
 * Thread-safe implementation using atomic counters.
 */
public class AuthenticationStatsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationStatsService.class);

    // Thread-safe counters for each OIDC provider
    private final Map<String, AtomicLong> successfulAuths = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> failedAuths = new ConcurrentHashMap<>();

    /**
     * Record a successful authentication for the given OIDC provider.
     * 
     * @param provider The OIDC provider name (e.g., "github")
     */
    public void recordSuccessfulAuth(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            logger.warn("Attempted to record successful auth with null or empty provider");
            return;
        }
        
        String normalizedProvider = provider.toLowerCase().trim();
        successfulAuths.computeIfAbsent(normalizedProvider, k -> new AtomicLong(0)).incrementAndGet();
        logger.debug("Recorded successful authentication for provider: {}", normalizedProvider);
    }

    /**
     * Record a failed authentication for the given OIDC provider.
     * 
     * @param provider The OIDC provider name (e.g., "github")
     */
    public void recordFailedAuth(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            logger.warn("Attempted to record failed auth with null or empty provider");
            return;
        }
        
        String normalizedProvider = provider.toLowerCase().trim();
        failedAuths.computeIfAbsent(normalizedProvider, k -> new AtomicLong(0)).incrementAndGet();
        logger.debug("Recorded failed authentication for provider: {}", normalizedProvider);
    }

    /**
     * Get the number of successful authentications for a specific provider.
     * 
     * @param provider The OIDC provider name
     * @return Number of successful authentications
     */
    public long getSuccessfulAuthCount(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return 0;
        }
        
        String normalizedProvider = provider.toLowerCase().trim();
        AtomicLong counter = successfulAuths.get(normalizedProvider);
        return counter != null ? counter.get() : 0;
    }

    /**
     * Get the number of failed authentications for a specific provider.
     * 
     * @param provider The OIDC provider name
     * @return Number of failed authentications
     */
    public long getFailedAuthCount(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return 0;
        }
        
        String normalizedProvider = provider.toLowerCase().trim();
        AtomicLong counter = failedAuths.get(normalizedProvider);
        return counter != null ? counter.get() : 0;
    }

    /**
     * Get all authentication statistics as a map.
     * 
     * @return Map containing provider -> {successful: count, failed: count}
     */
    public Map<String, Map<String, Long>> getAllStats() {
        Map<String, Map<String, Long>> stats = new ConcurrentHashMap<>();
        
        // Get all unique providers from both maps
        var allProviders = new java.util.HashSet<String>();
        allProviders.addAll(successfulAuths.keySet());
        allProviders.addAll(failedAuths.keySet());
        
        for (String provider : allProviders) {
            Map<String, Long> providerStats = new ConcurrentHashMap<>();
            providerStats.put("successful", getSuccessfulAuthCount(provider));
            providerStats.put("failed", getFailedAuthCount(provider));
            stats.put(provider, providerStats);
        }
        
        return stats;
    }

    /**
     * Get a summary of authentication statistics as a formatted string.
     * 
     * @return Formatted string with authentication statistics
     */
    public String getStatsSummary() {
        Map<String, Map<String, Long>> allStats = getAllStats();
        
        if (allStats.isEmpty()) {
            return "No authentication attempts recorded";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Authentication Statistics:\n");
        
        for (Map.Entry<String, Map<String, Long>> entry : allStats.entrySet()) {
            String provider = entry.getKey();
            Map<String, Long> stats = entry.getValue();
            long successful = stats.getOrDefault("successful", 0L);
            long failed = stats.getOrDefault("failed", 0L);
            long total = successful + failed;
            
            summary.append("  ").append(provider).append(":\n");
            summary.append("    Total Attempts: ").append(total).append("\n");
            summary.append("    Successful: ").append(successful).append("\n");
            summary.append("    Failed: ").append(failed).append("\n");
            
            if (total > 0) {
                double successRate = (double) successful / total * 100;
                summary.append("    Success Rate: ").append(String.format("%.1f", successRate)).append("%\n");
            }
        }
        
        return summary.toString();
    }

    /**
     * Reset all authentication statistics.
     * Useful for testing or manual reset.
     */
    public void resetStats() {
        successfulAuths.clear();
        failedAuths.clear();
        logger.info("Authentication statistics reset");
    }
}
