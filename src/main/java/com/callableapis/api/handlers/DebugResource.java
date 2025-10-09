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
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/debug")
public class DebugResource {
	private static final Logger logger = Logger.getLogger(DebugResource.class.getName());

	@GET
	@Path("/config")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConfig() {
		logger.info("=== DebugResource.getConfig() called ===");
		Map<String, Object> config = new HashMap<>();
		try {
			logger.info("Getting ParameterStoreService instance...");
			ParameterStoreService paramStore = ParameterStoreService.getInstance();
			logger.info("ParameterStoreService instance obtained");

			logger.info("Testing configuration values...");
			String clientId = AppConfig.getGithubClientId();
			String clientSecret = AppConfig.getGithubClientSecret();
			String oauthScope = AppConfig.getGithubOAuthScope();
			String callbackUrl = AppConfig.getGithubCallbackUrl();
			String apiKeySalt = AppConfig.getApiKeySalt();
			int rateLimitQps = AppConfig.getRateLimitQps();

			logger.info("Configuration values retrieved:");
			logger.info("- Client ID: " + (clientId != null ? "***" + clientId.substring(Math.max(0, clientId.length() - 4)) : "NULL"));
			logger.info("- Client Secret: " + (clientSecret != null ? "***" + clientSecret.substring(Math.max(0, clientSecret.length() - 4)) : "NULL"));
			logger.info("- OAuth Scope: " + oauthScope);
			logger.info("- Callback URL: " + callbackUrl);
			logger.info("- API Key Salt: " + (apiKeySalt != null ? "***" + apiKeySalt.substring(Math.max(0, apiKeySalt.length() - 4)) : "NULL"));
			logger.info("- Rate Limit QPS: " + rateLimitQps);

			config.put("github_client_id", clientId != null ? "***" + clientId.substring(Math.max(0, clientId.length() - 4)) : "null");
			config.put("github_client_secret", clientSecret != null ? "***" + clientSecret.substring(Math.max(0, clientSecret.length() - 4)) : "null");
			config.put("github_oauth_scope", oauthScope);
			config.put("github_callback_url", callbackUrl);
			config.put("api_key_salt", apiKeySalt != null ? "***" + apiKeySalt.substring(Math.max(0, apiKeySalt.length() - 4)) : "null");
			config.put("rate_limit_qps", rateLimitQps);

			logger.info("Testing direct Parameter Store access...");
			String paramStoreTest = paramStore.getParameter("/callableapis/github/oauth-scope", "FAILED");
			logger.info("Parameter Store test result: " + paramStoreTest);
			config.put("param_store_test", paramStoreTest);

			String envClientId = System.getenv("GITHUB_CLIENT_ID");
			String envOauthScope = System.getenv("GITHUB_OAUTH_SCOPE");
			logger.info("Environment variables:");
			logger.info("- GITHUB_CLIENT_ID: " + (envClientId != null ? "***" + envClientId.substring(Math.max(0, envClientId.length() - 4)) : "null"));
			logger.info("- GITHUB_OAUTH_SCOPE: " + envOauthScope);

			config.put("env_github_client_id", envClientId != null ? "***" + envClientId.substring(Math.max(0, envClientId.length() - 4)) : "null");
			config.put("env_github_oauth_scope", envOauthScope);

			logger.info("=== DebugResource.getConfig() completed successfully ===");
			return Response.ok(config).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ERROR in DebugResource.getConfig(): " + e.getMessage(), e);
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
		logger.info("=== DebugResource.getOAuthUrl() called ===");
		try {
			String oauthUrl = AppConfig.getGithubAuthorizeUri("test-state").toString();
			logger.info("Generated OAuth URL: " + oauthUrl);
			return Response.ok(oauthUrl).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ERROR generating OAuth URL: " + e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error generating OAuth URL: " + e.getMessage())
					.build();
		}
	}

	@GET
	@Path("/aws-sdk")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAwsSdk() {
		logger.info("=== DebugResource.testAwsSdk() called ===");
		Map<String, Object> result = new HashMap<>();
		try {
			Class.forName("software.amazon.awssdk.services.ssm.SsmClient");
			result.put("ssm_client_class", "Available");

			Class.forName("software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider");
			result.put("auth_class", "Available");

			ParameterStoreService paramStore = ParameterStoreService.getInstance();
			result.put("parameter_store_service", "Available");

			String testResult = paramStore.getParameter("/callableapis/github/oauth-scope", "FALLBACK_VALUE");
			result.put("parameter_test", testResult);

			logger.info("AWS SDK test completed successfully");
			return Response.ok(result).build();
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "AWS SDK class not found: " + e.getMessage(), e);
			result.put("error", "AWS SDK class not found: " + e.getMessage());
			result.put("error_type", "ClassNotFoundException");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ERROR testing AWS SDK: " + e.getMessage(), e);
			result.put("error", e.getMessage());
			result.put("error_type", e.getClass().getSimpleName());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
	}
}
