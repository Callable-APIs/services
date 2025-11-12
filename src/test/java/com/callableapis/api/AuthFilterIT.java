package com.callableapis.api;

import com.callableapis.api.security.ApiKeyService;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import static org.junit.Assert.*;

public class AuthFilterIT extends JerseyTest {

    // Add timeout rule to prevent individual tests from hanging
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @Override
    protected Application configure() {
        return new APIApplication();
    }
    
    @Override
    public void tearDown() throws Exception {
        try {
            super.tearDown();
        } catch (Exception e) {
            // Log but don't fail if tearDown has issues
            System.err.println("Warning: tearDown exception: " + e.getMessage());
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Seed a valid api key for identity
        ApiKeyService.getInstance().getOrCreateApiKeyForIdentity("github:testuser");
    }

    @Test
    public void testMissingBearer() {
        Response r = target("v1/calendar/date").request().get();
        assertEquals(401, r.getStatus());
    }

    @Test
    public void testInvalidBearer() {
        Response r = target("v1/calendar/date").request()
                .header("Authorization", "Bearer invalid")
                .get();
        assertEquals(403, r.getStatus());
    }

    @Test
    public void testValidBearer() {
        String apiKey = ApiKeyService.getInstance().getOrCreateApiKeyForIdentity("github:testuser");
        Response r = target("v1/calendar/date").request()
                .header("Authorization", "Bearer " + apiKey)
                .get();
        assertEquals(200, r.getStatus());
    }
}
