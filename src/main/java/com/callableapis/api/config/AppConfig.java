package com.callableapis.api.config;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

public final class AppConfig {
    private AppConfig() {}
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private static final String PUBLIC_BASE_URL = "https://api.callableapis.com";
    private static final ParameterStoreService parameterStore;
    
    static {
        logger.info("Initializing AppConfig...");
        parameterStore = ParameterStoreService.getInstance();
        logger.info("AppConfig initialized successfully");
    }

    public static String getGithubClientId() {
        String value = parameterStore.getParameterWithEnvFallback(
            "/callableapis/github/client-id", 
            "GITHUB_CLIENT_ID", 
            null
        );
        logger.info("GitHub Client ID: " + (value != null ? "***" + value.substring(Math.max(0, value.length() - 4)) : "null"));
        return value;
    }

    public static String getGithubClientSecret() {
        String value = parameterStore.getParameterWithEnvFallback(
            "/callableapis/github/client-secret", 
            "GITHUB_CLIENT_SECRET", 
            null
        );
        logger.info("GitHub Client Secret: " + (value != null ? "***" + value.substring(Math.max(0, value.length() - 4)) : "null"));
        return value;
    }

    public static String getGithubOAuthScope() {
        String value = parameterStore.getParameterWithEnvFallback(
            "/callableapis/github/oauth-scope", 
            "GITHUB_OAUTH_SCOPE", 
            "read:user user:email"
        );
        logger.info("GitHub OAuth Scope: " + value);
        return value;
    }

    public static String getGithubCallbackUrl() {
        String redirectUri = parameterStore.getParameterWithEnvFallback(
            "/callableapis/github/redirect-uri", 
            "GITHUB_REDIRECT_URI", 
            getPublicBaseUrl() + "/auth/callback"
        );
        return redirectUri;
    }

    public static String getPublicBaseUrl() { return PUBLIC_BASE_URL; }

    public static String getApiKeySalt() {
        return parameterStore.getParameterWithEnvFallback(
            "/callableapis/api/key-salt", 
            "API_KEY_SALT", 
            "dev-salt"
        );
    }

    public static int getRateLimitQps() {
        String value = parameterStore.getParameterWithEnvFallback(
            "/callableapis/api/rate-limit-qps", 
            "API_RATE_LIMIT_QPS", 
            "10"
        );
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 10;
        }
    }

    public static URI getGithubAuthorizeUri(String state) {
        String authorize = "https://github.com/login/oauth/authorize" +
                "?client_id=" + urlEncode(getGithubClientId()) +
                "&redirect_uri=" + urlEncode(getGithubCallbackUrl()) +
                "&scope=" + urlEncode(getGithubOAuthScope()) +
                "&state=" + urlEncode(state);
        return URI.create(authorize);
    }

    public static URI getGithubTokenUri() {
        return URI.create("https://github.com/login/oauth/access_token");
    }

    public static URI getGithubUserApiUri() {
        return URI.create("https://api.github.com/user");
    }

    private static String requireEnv(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return v;
    }

    private static String urlEncode(String v) {
        try {
            return java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Should never happen with UTF-8
            return v;
        }
    }
}
