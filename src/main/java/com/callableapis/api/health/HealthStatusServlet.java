package com.callableapis.api.health;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Servlet to handle /health endpoint at root level.
 * Provides simple JSON response for load balancer health checks.
 */
public class HealthStatusServlet extends HttpServlet {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressFBWarnings(value = "DM_SYSTEM_GETPROPERTY", justification = "System.getProperty is safe for reading system properties")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Callable APIs Services");
        health.put("version", "1.0.0-" + com.callableapis.api.config.VersionService.getInstance().getShortCommitHash());
        health.put("timestamp", java.time.Instant.now().toString());
        health.put("container", "rl337/callableapis:services");
        health.put("java_version", System.getProperty("java.version"));
        health.put("tomcat_version", "10.1.18");
        
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), health);
    }
}
