package com.callableapis.api.handlers;

import com.callableapis.api.config.AppConfig;
import com.callableapis.api.security.ApiKeyStore;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/auth")
public class AuthResource {
	private static final Logger logger = Logger.getLogger(AuthResource.class.getName());
	@Inject
	private ApiKeyStore apiKeyStore;

	@GET
	@Path("/login")
	public Response login() {
		logger.info("=== AuthResource.login() called ===");
		try {
			String state = UUID.randomUUID().toString();
			logger.info("Generated state: " + state);

			String clientId = AppConfig.getGithubClientId();
			String clientSecret = AppConfig.getGithubClientSecret();
			String oauthScope = AppConfig.getGithubOAuthScope();
			String callbackUrl = AppConfig.getGithubCallbackUrl();

			logger.info("Configuration values:");
			logger.info("- Client ID: " + (clientId != null ? "***" + clientId.substring(Math.max(0, clientId.length() - 4)) : "NULL"));
			logger.info("- Client Secret: " + (clientSecret != null ? "***" + clientSecret.substring(Math.max(0, clientSecret.length() - 4)) : "NULL"));
			logger.info("- OAuth Scope: " + oauthScope);
			logger.info("- Callback URL: " + callbackUrl);

			if (clientId == null || clientId.isBlank()) {
				logger.severe("ERROR: GitHub Client ID is null or blank!");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("GitHub Client ID is not configured")
						.build();
			}

			URI redirect = AppConfig.getGithubAuthorizeUri(state);
			logger.info("Generated OAuth URL: " + redirect.toString());
			return Response.seeOther(redirect).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ERROR in AuthResource.login(): " + e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("OAuth configuration error: " + e.getMessage())
					.build();
		}
	}

	@GET
	@Path("/callback")
	@Produces(MediaType.APPLICATION_JSON)
	public Response callback(@QueryParam("code") String code, @QueryParam("state") String state) throws IOException, InterruptedException {
		if (code == null || code.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Missing code").build();
		}
		String token = exchangeCodeForToken(code);
		if (token == null || token.isBlank()) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Token exchange failed").build();
		}
		String login = fetchGithubLogin(token);
		if (login == null || login.isBlank()) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Failed to fetch user").build();
		}
		String identity = "github:" + login;
		String apiKey = apiKeyStore.getOrCreateApiKeyForIdentity(identity);
		return Response.ok(Map.of(
				"identity", identity,
				"apiKey", apiKey
		)).build();
	}

	private String exchangeCodeForToken(String code) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String body = "client_id=" + urlEncode(AppConfig.getGithubClientId()) +
				"&client_secret=" + urlEncode(AppConfig.getGithubClientSecret()) +
				"&code=" + urlEncode(code) +
				"&redirect_uri=" + urlEncode(AppConfig.getGithubCallbackUrl());
		HttpRequest request = HttpRequest.newBuilder()
				.uri(AppConfig.getGithubTokenUri())
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() / 100 != 2) {
			return null;
		}
		String json = response.body();
		TokenResponse tokenResponse = parseJson(json, TokenResponse.class);
		return tokenResponse != null ? tokenResponse.accessToken : null;
	}

	private String fetchGithubLogin(String accessToken) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(AppConfig.getGithubUserApiUri())
				.header("Accept", "application/json")
				.header("Authorization", "Bearer " + accessToken)
				.GET()
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() / 100 != 2) {
			return null;
		}
		String json = response.body();
		GithubUser user = parseJson(json, GithubUser.class);
		return user != null ? user.login : null;
	}

	private static String urlEncode(String v) {
		return java.net.URLEncoder.encode(v, StandardCharsets.UTF_8);
	}

	private static <T> T parseJson(String json, Class<T> type) {
		try (Jsonb jsonb = JsonbBuilder.create()) {
			return jsonb.fromJson(json, type);
		} catch (Exception ex) {
			return null;
		}
	}

	public static class TokenResponse {
		@JsonbProperty("access_token")
		public String accessToken;
	}

	public static class GithubUser {
		public String login;
	}
}
