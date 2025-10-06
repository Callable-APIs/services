package com.callableapis.api;

import com.callableapis.api.handlers.AuthResource;
import com.callableapis.api.handlers.v1.CalendarResource;
import com.callableapis.api.handlers.UserResource;
import com.callableapis.api.security.BearerAuthFilter;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class APIApplication extends ResourceConfig {
    public APIApplication() {
        // Resources
        register(CalendarResource.class);
        register(AuthResource.class);
        register(UserResource.class);
        // Filters
        register(BearerAuthFilter.class);
    }
}
