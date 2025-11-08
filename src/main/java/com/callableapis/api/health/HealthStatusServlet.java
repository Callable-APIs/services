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
        
        // Compliance: Return only status, timestamp, and version
        Map<String, String> health = new HashMap<>();
        health.put("status", "healthy");  // Changed from "UP" to "healthy"
        health.put("timestamp", java.time.Instant.now().toString());
        health.put("version", com.callableapis.api.config.VersionService.getInstance().getFullVersionString());
        
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), health);
    }
}
