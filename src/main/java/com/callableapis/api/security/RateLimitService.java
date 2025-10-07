package com.callableapis.api.security;

public interface RateLimitService {
    boolean tryAcquire(String apiKey);
}
