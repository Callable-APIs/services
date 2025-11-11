package com.callableapis.api.config;

import com.callableapis.api.secrets.VaultSecretsManager;
import java.net.URI;
import java.util.logging.Logger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class AppConfig {
    private AppConfig() {
    }

    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private static final String PUBLIC_BASE_URL = "https://api.callableapis.com";
    private static volatile ParameterStoreService parameterStore;
    private static volatile VaultSecretsManager vaultSecretsManager;

    // Lazy initialization to avoid blocking during class loading (especially in tests)
    private static ParameterStoreService getParameterStore() {
        if (parameterStore == null) {
            synchronized (AppConfig.class) {
                if (parameterStore == null) {
                    try {
                        parameterStore = ParameterStoreService.getInstance();
                        logger.info("ParameterStoreService instance obtained successfully");
                    } catch (Exception e) {
                        logger.warning("Failed to initialize ParameterStoreService: " + e.getMessage());
                        // In test environments, this is expected - will use environment variable fallbacks
                        throw new RuntimeException("ParameterStoreService initialization failed", e);
                    }
                }
            }
        }
        return parameterStore;
    }

    private static VaultSecretsManager getVaultSecretsManagerInstance() {
        if (vaultSecretsManager == null) {
            synchronized (AppConfig.class) {
                if (vaultSecretsManager == null) {
                    try {
                        vaultSecretsManager = new VaultSecretsManager();
                        logger.info("VaultSecretsManager initialized successfully");
                    } catch (Exception e) {
                        logger.warning("Failed to initialize VaultSecretsManager: " + e.getMessage());
                        // Return null to allow fallback to ParameterStoreService
                        return null;
                    }
                }
            }
        }
        return vaultSecretsManager;
    }

    public static String getGithubClientId() {
        // Try VaultSecretsManager first (Ansible Vault -> AWS Parameter Store)
        VaultSecretsManager vaultManager = getVaultSecretsManagerInstance();
        String value = null;
        if (vaultManager != null) {
            value = vaultManager.getGitHubClientId();
        }

        // Fallback to original ParameterStoreService if VaultSecretsManager fails
        if (value == null) {
            value = getParameterStore().getParameterWithEnvFallback(
                    "/callableapis/github/client-id",
                    "GITHUB_CLIENT_ID",
                    "dev-client-id-placeholder");
        }

        logger.info("GitHub Client ID: "
                + (value != null ? "***" + value.substring(Math.max(0, value.length() - 4)) : "null"));
        return value;
    }

    public static String getGithubClientSecret() {
        // Try VaultSecretsManager first (Ansible Vault -> AWS Parameter Store)
        VaultSecretsManager vaultManager = getVaultSecretsManagerInstance();
        String value = null;
        if (vaultManager != null) {
            value = vaultManager.getGitHubClientSecret();
        }

        // Fallback to original ParameterStoreService if VaultSecretsManager fails
        if (value == null) {
            value = getParameterStore().getParameterWithEnvFallback(
                    "/callableapis/github/client-secret",
                    "GITHUB_CLIENT_SECRET",
                    "dev-client-secret-placeholder");
        }

        logger.info("GitHub Client Secret: "
                + (value != null ? "***" + value.substring(Math.max(0, value.length() - 4)) : "null"));
        return value;
    }

    public static String getGithubOAuthScope() {
        String value = getParameterStore().getParameterWithEnvFallback(
                "/callableapis/github/oauth-scope",
                "GITHUB_OAUTH_SCOPE",
                "read:user user:email");
        logger.info("GitHub OAuth Scope: " + value);
        return value;
    }

    public static String getGithubCallbackUrl() {
        // Try VaultSecretsManager first (Ansible Vault -> AWS Parameter Store)
        VaultSecretsManager vaultManager = getVaultSecretsManagerInstance();
        String redirectUri = null;
        if (vaultManager != null) {
            redirectUri = vaultManager.getGitHubRedirectUri();
        }

        // Fallback to original ParameterStoreService if VaultSecretsManager fails
        if (redirectUri == null) {
            redirectUri = getParameterStore().getParameterWithEnvFallback(
                    "/callableapis/github/redirect-uri",
                    "GITHUB_REDIRECT_URI",
                    getPublicBaseUrl() + "/api/auth/callback");
        }

        return redirectUri;
    }

    public static String getPublicBaseUrl() {
        return PUBLIC_BASE_URL;
    }

    /**
     * Get the VaultSecretsManager instance for advanced operations.
     */
    @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "VaultSecretsManager is intentionally exposed for advanced operations")
    public static VaultSecretsManager getVaultSecretsManager() {
        return getVaultSecretsManagerInstance();
    }

    /**
     * Get a summary of the secrets management status.
     */
    public static String getSecretsStatusSummary() {
        VaultSecretsManager vaultManager = getVaultSecretsManagerInstance();
        if (vaultManager != null) {
            return vaultManager.getStatusSummary();
        }
        return "VaultSecretsManager not available - using ParameterStoreService fallback";
    }

    public static String getApiKeySalt() {
        return getParameterStore().getParameterWithEnvFallback(
                "/callableapis/api/key-salt",
                "API_KEY_SALT",
                "dev-salt");
    }

    public static int getRateLimitQps() {
        String value = getParameterStore().getParameterWithEnvFallback(
                "/callableapis/api/rate-limit-qps",
                "API_RATE_LIMIT_QPS",
                "10");
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

    private static String urlEncode(String v) {
        try {
            return java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Should never happen with UTF-8
            return v;
        }
    }
}
