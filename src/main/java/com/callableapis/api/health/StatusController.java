package com.callableapis.api.health;

import com.callableapis.api.config.AppConfig;
import com.callableapis.api.security.AuthenticationStatsService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Status controller for detailed service status information.
 * Provides comprehensive status including base container features and Tomcat
 * info.
 */
@Path("/status")
public class StatusController {

    @Inject
    private AuthenticationStatsService authStatsService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressFBWarnings(value = "DM_SYSTEM_GETPROPERTY", justification = "System.getProperty is safe for reading system properties")
    public Response getStatus() {
        Map<String, Object> status = new HashMap<>();

        // Basic service info
        status.put("service", "Callable APIs Services");
        status.put("version", "1.0.0");
        status.put("timestamp", Instant.now().toString());
        status.put("container", "rl337/callableapis:services");

        // Java/Tomcat info
        Map<String, Object> runtime = new HashMap<>();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        runtime.put("java_version", System.getProperty("java.version"));
        runtime.put("java_vendor", System.getProperty("java.vendor"));
        runtime.put("tomcat_version", "10.1.18");
        runtime.put("uptime_ms", runtimeBean.getUptime());
        runtime.put("start_time", Instant.ofEpochMilli(runtimeBean.getStartTime()).toString());
        status.put("runtime", runtime);

        // Memory info
        Map<String, Object> memory = new HashMap<>();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        memory.put("heap_used_mb", memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024);
        memory.put("heap_max_mb", memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024);
        memory.put("non_heap_used_mb", memoryBean.getNonHeapMemoryUsage().getUsed() / 1024 / 1024);
        status.put("memory", memory);

        // Secrets management status
        Map<String, Object> secrets = new HashMap<>();
        try {
            String secretsStatus = AppConfig.getSecretsStatusSummary();
            secrets.put("status", "available");
            secrets.put("summary", secretsStatus);
        } catch (Exception e) {
            secrets.put("status", "error");
            secrets.put("error", e.getMessage());
        }
        status.put("secrets", secrets);

        // Authentication statistics
        Map<String, Object> authStats = new HashMap<>();
        try {
            Map<String, Map<String, Long>> allAuthStats = authStatsService.getAllStats();
            authStats.put("status", "available");
            authStats.put("providers", allAuthStats);
            
            // Add summary for each provider
            Map<String, Object> summary = new HashMap<>();
            for (Map.Entry<String, Map<String, Long>> entry : allAuthStats.entrySet()) {
                String provider = entry.getKey();
                Map<String, Long> stats = entry.getValue();
                long successful = stats.getOrDefault("successful", 0L);
                long failed = stats.getOrDefault("failed", 0L);
                long total = successful + failed;
                
                Map<String, Object> providerSummary = new HashMap<>();
                providerSummary.put("total_attempts", total);
                providerSummary.put("successful", successful);
                providerSummary.put("failed", failed);
                if (total > 0) {
                    providerSummary.put("success_rate_percent", Math.round((double) successful / total * 100 * 10) / 10.0);
                } else {
                    providerSummary.put("success_rate_percent", 0.0);
                }
                summary.put(provider, providerSummary);
            }
            authStats.put("summary", summary);
        } catch (Exception e) {
            authStats.put("status", "error");
            authStats.put("error", e.getMessage());
        }
        status.put("authentication", authStats);

        // Base container features
        Map<String, Object> features = new HashMap<>();
        features.put("python_available", checkPythonAvailability());
        features.put("secrets_management", "ansible_vault + aws_parameter_store");
        features.put("logging", "integrated");
        status.put("base_container_features", features);

        // Service endpoints
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("health", "/api/health");
        endpoints.put("status", "/api/status");
        endpoints.put("calendar_v1", "/api/v1/calendar");
        endpoints.put("calendar_v2", "/api/v2/calendar");
        endpoints.put("astronomy_v1", "/api/v1/astronomy");
        endpoints.put("astronomy_v2", "/api/v2/astronomy");
        status.put("available_endpoints", endpoints);

        return Response.ok(status).build();
    }

    private boolean checkPythonAvailability() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "--version");
            Process process = pb.start();
            try {
                int exitCode = process.waitFor();
                return exitCode == 0;
            } finally {
                process.destroy();
            }
        } catch (java.io.IOException | InterruptedException e) {
            return false;
        }
    }
}
