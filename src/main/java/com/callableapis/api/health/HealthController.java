package com.callableapis.api.health;

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
@Path("/v1/health")
public class HealthController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Callable APIs Services");
        health.put("version", "1.0.0");
        health.put("timestamp", Instant.now().toString());
        health.put("container", "rl337/callableapis:services");
        health.put("java_version", System.getProperty("java.version"));
        health.put("tomcat_version", "9.0");

        return Response.ok(health).build();
    }
}
