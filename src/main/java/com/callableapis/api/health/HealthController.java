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
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Callable APIs Services");
        String gitCommit = VersionService.getInstance().getShortCommitHash();
        health.put("version", "1.0.0-" + gitCommit);
        health.put("timestamp", Instant.now().toString());
        health.put("container", "rl337/callableapis:services");
        health.put("java_version", System.getProperty("java.version"));
        health.put("tomcat_version", "10.1.18");

        return Response.ok(health).build();
    }
}
