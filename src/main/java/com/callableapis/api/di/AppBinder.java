package com.callableapis.api.di;

import com.callableapis.api.security.ApiKeyService;
import com.callableapis.api.security.ApiKeyStore;
import com.callableapis.api.security.RateLimitService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class AppBinder extends AbstractBinder {
    @Override
    protected void configure() {
        ApiKeyService instance = ApiKeyService.getInstance();
        bind(instance).to(ApiKeyStore.class);
        bind(instance).to(RateLimitService.class);
    }
}
