package com.callableapis.api.handlers;

import com.callableapis.api.config.AppConfig;
import com.callableapis.api.security.ApiKeyService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Path("auth")
public class AuthResource {

    @GET
    @Path("/login")
    public Response login() {
        String state = UUID.randomUUID().toString();
        // In a real app, store the state in session/cookie; for simplicity we skip it
        URI redirect = AppConfig.getGithubAuthorizeUri(state);
        return Response.seeOther(redirect).build();
    }

    @GET
    @Path("/callback")
    @Produces(MediaType.APPLICATION_JSON)
    public Response callback(@QueryParam("code") String code, @QueryParam("state") String state) throws IOException, InterruptedException {
        if (code == null || code.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing code").build();
        }
        // Exchange code for access token
        String token = exchangeCodeForToken(code);
        if (token == null || token.isBlank()) {
            return Response.status(Response.Status.BAD_GATEWAY).entity("Token exchange failed").build();
        }
        // Fetch user identity from GitHub
        String login = fetchGithubLogin(token);
        if (login == null || login.isBlank()) {
            return Response.status(Response.Status.BAD_GATEWAY).entity("Failed to fetch user").build();
        }
        String identity = "github:" + login;
        String apiKey = ApiKeyService.getInstance().getOrCreateApiKeyForIdentity(identity);
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
                "&redirect_uri=" + urlEncode(AppConfig.getPublicBaseUrl() + "/auth/callback");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(AppConfig.getGithubTokenUri())
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            return null;
        }
        // naive parse; could use JSON-B but keep minimal
        String json = response.body();
        String token = extractJsonString(json, "access_token");
        return token;
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
        return extractJsonString(json, "login");
    }

    private static String urlEncode(String v) {
        return java.net.URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    private static String extractJsonString(String json, String key) {
        // Minimal JSON parsing to avoid extra deps: "key":"value"
        String quotedKey = "\"" + key + "\"";
        int idx = json.indexOf(quotedKey);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        if (colon < 0) return null;
        int startQuote = json.indexOf('"', colon + 1);
        if (startQuote < 0) return null;
        int endQuote = json.indexOf('"', startQuote + 1);
        if (endQuote < 0) return null;
        return json.substring(startQuote + 1, endQuote);
    }
}
