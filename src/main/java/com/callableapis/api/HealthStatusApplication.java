package com.callableapis.api;

import com.callableapis.api.health.HealthResource;
import com.callableapis.api.health.StatusResource;
import com.callableapis.api.di.AppBinder;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey application for health and status endpoints at root level.
 * This serves /health and /status without the /api prefix.
 */
public class HealthStatusApplication extends ResourceConfig {
    public HealthStatusApplication() {
        // Register health and status resources
        register(HealthResource.class);
        register(StatusResource.class);

        // Dependency injection bindings
        register(new AppBinder());
    }
}
