package com.callableapis.api.config;

import com.callableapis.api.secrets.VaultSecretsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Configuration class for managing secrets loading and validation.
 * This class ensures that all required secrets are available at startup.
 */
public class SecretsConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecretsConfig.class);

    private final VaultSecretsManager vaultSecretsManager;

    private String githubClientId;
    private String githubClientSecret;
    private String githubRedirectUri;

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", 
                       justification = "Constructor intentionally throws exception for configuration validation")
    public SecretsConfig() {
        this.vaultSecretsManager = new VaultSecretsManager();
        initializeSecrets();
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", 
                       justification = "VaultSecretsManager is intentionally shared for testing")
    public SecretsConfig(VaultSecretsManager vaultSecretsManager) {
        this.vaultSecretsManager = vaultSecretsManager;
        // Don't initialize in constructor for testing
    }
    
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", 
                       justification = "VaultSecretsManager is intentionally shared for testing")
    public SecretsConfig(VaultSecretsManager vaultSecretsManager, boolean initialize) {
        this.vaultSecretsManager = vaultSecretsManager;
        if (initialize) {
            try {
                initializeSecrets();
            } catch (Exception e) {
                // Log error but don't throw from constructor
                logger.error("Failed to initialize secrets in constructor: " + e.getMessage());
            }
        }
    }

    public void initializeSecrets() {
        logger.info("Initializing secrets configuration...");

        // Load secrets from VaultSecretsManager
        this.githubClientId = vaultSecretsManager.getGitHubClientId();
        this.githubClientSecret = vaultSecretsManager.getGitHubClientSecret();
        this.githubRedirectUri = vaultSecretsManager.getGitHubRedirectUri();

        // Validate that all required secrets are available
        if (!vaultSecretsManager.hasAllRequiredSecrets()) {
            logger.error("Failed to load all required secrets!");
            logger.error(vaultSecretsManager.getStatusSummary());
            throw new IllegalStateException("Required secrets not available. Check logs for details.");
        }

        logger.info("Secrets configuration initialized successfully");
        logger.info(vaultSecretsManager.getStatusSummary());
    }

    public String getGitHubClientId() {
        return githubClientId;
    }

    public String getGitHubClientSecret() {
        return githubClientSecret;
    }

    public String getGitHubRedirectUri() {
        return githubRedirectUri;
    }

    /**
     * Refresh secrets from the VaultSecretsManager.
     * Useful for testing or when secrets are updated.
     */
    public void refreshSecrets() {
        logger.info("Refreshing secrets...");
        vaultSecretsManager.clearCache();

        this.githubClientId = vaultSecretsManager.getGitHubClientId();
        this.githubClientSecret = vaultSecretsManager.getGitHubClientSecret();
        this.githubRedirectUri = vaultSecretsManager.getGitHubRedirectUri();

        if (!vaultSecretsManager.hasAllRequiredSecrets()) {
            logger.error("Failed to refresh secrets!");
            throw new IllegalStateException("Failed to refresh secrets");
        }

        logger.info("Secrets refreshed successfully");
    }

    /**
     * Get the VaultSecretsManager instance for advanced operations.
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", 
                       justification = "VaultSecretsManager is intentionally exposed for advanced operations")
    public VaultSecretsManager getVaultSecretsManager() {
        return vaultSecretsManager;
    }
}
