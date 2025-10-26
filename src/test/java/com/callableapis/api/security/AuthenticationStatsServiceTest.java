package com.callableapis.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AuthenticationStatsService to ensure proper tracking of
 * authentication statistics.
 */
@DisplayName("AuthenticationStatsService Tests")
public class AuthenticationStatsServiceTest {

    private AuthenticationStatsService authStatsService;

    @BeforeEach
    void setUp() {
        authStatsService = new AuthenticationStatsService();
    }

    @Test
    @DisplayName("Should record successful authentication")
    void testRecordSuccessfulAuth() {
        authStatsService.recordSuccessfulAuth("github");

        assertEquals(1, authStatsService.getSuccessfulAuthCount("github"));
        assertEquals(0, authStatsService.getFailedAuthCount("github"));
    }

    @Test
    @DisplayName("Should record failed authentication")
    void testRecordFailedAuth() {
        authStatsService.recordFailedAuth("github");

        assertEquals(0, authStatsService.getSuccessfulAuthCount("github"));
        assertEquals(1, authStatsService.getFailedAuthCount("github"));
    }

    @Test
    @DisplayName("Should handle multiple providers")
    void testMultipleProviders() {
        authStatsService.recordSuccessfulAuth("github");
        authStatsService.recordSuccessfulAuth("github");
        authStatsService.recordFailedAuth("github");

        authStatsService.recordSuccessfulAuth("google");
        authStatsService.recordFailedAuth("google");
        authStatsService.recordFailedAuth("google");

        assertEquals(2, authStatsService.getSuccessfulAuthCount("github"));
        assertEquals(1, authStatsService.getFailedAuthCount("github"));

        assertEquals(1, authStatsService.getSuccessfulAuthCount("google"));
        assertEquals(2, authStatsService.getFailedAuthCount("google"));
    }

    @Test
    @DisplayName("Should normalize provider names")
    void testProviderNameNormalization() {
        authStatsService.recordSuccessfulAuth("GitHub");
        authStatsService.recordSuccessfulAuth("GITHUB");
        authStatsService.recordSuccessfulAuth("  github  ");

        assertEquals(3, authStatsService.getSuccessfulAuthCount("github"));
        assertEquals(3, authStatsService.getSuccessfulAuthCount("GitHub"));
        assertEquals(3, authStatsService.getSuccessfulAuthCount("GITHUB"));
    }

    @Test
    @DisplayName("Should handle null and empty provider names")
    void testNullAndEmptyProviders() {
        authStatsService.recordSuccessfulAuth(null);
        authStatsService.recordSuccessfulAuth("");
        authStatsService.recordSuccessfulAuth("   ");

        authStatsService.recordFailedAuth(null);
        authStatsService.recordFailedAuth("");
        authStatsService.recordFailedAuth("   ");

        // Should not crash and should not record anything
        Map<String, Map<String, Long>> allStats = authStatsService.getAllStats();
        assertTrue(allStats.isEmpty(), "Should not record stats for null/empty providers");
    }

    @Test
    @DisplayName("Should return correct statistics summary")
    void testGetStatsSummary() {
        authStatsService.recordSuccessfulAuth("github");
        authStatsService.recordSuccessfulAuth("github");
        authStatsService.recordFailedAuth("github");

        authStatsService.recordSuccessfulAuth("google");
        authStatsService.recordFailedAuth("google");
        authStatsService.recordFailedAuth("google");

        String summary = authStatsService.getStatsSummary();

        assertNotNull(summary, "Summary should not be null");
        assertTrue(summary.contains("Authentication Statistics"), "Should contain header");
        assertTrue(summary.contains("github:"), "Should contain github stats");
        assertTrue(summary.contains("google:"), "Should contain google stats");
        assertTrue(summary.contains("Total Attempts: 3"), "Should contain total attempts for github");
        assertTrue(summary.contains("Total Attempts: 3"), "Should contain total attempts for google");
        assertTrue(summary.contains("Success Rate: 66.7%"), "Should contain success rate for github");
        assertTrue(summary.contains("Success Rate: 33.3%"), "Should contain success rate for google");
    }

    @Test
    @DisplayName("Should return empty summary when no stats recorded")
    void testEmptyStatsSummary() {
        String summary = authStatsService.getStatsSummary();

        assertEquals("No authentication attempts recorded", summary);
    }

    @Test
    @DisplayName("Should return all statistics as map")
    void testGetAllStats() {
        authStatsService.recordSuccessfulAuth("github");
        authStatsService.recordFailedAuth("github");

        Map<String, Map<String, Long>> allStats = authStatsService.getAllStats();

        assertNotNull(allStats, "All stats should not be null");
        assertEquals(1, allStats.size(), "Should have one provider");
        assertTrue(allStats.containsKey("github"), "Should contain github provider");

        Map<String, Long> githubStats = allStats.get("github");
        assertEquals(2, githubStats.get("successful"), "Should have 2 successful auths");
        assertEquals(1, githubStats.get("failed"), "Should have 1 failed auth");
    }

    @Test
    @DisplayName("Should reset statistics")
    void testResetStats() {
        authStatsService.recordSuccessfulAuth("github");
        authStatsService.recordFailedAuth("github");

        assertEquals(1, authStatsService.getSuccessfulAuthCount("github"));
        assertEquals(1, authStatsService.getFailedAuthCount("github"));

        authStatsService.resetStats();

        assertEquals(0, authStatsService.getSuccessfulAuthCount("github"));
        assertEquals(0, authStatsService.getFailedAuthCount("github"));

        Map<String, Map<String, Long>> allStats = authStatsService.getAllStats();
        assertTrue(allStats.isEmpty(), "All stats should be empty after reset");
    }

    @Test
    @DisplayName("Should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        int numThreads = 10;
        int operationsPerThread = 100;
        Thread[] threads = new Thread[numThreads];

        // Create threads that record successful auths
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    authStatsService.recordSuccessfulAuth("github");
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify total count
        long expectedCount = numThreads * operationsPerThread;
        assertEquals(expectedCount, authStatsService.getSuccessfulAuthCount("github"));
    }

    @Test
    @DisplayName("Should handle zero success rate correctly")
    void testZeroSuccessRate() {
        authStatsService.recordFailedAuth("github");
        authStatsService.recordFailedAuth("github");

        String summary = authStatsService.getStatsSummary();

        assertTrue(summary.contains("Success Rate: 0.0%"), "Should show 0% success rate");
    }

    @Test
    @DisplayName("Should handle 100% success rate correctly")
    void testHundredPercentSuccessRate() {
        authStatsService.recordSuccessfulAuth("github");
        authStatsService.recordSuccessfulAuth("github");

        String summary = authStatsService.getStatsSummary();

        assertTrue(summary.contains("Success Rate: 100.0%"), "Should show 100% success rate");
    }
}
