package com.callableapis.api.web;

import com.callableapis.api.config.VersionService;
import com.callableapis.api.security.ApiKeyStore;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class AuthenticatedServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AuthenticatedServlet.class.getName());
    
    @Inject
    private ApiKeyStore apiKeyStore;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        logger.info("AuthenticatedServlet.doGet() called!");
        
        String identity = request.getParameter("identity");
        String apiKey = request.getParameter("apiKey");
        
        logger.info("Parameters - identity: " + identity + ", apiKey: " + (apiKey != null ? "***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) : "null"));
        
        if (identity == null || identity.isBlank() || apiKey == null || apiKey.isBlank()) {
            logger.warning("Missing identity or apiKey parameters, redirecting to home");
            response.sendRedirect("/");
            return;
        }
        
        // Verify the API key belongs to this identity by checking if it maps back to the identity
        Optional<String> storedIdentity = apiKeyStore.findIdentityByApiKey(apiKey);
        if (storedIdentity.isEmpty() || !storedIdentity.get().equals(identity)) {
            logger.warning("Invalid API key for identity: " + identity);
            response.sendRedirect("/");
            return;
        }
        
        // Add version information to request attributes for JSP
        VersionService versionService = VersionService.getInstance();
        String gitCommitHash = versionService.getGitCommitHash();
        String shortCommitHash = versionService.getShortCommitHash();
        String buildTime = versionService.getBuildTime();
        
        logger.info("Version info - Commit: " + gitCommitHash + ", Build: " + buildTime);
        
        request.setAttribute("gitCommitHash", gitCommitHash);
        request.setAttribute("shortCommitHash", shortCommitHash);
        request.setAttribute("buildTime", buildTime);
        request.setAttribute("userIdentity", identity);
        request.setAttribute("apiKey", apiKey);
        
        // Forward to JSP
        request.getRequestDispatcher("/authenticated.jsp").forward(request, response);
    }
}
