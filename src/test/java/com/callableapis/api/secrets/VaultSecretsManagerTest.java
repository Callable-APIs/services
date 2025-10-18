package com.callableapis.api.secrets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;
import software.amazon.awssdk.services.ssm.model.SsmException;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for VaultSecretsManager to ensure proper dual secrets management.
 */
@DisplayName("VaultSecretsManager Tests")
public class VaultSecretsManagerTest {
    
    @TempDir
    Path tempDir;
    
    private VaultSecretsManager vaultSecretsManager;
    private SsmClient mockSsmClient;
    
    @BeforeEach
    void setUp() {
        // Mock the AWS SSM client
        mockSsmClient = mock(SsmClient.class);
        
        // Create VaultSecretsManager with mocked dependencies
        vaultSecretsManager = new VaultSecretsManager() {
            @Override
            protected SsmClient createSSMClient() {
                return mockSsmClient;
            }
        };
    }
    
    @Test
    @DisplayName("Should return null when Ansible Vault is not available and AWS Parameter Store fails")
    void testGetSecretWhenBothSourcesFail() {
        // Mock AWS Parameter Store to throw exception
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenThrow(ParameterNotFoundException.builder().message("Parameter not found").build());
        
        String result = vaultSecretsManager.getSecret("NONEXISTENT_SECRET");
        
        assertNull(result, "Should return null when both sources fail");
    }
    
    @Test
    @DisplayName("Should return value from AWS Parameter Store when Ansible Vault is not available")
    void testGetSecretFromAWSParameterStoreFallback() {
        // Mock AWS Parameter Store response
        Parameter mockParameter = Parameter.builder()
            .value("test-secret-value")
            .build();
        
        GetParameterResponse mockResponse = GetParameterResponse.builder()
            .parameter(mockParameter)
            .build();
        
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenReturn(mockResponse);
        
        String result = vaultSecretsManager.getSecret(VaultSecretsManager.GITHUB_CLIENT_ID);
        
        assertEquals("test-secret-value", result, "Should return value from AWS Parameter Store");
    }
    
    @Test
    @DisplayName("Should cache secrets after first retrieval")
    void testSecretsCaching() {
        // Mock AWS Parameter Store response
        Parameter mockParameter = Parameter.builder()
            .value("cached-secret-value")
            .build();
        
        GetParameterResponse mockResponse = GetParameterResponse.builder()
            .parameter(mockParameter)
            .build();
        
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenReturn(mockResponse);
        
        // First call should hit AWS
        String result1 = vaultSecretsManager.getSecret(VaultSecretsManager.GITHUB_CLIENT_ID);
        assertEquals("cached-secret-value", result1);
        
        // Second call should use cache
        String result2 = vaultSecretsManager.getSecret(VaultSecretsManager.GITHUB_CLIENT_ID);
        assertEquals("cached-secret-value", result2);
        
        // Verify AWS was called only once
        verify(mockSsmClient, times(1)).getParameter(any(GetParameterRequest.class));
    }
    
    @Test
    @DisplayName("Should clear cache when clearCache is called")
    void testClearCache() {
        // Mock AWS Parameter Store response
        Parameter mockParameter = Parameter.builder()
            .value("secret-value")
            .build();
        
        GetParameterResponse mockResponse = GetParameterResponse.builder()
            .parameter(mockParameter)
            .build();
        
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenReturn(mockResponse);
        
        // First call
        vaultSecretsManager.getSecret(VaultSecretsManager.GITHUB_CLIENT_ID);
        
        // Clear cache
        vaultSecretsManager.clearCache();
        
        // Second call should hit AWS again
        vaultSecretsManager.getSecret(VaultSecretsManager.GITHUB_CLIENT_ID);
        
        // Verify AWS was called twice
        verify(mockSsmClient, times(2)).getParameter(any(GetParameterRequest.class));
    }
    
    @Test
    @DisplayName("Should return false when required secrets are missing")
    void testHasAllRequiredSecretsWhenMissing() {
        // Mock AWS Parameter Store to return null for all secrets
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenThrow(ParameterNotFoundException.builder().message("Parameter not found").build());
        
        boolean hasAllSecrets = vaultSecretsManager.hasAllRequiredSecrets();
        
        assertFalse(hasAllSecrets, "Should return false when required secrets are missing");
    }
    
    @Test
    @DisplayName("Should return true when all required secrets are available")
    void testHasAllRequiredSecretsWhenAvailable() {
        // Mock AWS Parameter Store to return values for all secrets
        Parameter mockParameter = Parameter.builder()
            .value("test-value")
            .build();
        
        GetParameterResponse mockResponse = GetParameterResponse.builder()
            .parameter(mockParameter)
            .build();
        
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenReturn(mockResponse);
        
        boolean hasAllSecrets = vaultSecretsManager.hasAllRequiredSecrets();
        
        assertTrue(hasAllSecrets, "Should return true when all required secrets are available");
    }
    
    @Test
    @DisplayName("Should provide status summary")
    void testGetStatusSummary() {
        // Mock AWS Parameter Store to avoid null pointer
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenThrow(ParameterNotFoundException.builder().message("Parameter not found").build());
        
        String statusSummary = vaultSecretsManager.getStatusSummary();
        
        assertNotNull(statusSummary, "Status summary should not be null");
        assertTrue(statusSummary.contains("VaultSecretsManager Status"), "Should contain status header");
        assertTrue(statusSummary.contains("Ansible Vault Available"), "Should contain Ansible Vault status");
        assertTrue(statusSummary.contains("Cached Secrets"), "Should contain cache status");
    }
    
    @Test
    @DisplayName("Should handle AWS SSM exceptions gracefully")
    void testHandleAWSSSMException() {
        // Mock AWS SSM to throw exception
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenThrow(SsmException.builder().message("SSM error").build());
        
        String result = vaultSecretsManager.getSecret(VaultSecretsManager.GITHUB_CLIENT_ID);
        
        assertNull(result, "Should return null when AWS SSM throws exception");
    }
    
    @Test
    @DisplayName("Should handle GitHub-specific secret methods")
    void testGitHubSpecificMethods() {
        // Mock AWS Parameter Store response
        Parameter mockParameter = Parameter.builder()
            .value("github-test-value")
            .build();
        
        GetParameterResponse mockResponse = GetParameterResponse.builder()
            .parameter(mockParameter)
            .build();
        
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
            .thenReturn(mockResponse);
        
        // Test GitHub-specific methods
        String clientId = vaultSecretsManager.getGitHubClientId();
        String clientSecret = vaultSecretsManager.getGitHubClientSecret();
        String redirectUri = vaultSecretsManager.getGitHubRedirectUri();
        
        assertEquals("github-test-value", clientId);
        assertEquals("github-test-value", clientSecret);
        assertEquals("github-test-value", redirectUri);
    }
}