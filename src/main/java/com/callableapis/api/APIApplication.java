package com.callableapis.api;

import com.callableapis.api.handlers.v1.CalendarResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class APIApplication extends ResourceConfig {
    public APIApplication() {
        register(CalendarResource.class);
    }
}
