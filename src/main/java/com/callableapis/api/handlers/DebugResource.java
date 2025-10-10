package com.callableapis.api.handlers;

import com.callableapis.api.config.AppConfig;
import com.callableapis.api.config.ParameterStoreService;
import com.callableapis.api.time.AstronomyService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
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
			
			// Check if Parameter Store is available
			boolean paramStoreAvailable = paramStore.isParameterStoreAvailable();
			config.put("parameter_store_available", paramStoreAvailable);
			config.put("parameter_store_status", paramStoreAvailable ? "Using Parameter Store" : "Using fallback values only (no AWS credentials)");

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
	
	@GET
	@Path("/intensity-test")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testIntensity() {
		logger.info("=== DebugResource.testIntensity() called ===");
		Map<String, Object> result = new HashMap<>();
		
		try {
			// Test solar intensity at noon on equator (should be close to 1.0)
			ZonedDateTime noonEquator = ZonedDateTime.of(2024, 6, 21, 12, 0, 0, 0, ZoneOffset.UTC);
			AstronomyService astronomyService = new AstronomyService();
			AstronomyService.SolarInfoResult solar = astronomyService.computeSolarInfo(noonEquator, 0.0, 0.0);
			
			// Test moonlight intensity at full moon - try different times to find when moon is visible
			ZonedDateTime fullMoon = ZonedDateTime.of(2024, 6, 21, 18, 0, 0, 0, ZoneOffset.UTC); // 6 PM
			AstronomyService.MoonlightInfoResult moonlight = astronomyService.computeMoonlightInfo(fullMoon, 0.0, 0.0);
			
			// Also test at a different time when moon might be higher
			ZonedDateTime fullMoon2 = ZonedDateTime.of(2024, 6, 21, 6, 0, 0, 0, ZoneOffset.UTC); // 6 AM
			AstronomyService.MoonlightInfoResult moonlight2 = astronomyService.computeMoonlightInfo(fullMoon2, 0.0, 0.0);
			
			result.put("solar_intensity_noon_equator", solar.intensity);
			result.put("solar_elevation_noon_equator", solar.elevationDeg);
			result.put("moonlight_intensity_6pm", moonlight.intensity);
			result.put("moonlight_elevation_6pm", moonlight.elevationDeg);
			result.put("moonlight_intensity_6am", moonlight2.intensity);
			result.put("moonlight_elevation_6am", moonlight2.elevationDeg);
			result.put("moon_illumination", moonlight.illumination);
			
			// Calculate ratios for both times
			double ratio1 = solar.intensity > 0 ? moonlight.intensity / solar.intensity : 0;
			double ratio2 = solar.intensity > 0 ? moonlight2.intensity / solar.intensity : 0;
			result.put("moonlight_to_solar_ratio_6pm", ratio1);
			result.put("moonlight_to_solar_ratio_6am", ratio2);
			
			logger.info("Intensity test completed successfully");
			return Response.ok(result).build();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ERROR testing intensity: " + e.getMessage(), e);
			result.put("error", e.getMessage());
			result.put("error_type", e.getClass().getSimpleName());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
	}
	
	@GET
	@Path("/clear-cache")
	@Produces(MediaType.APPLICATION_JSON)
	public Response clearCache() {
		logger.info("=== DebugResource.clearCache() called ===");
		Map<String, Object> result = new HashMap<>();
		
		try {
			ParameterStoreService paramStore = ParameterStoreService.getInstance();
			
			// Clear cache for OAuth-related parameters
			paramStore.clearCache("/callableapis/github/redirect-uri");
			paramStore.clearCache("/callableapis/github/client-id");
			paramStore.clearCache("/callableapis/github/client-secret");
			paramStore.clearCache("/callableapis/github/oauth-scope");
			
			result.put("status", "success");
			result.put("message", "Cache cleared for OAuth parameters");
			result.put("cleared_parameters", new String[]{
				"/callableapis/github/redirect-uri",
				"/callableapis/github/client-id", 
				"/callableapis/github/client-secret",
				"/callableapis/github/oauth-scope"
			});
			
			logger.info("Cache cleared successfully");
			return Response.ok(result).build();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ERROR clearing cache: " + e.getMessage(), e);
			result.put("error", e.getMessage());
			result.put("error_type", e.getClass().getSimpleName());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
	}
}
