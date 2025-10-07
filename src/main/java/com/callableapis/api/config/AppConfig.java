package com.callableapis.api.config;

import java.net.URI;
import java.util.Optional;

public final class AppConfig {
    private AppConfig() {}
    private static final String PUBLIC_BASE_URL = "https://api.callableapis.com";

    public static String getGithubClientId() {
        return requireEnv("GITHUB_CLIENT_ID");
    }

    public static String getGithubClientSecret() {
        return requireEnv("GITHUB_CLIENT_SECRET");
    }

    public static String getPublicBaseUrl() { return PUBLIC_BASE_URL; }

    public static String getApiKeySalt() {
        String v = System.getenv("API_KEY_SALT");
        if (v == null || v.isBlank()) {
            // default for dev/test only
            return "dev-salt";
        }
        return v;
    }

    public static int getRateLimitQps() {
        String value = Optional.ofNullable(System.getenv("API_RATE_LIMIT_QPS")).orElse("10");
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
                "&scope=" + urlEncode("read:user user:email") +
                "&state=" + urlEncode(state);
        return URI.create(authorize);
    }

    public static String getGithubCallbackUrl() {
        return getPublicBaseUrl() + "/auth/callback";
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
