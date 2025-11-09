package com.callableapis.api.di;

import com.callableapis.api.security.ApiKeyService;
import com.callableapis.api.security.ApiKeyStore;
import com.callableapis.api.security.AuthenticationStatsService;
import com.callableapis.api.security.RateLimitService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class AppBinder extends AbstractBinder {
    @Override
    protected void configure() {
        ApiKeyService instance = ApiKeyService.getInstance();
        // Bind to interfaces for resources that use interfaces
        bind(instance).to(ApiKeyStore.class);
        bind(instance).to(RateLimitService.class);
        // Also bind to concrete class for resources that inject ApiKeyService directly
        bind(instance).to(ApiKeyService.class);
        
        // Register AuthenticationStatsService as a singleton for dependency injection
        // This service tracks authentication statistics across all requests
        bind(new AuthenticationStatsService()).to(AuthenticationStatsService.class);
    }
}
