# Secrets Management

This document describes the dual secrets management system implemented for the Callable APIs service, supporting both Ansible Vault (primary) and AWS Parameter Store (fallback) for cloud migration.

## Overview

The service implements a dual secrets management approach:
- **Primary**: Ansible Vault (for Oracle/Google/IBM nodes)
- **Fallback**: AWS Parameter Store (for Elastic Beanstalk)

This allows for a smooth transition from AWS to other cloud providers while maintaining backward compatibility.

## Architecture

### VaultSecretsManager

The `VaultSecretsManager` class provides the core functionality:

```java
// Primary: Try Ansible Vault first
String value = vaultSecretsManager.getSecret("GITHUB_CLIENT_ID");

// Fallback: Use AWS Parameter Store if Ansible Vault fails
if (value == null) {
    value = parameterStore.getParameterWithEnvFallback(...);
}
```

### SecretsConfig

The `SecretsConfig` class manages secrets loading and validation:

```java
@ApplicationScoped
public class SecretsConfig {
    @Inject
    private VaultSecretsManager vaultSecretsManager;
    
    @PostConstruct
    public void initializeSecrets() {
        // Load and validate all required secrets
    }
}
```

## Configuration

### Ansible Vault Structure

For Oracle/Google/IBM nodes (containerd):

**Vault password file**: `/app/vault-password`
**Secrets file**: `/app/secrets/all-secrets.env`

**Secrets format (YAML)**:
```yaml
vault_github_client_id: "your-client-id"
vault_github_client_secret: "your-client-secret"
vault_github_redirect_uri: "https://api.callableapis.com/api/auth/callback"
```

**Converted to environment variables**:
```bash
GITHUB_CLIENT_ID=your-client-id
GITHUB_CLIENT_SECRET=your-client-secret
GITHUB_REDIRECT_URI=https://api.callableapis.com/api/auth/callback
```

### AWS Parameter Store Structure

For Elastic Beanstalk:

**Path**: `/callableapis/github-oidc/`

**Parameters**:
- `/callableapis/github-oidc/github_client_id`
- `/callableapis/github-oidc/github_client_secret`
- `/callableapis/github-oidc/github_redirect_uri`

## Deployment Paths

### Containerized Nodes (Oracle/Google/IBM)

1. **Vault password**: `/app/vault-password`
2. **Secrets file**: `/app/secrets/all-secrets.env`
3. **Use Ansible Vault decryption**

### Elastic Beanstalk

1. **Use AWS Parameter Store directly**
2. **No vault files needed**

## Infrastructure Details

### Current Infrastructure

- **Oracle Cloud (onode1)**: 159.54.170.237
- **Google Cloud (gnode1)**: 35.233.161.8
- **IBM Cloud (inode1)**: 52.116.135.43
- **AWS Elastic Beanstalk**: 52.13.53.164

### Ansible Configuration

- **Inventory**: `ansible/inventory/production` (sensitive file)
- **Deployment playbooks**: `ansible/playbooks/deploy-*.yml`

## Usage

### Basic Usage

```java
// Get a secret (tries Ansible Vault first, then AWS Parameter Store)
String clientId = vaultSecretsManager.getGitHubClientId();
String clientSecret = vaultSecretsManager.getGitHubClientSecret();
String redirectUri = vaultSecretsManager.getGitHubRedirectUri();

// Check if all required secrets are available
boolean hasAllSecrets = vaultSecretsManager.hasAllRequiredSecrets();

// Get status summary
String status = vaultSecretsManager.getStatusSummary();
```

### Advanced Usage

```java
// Clear cache (useful for testing or when secrets are updated)
vaultSecretsManager.clearCache();

// Get VaultSecretsManager instance for advanced operations
VaultSecretsManager manager = AppConfig.getVaultSecretsManager();

// Get secrets status summary
String summary = AppConfig.getSecretsStatusSummary();
```

## Testing

### Unit Tests

The system includes comprehensive unit tests:

- `VaultSecretsManagerTest` - Tests the core secrets management logic
- `SecretsConfigTest` - Tests the configuration management
- `AppConfigIntegrationTest` - Tests the integration with existing AppConfig

### Test Execution

```bash
# Run all tests
./gradlew test

# Run specific test classes
./gradlew test --tests "VaultSecretsManagerTest"
./gradlew test --tests "SecretsConfigTest"
./gradlew test --tests "AppConfigIntegrationTest"
```

## Error Handling

### Ansible Vault Errors

- **File not found**: Falls back to AWS Parameter Store
- **Decryption failure**: Falls back to AWS Parameter Store
- **Invalid format**: Falls back to AWS Parameter Store

### AWS Parameter Store Errors

- **Parameter not found**: Uses fallback values
- **Access denied**: Uses fallback values
- **Network errors**: Uses fallback values

### Fallback Chain

1. **Ansible Vault** (if available)
2. **AWS Parameter Store** (if available)
3. **Environment variables** (if available)
4. **Default values** (hardcoded)

## Monitoring

### Logging

The system provides detailed logging:

```
INFO: VaultSecretsManager initialized. Ansible Vault available: true
INFO: Retrieved secret 'GITHUB_CLIENT_ID' from Ansible Vault
INFO: Retrieved secret 'GITHUB_CLIENT_SECRET' from AWS Parameter Store (fallback)
WARN: Secret 'GITHUB_REDIRECT_URI' not found in either Ansible Vault or AWS Parameter Store
```

### Status Summary

```java
String status = vaultSecretsManager.getStatusSummary();
```

Example output:
```
VaultSecretsManager Status:
  Ansible Vault Available: true
  Cached Secrets: 3
  Required Secrets Available: true
  Primary Source: Ansible Vault
  Fallback Source: AWS Parameter Store
```

## Migration Strategy

### Phase 1: Dual Support (Current)

- ✅ Ansible Vault primary
- ✅ AWS Parameter Store fallback
- ✅ Backward compatibility maintained

### Phase 2: Full Migration

- 🔄 Remove AWS Parameter Store fallback
- 🔄 Update all nodes to use Ansible Vault
- 🔄 Remove AWS-specific code

### Phase 3: Cleanup

- 🔄 Remove AWS dependencies
- 🔄 Simplify configuration
- 🔄 Update documentation

## Security Considerations

### Ansible Vault

- **Vault password**: Stored securely on containerized nodes
- **Secrets file**: Encrypted and stored securely
- **Access control**: Managed through Ansible inventory

### AWS Parameter Store

- **IAM roles**: Proper permissions for Parameter Store access
- **Encryption**: SecureString parameters are encrypted
- **Access logging**: CloudTrail logs all access

### General Security

- **No hardcoded secrets**: All secrets loaded from external sources
- **Minimal exposure**: Secrets only loaded when needed
- **Cache management**: Secrets cached with appropriate TTL
- **Error handling**: No secrets leaked in error messages

## Troubleshooting

### Common Issues

1. **Ansible Vault not available**
   - Check if `/app/vault-password` exists
   - Check if `/app/secrets/all-secrets.env` exists
   - Verify Ansible Vault decryption works

2. **AWS Parameter Store not accessible**
   - Check IAM permissions
   - Verify AWS credentials
   - Check network connectivity

3. **Secrets not loading**
   - Check logs for specific error messages
   - Verify secret names and paths
   - Test with fallback values

### Debug Commands

```bash
# Check Ansible Vault files
ls -la /app/vault-password
ls -la /app/secrets/all-secrets.env

# Test Ansible Vault decryption
ansible-vault view /app/secrets/all-secrets.env --vault-password-file /app/vault-password

# Check AWS Parameter Store access
aws ssm get-parameter --name "/callableapis/github-oidc/github_client_id" --with-decryption

# Check application logs
tail -f logs/catalina.out | grep -i vault
```

## Future Enhancements

### Planned Features

- **Secret rotation**: Automatic secret rotation support
- **Health checks**: Secrets availability health checks
- **Metrics**: Prometheus metrics for secrets management
- **Audit logging**: Detailed audit logs for secret access

### Configuration Improvements

- **YAML configuration**: Support for YAML configuration files
- **Environment-specific**: Different configurations per environment
- **Validation**: Enhanced secret validation and format checking

## References

- [Ansible Vault Documentation](https://docs.ansible.com/ansible/latest/user_guide/vault.html)
- [AWS Parameter Store Documentation](https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-parameter-store.html)
- [Java AWS SDK Documentation](https://docs.aws.amazon.com/sdk-for-java/)
- [SnakeYAML Documentation](https://bitbucket.org/asomov/snakeyaml/wiki/Documentation)
