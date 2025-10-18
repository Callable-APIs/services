package com.callableapis.api.config;

import com.callableapis.api.secrets.VaultSecretsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for SecretsConfig to ensure proper secrets loading and validation.
 */
@DisplayName("SecretsConfig Tests")
public class SecretsConfigTest {
    
    @Mock
    private VaultSecretsManager mockVaultSecretsManager;
    
    private SecretsConfig secretsConfig;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create SecretsConfig with mocked VaultSecretsManager
        secretsConfig = new SecretsConfig(mockVaultSecretsManager);
    }
    
    @Test
    @DisplayName("Should initialize successfully when all secrets are available")
    void testInitializeSecretsSuccess() {
        // Mock VaultSecretsManager to return all required secrets
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("test-client-id");
        when(mockVaultSecretsManager.getGitHubClientSecret()).thenReturn("test-client-secret");
        when(mockVaultSecretsManager.getGitHubRedirectUri()).thenReturn("test-redirect-uri");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(true);
        when(mockVaultSecretsManager.getStatusSummary()).thenReturn("Test status summary");
        
        // This should not throw an exception
        assertDoesNotThrow(() -> secretsConfig.initializeSecrets());
        
        // Verify that all secrets were retrieved
        verify(mockVaultSecretsManager).getGitHubClientId();
        verify(mockVaultSecretsManager).getGitHubClientSecret();
        verify(mockVaultSecretsManager).getGitHubRedirectUri();
        verify(mockVaultSecretsManager).hasAllRequiredSecrets();
    }
    
    @Test
    @DisplayName("Should throw exception when required secrets are missing")
    void testInitializeSecretsFailure() {
        // Mock VaultSecretsManager to indicate missing secrets
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("test-client-id");
        when(mockVaultSecretsManager.getGitHubClientSecret()).thenReturn("test-client-secret");
        when(mockVaultSecretsManager.getGitHubRedirectUri()).thenReturn("test-redirect-uri");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(false);
        when(mockVaultSecretsManager.getStatusSummary()).thenReturn("Missing secrets");
        
        // This should throw an exception
        assertThrows(IllegalStateException.class, () -> secretsConfig.initializeSecrets());
    }
    
    @Test
    @DisplayName("Should return correct GitHub client ID")
    void testGetGitHubClientId() {
        // Mock VaultSecretsManager to return client ID
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("test-client-id");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(true);
        when(mockVaultSecretsManager.getStatusSummary()).thenReturn("Test status");
        
        // Initialize secrets
        secretsConfig.initializeSecrets();
        
        // Test getter
        String clientId = secretsConfig.getGitHubClientId();
        assertEquals("test-client-id", clientId);
    }
    
    @Test
    @DisplayName("Should return correct GitHub client secret")
    void testGetGitHubClientSecret() {
        // Mock VaultSecretsManager to return client secret
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("test-client-id");
        when(mockVaultSecretsManager.getGitHubClientSecret()).thenReturn("test-client-secret");
        when(mockVaultSecretsManager.getGitHubRedirectUri()).thenReturn("test-redirect-uri");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(true);
        when(mockVaultSecretsManager.getStatusSummary()).thenReturn("Test status");
        
        // Initialize secrets
        secretsConfig.initializeSecrets();
        
        // Test getter
        String clientSecret = secretsConfig.getGitHubClientSecret();
        assertEquals("test-client-secret", clientSecret);
    }
    
    @Test
    @DisplayName("Should return correct GitHub redirect URI")
    void testGetGitHubRedirectUri() {
        // Mock VaultSecretsManager to return redirect URI
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("test-client-id");
        when(mockVaultSecretsManager.getGitHubClientSecret()).thenReturn("test-client-secret");
        when(mockVaultSecretsManager.getGitHubRedirectUri()).thenReturn("test-redirect-uri");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(true);
        when(mockVaultSecretsManager.getStatusSummary()).thenReturn("Test status");
        
        // Initialize secrets
        secretsConfig.initializeSecrets();
        
        // Test getter
        String redirectUri = secretsConfig.getGitHubRedirectUri();
        assertEquals("test-redirect-uri", redirectUri);
    }
    
    @Test
    @DisplayName("Should refresh secrets successfully")
    void testRefreshSecrets() {
        // Mock VaultSecretsManager for initial load
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("initial-client-id");
        when(mockVaultSecretsManager.getGitHubClientSecret()).thenReturn("initial-client-secret");
        when(mockVaultSecretsManager.getGitHubRedirectUri()).thenReturn("initial-redirect-uri");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(true);
        when(mockVaultSecretsManager.getStatusSummary()).thenReturn("Initial status");
        
        // Initialize secrets
        secretsConfig.initializeSecrets();
        
        // Mock VaultSecretsManager for refresh
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("refreshed-client-id");
        when(mockVaultSecretsManager.getGitHubClientSecret()).thenReturn("refreshed-client-secret");
        when(mockVaultSecretsManager.getGitHubRedirectUri()).thenReturn("refreshed-redirect-uri");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(true);
        
        // Refresh secrets
        assertDoesNotThrow(() -> secretsConfig.refreshSecrets());
        
        // Verify that cache was cleared and secrets were refreshed
        verify(mockVaultSecretsManager).clearCache();
        verify(mockVaultSecretsManager, times(2)).getGitHubClientId();
        verify(mockVaultSecretsManager, times(2)).getGitHubClientSecret();
        verify(mockVaultSecretsManager, times(2)).getGitHubRedirectUri();
        
        // Verify refreshed values
        assertEquals("refreshed-client-id", secretsConfig.getGitHubClientId());
        assertEquals("refreshed-client-secret", secretsConfig.getGitHubClientSecret());
        assertEquals("refreshed-redirect-uri", secretsConfig.getGitHubRedirectUri());
    }
    
    @Test
    @DisplayName("Should throw exception when refresh fails")
    void testRefreshSecretsFailure() {
        // Mock VaultSecretsManager for initial load
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("test-client-id");
        when(mockVaultSecretsManager.getGitHubClientSecret()).thenReturn("test-client-secret");
        when(mockVaultSecretsManager.getGitHubRedirectUri()).thenReturn("test-redirect-uri");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(true);
        when(mockVaultSecretsManager.getStatusSummary()).thenReturn("Test status");
        
        // Initialize secrets
        secretsConfig.initializeSecrets();
        
        // Mock VaultSecretsManager for refresh to fail
        when(mockVaultSecretsManager.getGitHubClientId()).thenReturn("refreshed-client-id");
        when(mockVaultSecretsManager.getGitHubClientSecret()).thenReturn("refreshed-client-secret");
        when(mockVaultSecretsManager.getGitHubRedirectUri()).thenReturn("refreshed-redirect-uri");
        when(mockVaultSecretsManager.hasAllRequiredSecrets()).thenReturn(false);
        
        // Refresh should throw exception
        assertThrows(IllegalStateException.class, () -> secretsConfig.refreshSecrets());
    }
    
    @Test
    @DisplayName("Should return VaultSecretsManager instance")
    void testGetVaultSecretsManager() {
        VaultSecretsManager manager = secretsConfig.getVaultSecretsManager();
        assertSame(mockVaultSecretsManager, manager);
    }
}