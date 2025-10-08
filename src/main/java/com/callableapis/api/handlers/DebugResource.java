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
        System.out.println("=== DebugResource.getConfig() called ===");
        Map<String, Object> config = new HashMap<>();
        
        try {
            // Test Parameter Store access
            System.out.println("Getting ParameterStoreService instance...");
            ParameterStoreService paramStore = ParameterStoreService.getInstance();
            System.out.println("ParameterStoreService instance obtained");
            
            System.out.println("Testing configuration values...");
            String clientId = AppConfig.getGithubClientId();
            String clientSecret = AppConfig.getGithubClientSecret();
            String oauthScope = AppConfig.getGithubOAuthScope();
            String callbackUrl = AppConfig.getGithubCallbackUrl();
            String apiKeySalt = AppConfig.getApiKeySalt();
            int rateLimitQps = AppConfig.getRateLimitQps();
            
            System.out.println("Configuration values retrieved:");
            System.out.println("- Client ID: " + (clientId != null ? "***" + clientId.substring(Math.max(0, clientId.length() - 4)) : "NULL"));
            System.out.println("- Client Secret: " + (clientSecret != null ? "***" + clientSecret.substring(Math.max(0, clientSecret.length() - 4)) : "NULL"));
            System.out.println("- OAuth Scope: " + oauthScope);
            System.out.println("- Callback URL: " + callbackUrl);
            System.out.println("- API Key Salt: " + (apiKeySalt != null ? "***" + apiKeySalt.substring(Math.max(0, apiKeySalt.length() - 4)) : "NULL"));
            System.out.println("- Rate Limit QPS: " + rateLimitQps);
            
            config.put("github_client_id", clientId != null ? "***" + clientId.substring(Math.max(0, clientId.length() - 4)) : "null");
            config.put("github_client_secret", clientSecret != null ? "***" + clientSecret.substring(Math.max(0, clientSecret.length() - 4)) : "null");
            config.put("github_oauth_scope", oauthScope);
            config.put("github_callback_url", callbackUrl);
            config.put("api_key_salt", apiKeySalt != null ? "***" + apiKeySalt.substring(Math.max(0, apiKeySalt.length() - 4)) : "null");
            config.put("rate_limit_qps", rateLimitQps);
            
            // Test direct Parameter Store access
            System.out.println("Testing direct Parameter Store access...");
            String paramStoreTest = paramStore.getParameter("/callableapis/github/oauth-scope", "FAILED");
            System.out.println("Parameter Store test result: " + paramStoreTest);
            config.put("param_store_test", paramStoreTest);
            
            // Environment variables
            String envClientId = System.getenv("GITHUB_CLIENT_ID");
            String envOauthScope = System.getenv("GITHUB_OAUTH_SCOPE");
            System.out.println("Environment variables:");
            System.out.println("- GITHUB_CLIENT_ID: " + (envClientId != null ? "***" + envClientId.substring(Math.max(0, envClientId.length() - 4)) : "null"));
            System.out.println("- GITHUB_OAUTH_SCOPE: " + envOauthScope);
            
            config.put("env_github_client_id", envClientId != null ? "***" + envClientId.substring(Math.max(0, envClientId.length() - 4)) : "null");
            config.put("env_github_oauth_scope", envOauthScope);
            
            System.out.println("=== DebugResource.getConfig() completed successfully ===");
            return Response.ok(config).build();
            
        } catch (Exception e) {
            System.out.println("ERROR in DebugResource.getConfig(): " + e.getMessage());
            e.printStackTrace();
            config.put("error", e.getMessage());
            config.put("error_type", e.getClass().getSimpleName());
            config.put("stack_trace", e.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(config).build();
        }
    }
    
    @GET
    @Path("/oauth-url")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getOAuthUrl() {
        System.out.println("=== DebugResource.getOAuthUrl() called ===");
        try {
            String oauthUrl = AppConfig.getGithubAuthorizeUri("test-state").toString();
            System.out.println("Generated OAuth URL: " + oauthUrl);
            return Response.ok(oauthUrl).build();
        } catch (Exception e) {
            System.out.println("ERROR generating OAuth URL: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generating OAuth URL: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/aws-sdk")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testAwsSdk() {
        System.out.println("=== DebugResource.testAwsSdk() called ===");
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test AWS SDK classes availability
            Class<?> ssmClientClass = Class.forName("software.amazon.awssdk.services.ssm.SsmClient");
            result.put("ssm_client_class", "Available");
            
            Class<?> authClass = Class.forName("software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider");
            result.put("auth_class", "Available");
            
            // Test ParameterStoreService
            ParameterStoreService paramStore = ParameterStoreService.getInstance();
            result.put("parameter_store_service", "Available");
            
            // Test a simple parameter retrieval
            String testResult = paramStore.getParameter("/callableapis/github/oauth-scope", "FALLBACK_VALUE");
            result.put("parameter_test", testResult);
            
            System.out.println("AWS SDK test completed successfully");
            return Response.ok(result).build();
            
        } catch (ClassNotFoundException e) {
            System.out.println("AWS SDK class not found: " + e.getMessage());
            result.put("error", "AWS SDK class not found: " + e.getMessage());
            result.put("error_type", "ClassNotFoundException");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        } catch (Exception e) {
            System.out.println("ERROR testing AWS SDK: " + e.getMessage());
            e.printStackTrace();
            result.put("error", e.getMessage());
            result.put("error_type", e.getClass().getSimpleName());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
    }
}
