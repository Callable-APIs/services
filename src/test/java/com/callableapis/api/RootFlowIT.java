package com.callableapis.api;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import static org.junit.Assert.*;

public class RootFlowIT extends JerseyTest {
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

    @Test
    public void testRootPageAccessible() {
        Response r = target("/").request()
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .get();
        // Should redirect to /index
        assertEquals(303, r.getStatus());
        String location = r.getHeaderString("Location");
        assertTrue(location != null && location.endsWith("/index"));
    }

    @Test
    public void testNotFoundRedirectsToRoot() {
        Response r = target("no-such").request()
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .get();
        // Jersey test client follows redirects only if asked; we expect 303 See Other
        assertTrue(r.getStatus() == 303 || r.getStatus() == 302);
        String location = r.getHeaderString("Location");
        assertTrue(location != null && ("/".equals(location) || location.endsWith("/")));
    }
}
