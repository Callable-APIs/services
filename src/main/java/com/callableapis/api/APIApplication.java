package com.callableapis.api;

import com.callableapis.api.handlers.AuthResource;
import com.callableapis.api.handlers.RootResource;
import com.callableapis.api.handlers.v1.CalendarResource;
import com.callableapis.api.handlers.UserResource;
import com.callableapis.api.health.HealthResource;
import com.callableapis.api.security.BearerAuthFilter;
import com.callableapis.api.di.AppBinder;
import com.callableapis.api.web.NotFoundRedirectMapper;
import com.callableapis.api.web.NotFoundRedirectFilter;
import com.callableapis.api.web.FallbackResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

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
        register(NotFoundRedirectFilter.class);
        register(FallbackResource.class);

        // MVC (JSP) support
        register(JspMvcFeature.class);
    }
}
