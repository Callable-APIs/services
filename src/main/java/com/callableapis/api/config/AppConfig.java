package com.callableapis.api.config;

import java.net.URI;
import java.util.logging.Logger;

public final class AppConfig {
    private AppConfig() {}
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private static final String PUBLIC_BASE_URL = "https://api.callableapis.com";
    private static final ParameterStoreService parameterStore;
    
    static {
        logger.info("=== AppConfig Static Initialization Started ===");
        try {
            parameterStore = ParameterStoreService.getInstance();
            logger.info("ParameterStoreService instance obtained successfully");
            
            // Test configuration loading immediately
            logger.info("Testing configuration loading...");
            String clientId = getGithubClientId();
            String clientSecret = getGithubClientSecret();
            String oauthScope = getGithubOAuthScope();
            String callbackUrl = getGithubCallbackUrl();
            
            logger.info("Configuration test results:");
            logger.info("- GitHub Client ID: " + (clientId != null ? "***" + clientId.substring(Math.max(0, clientId.length() - 4)) : "NULL"));
            logger.info("- GitHub Client Secret: " + (clientSecret != null ? "***" + clientSecret.substring(Math.max(0, clientSecret.length() - 4)) : "NULL"));
            logger.info("- GitHub OAuth Scope: " + oauthScope);
            logger.info("- GitHub Callback URL: " + callbackUrl);
            
            logger.info("=== AppConfig Static Initialization Completed Successfully ===");
        } catch (Exception e) {
            logger.severe("=== AppConfig Static Initialization FAILED ===");
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("AppConfig initialization failed", e);
        }
    }

    public static String getGithubClientId() {
        String value = parameterStore.getParameterWithEnvFallback(
            "/callableapis/github/client-id", 
            "GITHUB_CLIENT_ID", 
            "dev-client-id-placeholder"
        );
        logger.info("GitHub Client ID: " + (value != null ? "***" + value.substring(Math.max(0, value.length() - 4)) : "null"));
        return value;
    }

    public static String getGithubClientSecret() {
        String value = parameterStore.getParameterWithEnvFallback(
            "/callableapis/github/client-secret", 
            "GITHUB_CLIENT_SECRET", 
            "dev-client-secret-placeholder"
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
            getPublicBaseUrl() + "/api/auth/callback"
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
