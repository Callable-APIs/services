package com.callableapis.api;

import com.callableapis.api.handlers.AuthResource;
import com.callableapis.api.handlers.RootResource;
import com.callableapis.api.handlers.v1.CalendarResource;
import com.callableapis.api.handlers.UserResource;
import com.callableapis.api.health.HealthResource;
import com.callableapis.api.security.BearerAuthFilter;
import com.callableapis.api.di.AppBinder;
import com.callableapis.api.web.NotFoundRedirectMapper;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class APIApplication extends ResourceConfig {
    public APIApplication() {
        // Resources
        register(CalendarResource.class);
        register(AuthResource.class);
        register(UserResource.class);
        register(RootResource.class);
        register(HealthResource.class);
        // Filters
        register(BearerAuthFilter.class);

        // Dependency injection bindings
        register(new AppBinder());

        // Exception mappers
        register(NotFoundRedirectMapper.class);
    }
}
