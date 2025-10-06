package com.callableapis.api;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import static org.junit.Assert.*;

public class RootFlowIT extends JerseyTest {
    @Override
    protected Application configure() {
        return new APIApplication();
    }

    @Test
    public void testRootPageAccessible() {
        Response r = target("/").request().get();
        assertEquals(200, r.getStatus());
        String html = r.readEntity(String.class);
        assertTrue(html.contains("Connect with GitHub"));
    }

    @Test
    public void testNotFoundRedirectsToRoot() {
        Response r = target("no-such").request().get();
        // Jersey test client follows redirects only if asked; we expect 303 See Other
        assertTrue(r.getStatus() == 303 || r.getStatus() == 302);
        String location = r.getHeaderString("Location");
        assertTrue(location != null && ("/".equals(location) || location.endsWith("/")));
    }
}
