package com.callableapis.api.security;

import com.callableapis.api.config.AppConfig;
import com.google.common.util.concurrent.RateLimiter;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ApiKeyService {
    private static final ApiKeyService INSTANCE = new ApiKeyService();

    private final ConcurrentHashMap<String, String> identityToApiKey = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> apiKeyToIdentity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RateLimiter> apiKeyToLimiter = new ConcurrentHashMap<>();

    private ApiKeyService() {}

    public static ApiKeyService getInstance() { return INSTANCE; }

    public String getOrCreateApiKeyForIdentity(String oidcIdentity) {
        Objects.requireNonNull(oidcIdentity, "oidcIdentity");
        return identityToApiKey.computeIfAbsent(oidcIdentity, id -> {
            String apiKey = CryptoUtils.computeApiKeyForIdentity(id);
            apiKeyToIdentity.put(apiKey, id);
            return apiKey;
        });
    }

    public String rotateApiKeyForIdentity(String oidcIdentity) {
        Objects.requireNonNull(oidcIdentity, "oidcIdentity");
        String oldKey = identityToApiKey.remove(oidcIdentity);
        if (oldKey != null) {
            apiKeyToIdentity.remove(oldKey);
            apiKeyToLimiter.remove(oldKey);
        }
        String newKey = CryptoUtils.computeApiKeyForIdentity(oidcIdentity + ":" + System.nanoTime());
        identityToApiKey.put(oidcIdentity, newKey);
        apiKeyToIdentity.put(newKey, oidcIdentity);
        return newKey;
    }

    public Optional<String> findIdentityByApiKey(String apiKey) {
        return Optional.ofNullable(apiKeyToIdentity.get(apiKey));
    }

    public RateLimiter getRateLimiterForApiKey(String apiKey) {
        double permitsPerSecond = Math.max(1, AppConfig.getRateLimitQps());
        return apiKeyToLimiter.computeIfAbsent(apiKey, k -> RateLimiter.create(permitsPerSecond));
    }
}
