package com.callableapis.api.web;

import com.callableapis.api.config.VersionService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/")
public class RootResource {
    private static final Logger logger = Logger.getLogger(RootResource.class.getName());

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
    public Response getRoot() {
        logger.info("RootResource.getRoot() called!");

        // Check if client accepts JSON (compliance requirement)
        // If Accept header includes application/json, return JSON
        // Otherwise, redirect to HTML page for backward compatibility
        
        // For compliance, return JSON by default
        // HTML can be accessed via /index or /docs
        Map<String, Object> response = new HashMap<>();
        VersionService versionService = VersionService.getInstance();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        response.put("service", "CallableAPIs Services Container");
        response.put("version", versionService.getFullVersionString());
        response.put("status", "running");
        response.put("uptime", formatUptime(runtimeBean.getUptime()));
        response.put("timestamp", Instant.now().toString());
        
        return Response.ok(response).type(MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * Convert uptime in milliseconds to human-readable format.
     */
    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0) {
            return String.format("%d day, %d:%02d:%02d", days, hours, minutes, secs);
        } else {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        }
    }
}
