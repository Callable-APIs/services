package com.callableapis.api.health;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import com.callableapis.api.config.VersionService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for the Callable APIs services container.
 * Implements the health endpoints as recommended by the infrastructure agent.
 */
@Path("/health")
public class HealthController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressFBWarnings(value = "DM_SYSTEM_GETPROPERTY", justification = "System.getProperty is safe for reading system properties")
    public Response getHealth() {
        // Compliance: /api/health should return "ok" status (different from /health which returns "healthy")
        // Return only status, timestamp, and version
        Map<String, String> health = new HashMap<>();
        health.put("status", "ok");
        health.put("timestamp", Instant.now().toString());
        health.put("version", VersionService.getInstance().getFullVersionString());

        return Response.ok(health).build();
    }
}
