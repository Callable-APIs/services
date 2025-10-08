package com.callableapis.api.handlers;

import com.callableapis.api.config.AppConfig;
import com.callableapis.api.config.ParameterStoreService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("debug")
public class DebugResource {

    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig() {
        Map<String, Object> config = new HashMap<>();
        
        try {
            // Test Parameter Store access
            ParameterStoreService paramStore = ParameterStoreService.getInstance();
            
            config.put("github_client_id", AppConfig.getGithubClientId() != null ? "***" + AppConfig.getGithubClientId().substring(Math.max(0, AppConfig.getGithubClientId().length() - 4)) : "null");
            config.put("github_client_secret", AppConfig.getGithubClientSecret() != null ? "***" + AppConfig.getGithubClientSecret().substring(Math.max(0, AppConfig.getGithubClientSecret().length() - 4)) : "null");
            config.put("github_oauth_scope", AppConfig.getGithubOAuthScope());
            config.put("github_callback_url", AppConfig.getGithubCallbackUrl());
            config.put("api_key_salt", AppConfig.getApiKeySalt() != null ? "***" + AppConfig.getApiKeySalt().substring(Math.max(0, AppConfig.getApiKeySalt().length() - 4)) : "null");
            config.put("rate_limit_qps", AppConfig.getRateLimitQps());
            
            // Test direct Parameter Store access
            config.put("param_store_test", paramStore.getParameter("/callableapis/github/oauth-scope", "FAILED"));
            
            // Environment variables
            config.put("env_github_client_id", System.getenv("GITHUB_CLIENT_ID") != null ? "***" + System.getenv("GITHUB_CLIENT_ID").substring(Math.max(0, System.getenv("GITHUB_CLIENT_ID").length() - 4)) : "null");
            config.put("env_github_oauth_scope", System.getenv("GITHUB_OAUTH_SCOPE"));
            
            return Response.ok(config).build();
            
        } catch (Exception e) {
            config.put("error", e.getMessage());
            config.put("error_type", e.getClass().getSimpleName());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(config).build();
        }
    }
    
    @GET
    @Path("/oauth-url")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getOAuthUrl() {
        try {
            String oauthUrl = AppConfig.getGithubAuthorizeUri("test-state").toString();
            return Response.ok(oauthUrl).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generating OAuth URL: " + e.getMessage())
                    .build();
        }
    }
}
