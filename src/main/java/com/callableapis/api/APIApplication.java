package com.callableapis.api;

import com.callableapis.api.handlers.AuthResource;
import com.callableapis.api.handlers.DebugResource;
import com.callableapis.api.handlers.TestResource;
import com.callableapis.api.handlers.TimeResource;
import com.callableapis.api.handlers.v1.CalendarResource;
import com.callableapis.api.handlers.v2.CalendarResourceV2;
import com.callableapis.api.handlers.v2.PlanetaryResourceV2;
import com.callableapis.api.handlers.v2.RandomResourceV2;
import com.callableapis.api.handlers.v2.InspirationResourceV2;
import com.callableapis.api.handlers.UserResource;
import com.callableapis.api.health.HealthController;
import com.callableapis.api.health.StatusController;
import com.callableapis.api.security.BearerAuthFilter;
import com.callableapis.api.di.AppBinder;
import com.callableapis.api.web.NotFoundRedirectMapper;
import com.callableapis.api.web.NotFoundRedirectFilter;
import com.callableapis.api.web.FallbackResource;
import com.callableapis.api.web.RootResource;
import com.callableapis.api.web.AuthenticatedResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

public class APIApplication extends ResourceConfig {
    public APIApplication() {
        // Resources
        register(TestResource.class);
        register(TimeResource.class);
        register(CalendarResource.class);
        register(CalendarResourceV2.class);
        register(PlanetaryResourceV2.class);
        register(RandomResourceV2.class);
        register(InspirationResourceV2.class);
        register(AuthResource.class);
        register(UserResource.class);
        register(HealthController.class);
        register(StatusController.class);
        register(DebugResource.class);
        // Filters
        register(BearerAuthFilter.class);

        // Dependency injection bindings
        register(new AppBinder());

        // Web resources
        register(RootResource.class);
        register(AuthenticatedResource.class);

        // Exception mappers
        register(NotFoundRedirectMapper.class);
        register(NotFoundRedirectFilter.class);
        register(FallbackResource.class);

        // MVC (JSP) support
        register(JspMvcFeature.class);
    }
}
