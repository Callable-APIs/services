package com.callableapis.api.secrets;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;
import software.amazon.awssdk.services.ssm.model.SsmException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * VaultSecretsManager provides dual secrets management:
 * Primary: Ansible Vault (for Oracle/Google/IBM nodes)
 * Fallback: AWS Parameter Store (for Elastic Beanstalk)
 * 
 * This class handles the transition from AWS Parameter Store to Ansible Vault
 * while maintaining backward compatibility.
 */
public class VaultSecretsManager {

    private static final Logger logger = LoggerFactory.getLogger(VaultSecretsManager.class);

    // Ansible Vault paths
    private static final String VAULT_PASSWORD_PATH = "/app/vault-password";
    private static final String VAULT_SECRETS_PATH = "/app/secrets/all-secrets.env";

    // AWS Parameter Store paths
    private static final String AWS_PARAMETER_PREFIX = "/callableapis/github-oidc/";

    // Secret keys
    public static final String GITHUB_CLIENT_ID = "GITHUB_CLIENT_ID";
    public static final String GITHUB_CLIENT_SECRET = "GITHUB_CLIENT_SECRET";
    public static final String GITHUB_REDIRECT_URI = "GITHUB_REDIRECT_URI";

    private final SsmClient ssmClient;
    private final Map<String, String> secretsCache;
    private final boolean ansibleVaultAvailable;

    public VaultSecretsManager() {
        this.ssmClient = createSSMClient();
        this.secretsCache = new HashMap<>();
        this.ansibleVaultAvailable = isAnsibleVaultAvailable();

        logger.info("VaultSecretsManager initialized. Ansible Vault available: {}", ansibleVaultAvailable);
    }

    /**
     * Create SSM client. This method can be overridden for testing.
     */
    protected SsmClient createSSMClient() {
        return SsmClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Get a secret value, trying Ansible Vault first, then falling back to AWS
     * Parameter Store.
     * 
     * @param key The secret key to retrieve
     * @return The secret value, or null if not found
     */
    public String getSecret(String key) {
        // Check cache first
        if (secretsCache.containsKey(key)) {
            return secretsCache.get(key);
        }

        String value = null;

        if (ansibleVaultAvailable) {
            value = getSecretFromAnsibleVault(key);
            if (value != null) {
                logger.debug("Retrieved secret '{}' from Ansible Vault", key);
            }
        }

        // Fallback to AWS Parameter Store if Ansible Vault failed or unavailable
        if (value == null) {
            value = getSecretFromAWSParameterStore(key);
            if (value != null) {
                logger.debug("Retrieved secret '{}' from AWS Parameter Store (fallback)", key);
            }
        }

        if (value != null) {
            secretsCache.put(key, value);
        } else {
            logger.warn("Secret '{}' not found in either Ansible Vault or AWS Parameter Store", key);
        }

        return value;
    }

    /**
     * Get GitHub OIDC client ID.
     */
    public String getGitHubClientId() {
        return getSecret(GITHUB_CLIENT_ID);
    }

    /**
     * Get GitHub OIDC client secret.
     */
    public String getGitHubClientSecret() {
        return getSecret(GITHUB_CLIENT_SECRET);
    }

    /**
     * Get GitHub OIDC redirect URI.
     */
    public String getGitHubRedirectUri() {
        return getSecret(GITHUB_REDIRECT_URI);
    }

    /**
     * Check if Ansible Vault is available on this node.
     */
    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "Hardcoded paths are required for Ansible Vault integration")
    private boolean isAnsibleVaultAvailable() {
        File vaultPasswordFile = new File(VAULT_PASSWORD_PATH);
        File vaultSecretsFile = new File(VAULT_SECRETS_PATH);

        boolean available = vaultPasswordFile.exists() && vaultSecretsFile.exists();

        if (available) {
            logger.info("Ansible Vault files found - using Ansible Vault for secrets management");
        } else {
            logger.info("Ansible Vault files not found - will use AWS Parameter Store fallback");
        }

        return available;
    }

    /**
     * Get secret from Ansible Vault by reading the decrypted secrets file.
     */
    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "Hardcoded path is required for Ansible Vault integration")
    private String getSecretFromAnsibleVault(String key) {
        try {
            Path secretsPath = Paths.get(VAULT_SECRETS_PATH);
            if (!Files.exists(secretsPath)) {
                logger.warn("Ansible Vault secrets file not found: {}", VAULT_SECRETS_PATH);
                return null;
            }

            // Read the secrets file as environment variables
            Properties secrets = new Properties();
            try (FileInputStream fis = new FileInputStream(secretsPath.toFile())) {
                secrets.load(fis);
            }

            // Convert vault_ prefixed keys to uppercase environment variable names
            String vaultKey = "vault_" + key.toLowerCase();
            String value = secrets.getProperty(vaultKey);

            if (value == null) {
                // Try direct key lookup as fallback
                value = secrets.getProperty(key);
            }

            return value;

        } catch (IOException e) {
            logger.error("Failed to read Ansible Vault secrets file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get secret from AWS Parameter Store.
     */
    private String getSecretFromAWSParameterStore(String key) {
        try {
            // Convert environment variable name to AWS parameter name
            String parameterName = convertKeyToAWSParameterName(key);

            GetParameterRequest request = GetParameterRequest.builder()
                    .name(parameterName)
                    .withDecryption(true)
                    .build();

            GetParameterResponse response = ssmClient.getParameter(request);
            return response.parameter().value();

        } catch (ParameterNotFoundException e) {
            logger.debug("Parameter not found in AWS Parameter Store: {}", key);
            return null;
        } catch (SsmException e) {
            logger.error("Failed to retrieve parameter from AWS Parameter Store: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Convert environment variable key to AWS Parameter Store parameter name.
     */
    private String convertKeyToAWSParameterName(String key) {
        // Convert GITHUB_CLIENT_ID to /callableapis/github-oidc/client_id
        String parameterName = key.toLowerCase().replace("_", "_");
        return AWS_PARAMETER_PREFIX + parameterName;
    }

    /**
     * Clear the secrets cache (useful for testing or when secrets are updated).
     */
    public void clearCache() {
        secretsCache.clear();
        logger.debug("Secrets cache cleared");
    }

    /**
     * Check if all required secrets are available.
     */
    public boolean hasAllRequiredSecrets() {
        String clientId = getGitHubClientId();
        String clientSecret = getGitHubClientSecret();
        String redirectUri = getGitHubRedirectUri();

        boolean hasAll = clientId != null && clientSecret != null && redirectUri != null;

        if (!hasAll) {
            logger.warn("Missing required secrets - Client ID: {}, Client Secret: {}, Redirect URI: {}",
                    clientId != null, clientSecret != null, redirectUri != null);
        }

        return hasAll;
    }

    /**
     * Calculate SHA1 hash of a file.
     */
    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "Hardcoded paths are required for Ansible Vault integration")
    private String calculateFileSHA1(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return "file_not_found";
            }
            
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] fileBytes = Files.readAllBytes(path);
            byte[] hashBytes = digest.digest(fileBytes);
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.warn("Failed to calculate SHA1 for file {}: {}", filePath, e.getMessage());
            return "error_calculating_hash";
        }
    }

    /**
     * Get all discovered secret keys from the vault secrets file.
     */
    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "Hardcoded path is required for Ansible Vault integration")
    private Set<String> getDiscoveredSecretKeys() {
        Set<String> discoveredKeys = new HashSet<>();
        
        if (!ansibleVaultAvailable) {
            return discoveredKeys;
        }
        
        try {
            Path secretsPath = Paths.get(VAULT_SECRETS_PATH);
            if (!Files.exists(secretsPath)) {
                return discoveredKeys;
            }

            Properties secrets = new Properties();
            try (FileInputStream fis = new FileInputStream(secretsPath.toFile())) {
                secrets.load(fis);
            }

            // Get all property keys
            for (String key : secrets.stringPropertyNames()) {
                discoveredKeys.add(key);
            }

        } catch (IOException e) {
            logger.warn("Failed to read secrets file for key discovery: {}", e.getMessage());
        }
        
        return discoveredKeys;
    }

    /**
     * Get a summary of the secrets management status.
     */
    public String getStatusSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("VaultSecretsManager Status:\n");
        summary.append("  Ansible Vault Available: ").append(ansibleVaultAvailable).append("\n");
        summary.append("  Cached Secrets: ").append(secretsCache.size()).append("\n");
        summary.append("  Required Secrets Available: ").append(hasAllRequiredSecrets()).append("\n");

        if (ansibleVaultAvailable) {
            summary.append("  Primary Source: Ansible Vault\n");
            summary.append("  Fallback Source: AWS Parameter Store\n");
            
            // Add file hashes
            String vaultPasswordHash = calculateFileSHA1(VAULT_PASSWORD_PATH);
            String vaultSecretsHash = calculateFileSHA1(VAULT_SECRETS_PATH);
            summary.append("  Vault Password File SHA1: ").append(vaultPasswordHash).append("\n");
            summary.append("  Vault Secrets File SHA1: ").append(vaultSecretsHash).append("\n");
            
            // Add discovered secret keys
            Set<String> discoveredKeys = getDiscoveredSecretKeys();
            summary.append("  Discovered Secret Keys: ").append(discoveredKeys.size()).append("\n");
            if (!discoveredKeys.isEmpty()) {
                summary.append("  Secret Keys: ").append(String.join(", ", discoveredKeys)).append("\n");
            }
        } else {
            summary.append("  Primary Source: AWS Parameter Store\n");
            summary.append("  Fallback Source: None\n");
        }

        return summary.toString();
    }
}
