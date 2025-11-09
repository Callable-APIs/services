package com.callableapis.api;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * Integration test to verify that AuthResource can be properly instantiated
 * with all its dependencies via dependency injection.
 * 
 * This test would have caught the issue where AuthenticationStatsService
 * was not registered in the DI container, causing UnsatisfiedDependencyException.
 */
public class AuthResourceDITest extends JerseyTest {

    @Override
    protected Application configure() {
        return new APIApplication();
    }

    @Test
    public void testAuthResourceCanBeInstantiated() {
        // If AuthResource cannot be instantiated due to missing DI bindings,
        // this request will fail with UnsatisfiedDependencyException
        // This test verifies that all required dependencies are registered
        Response r = target("auth/login").request().get();
        
        // We don't care about the actual response status here (it might be 500 if
        // OAuth config is missing, or 302 if redirect works)
        // The important thing is that it doesn't fail due to DI issues
        assertNotNull("Response should not be null", r);
        
        // If we get a 500, check that it's not due to DI failure
        // A 500 due to missing OAuth config is acceptable, but DI failures would
        // cause the resource to not be instantiated at all
        if (r.getStatus() == 500) {
            String entity = r.readEntity(String.class);
            // If the error mentions DI or UnsatisfiedDependencyException, that's a failure
            if (entity != null) {
                assertFalse("Should not fail due to DI/UnsatisfiedDependencyException: " + entity,
                           entity.contains("UnsatisfiedDependencyException") || 
                           entity.contains("injection") ||
                           (entity.contains("AuthenticationStatsService") && entity.contains("not available")));
            }
        }
        // If status is not 500, or if 500 but not DI-related, test passes
    }

    @Test
    public void testCallbackEndpointCanHandleMissingCode() {
        // Test that the callback endpoint can be called even with invalid parameters
        // This verifies that AuthResource is properly instantiated with all dependencies
        Response r = target("auth/callback").request().get();
        
        // Should return 400 Bad Request for missing code, not 500 due to DI failure
        assertEquals("Callback should return 400 for missing code parameter", 
                     400, r.getStatus());
        
        String entity = r.readEntity(String.class);
        assertNotNull("Response entity should not be null", entity);
        assertTrue("Response should indicate missing code", 
                   entity.contains("Missing code") || entity.contains("code"));
    }

    @Test
    public void testCallbackEndpointCanHandleInvalidCode() {
        // Test that the callback endpoint can handle invalid code parameter
        // This verifies that AuthenticationStatsService is properly injected
        // and can record failed authentication attempts
        Response r = target("auth/callback")
                .queryParam("code", "invalid_code")
                .queryParam("state", "test_state")
                .request()
                .get();
        
        // Should return 400, 502, or 500 (if OAuth config missing), but not 500 due to DI failure
        // If DI was broken, we'd get 500 with UnsatisfiedDependencyException
        assertNotNull("Response should not be null", r);
        
        // Verify it's not a DI-related error
        if (r.getStatus() == 500) {
            String entity = r.readEntity(String.class);
            if (entity != null) {
                // Check that it's not a DI failure
                assertFalse("Should not fail due to UnsatisfiedDependencyException: " + entity,
                           entity.contains("UnsatisfiedDependencyException") ||
                           (entity.contains("AuthenticationStatsService") && entity.contains("not available")) ||
                           (entity.contains("injection") && entity.contains("error")));
            }
        } else {
            // If not 500, should be 400 or 502 (expected for invalid code)
            assertTrue("Callback should return 400 or 502 for invalid code",
                      r.getStatus() == 400 || r.getStatus() == 502);
        }
    }

    @Test
    public void testAuthenticationStatsServiceIsRegistered() {
        // Verify that AuthenticationStatsService can be accessed through DI
        // This is a direct test of the DI configuration
        Response r = target("auth/callback")
                .queryParam("code", "test")
                .queryParam("state", "test")
                .request()
                .get();
        
        // The fact that we can call the endpoint without DI errors means
        // AuthenticationStatsService is properly registered
        // Even if the OAuth flow fails, the service should be injected
        assertNotNull("Response should not be null", r);
        
        // If DI was broken, we'd get 500 with dependency injection error
        if (r.getStatus() == 500) {
            String entity = r.readEntity(String.class);
            if (entity != null) {
                assertFalse("Should not contain UnsatisfiedDependencyException",
                           entity.contains("UnsatisfiedDependencyException"));
                assertFalse("Should not contain AuthenticationStatsService injection error",
                           entity.contains("AuthenticationStatsService") && 
                           entity.contains("injection"));
            }
        }
    }
}

