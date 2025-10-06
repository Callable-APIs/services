package com.callableapis.api;

import com.callableapis.api.security.ApiKeyService;
import org.junit.Test;
import static org.junit.Assert.*;

public class AuthAndApiFilterTest {
    @Test
    public void testApiKeyServiceRoundTrip() {
        String identity = "github:testuser";
        String key = ApiKeyService.getInstance().getOrCreateApiKeyForIdentity(identity);
        assertNotNull(key);
        assertTrue(ApiKeyService.getInstance().findIdentityByApiKey(key).isPresent());
    }
}
