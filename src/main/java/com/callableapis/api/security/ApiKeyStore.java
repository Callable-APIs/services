package com.callableapis.api.security;

import java.util.Optional;

public interface ApiKeyStore {
    String getOrCreateApiKeyForIdentity(String oidcIdentity);
    String rotateApiKeyForIdentity(String oidcIdentity);
    Optional<String> findIdentityByApiKey(String apiKey);
}
