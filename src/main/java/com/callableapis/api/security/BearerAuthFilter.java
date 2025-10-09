package com.callableapis.api.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class BearerAuthFilter implements ContainerRequestFilter {

    @Inject
    private ApiKeyStore apiKeyStore;

    @Inject
    private RateLimitService rateLimitService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Only protect API endpoints; leave others public for docs/redirects
        String rawPath = requestContext.getUriInfo().getPath();
        String path = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;
        boolean isProtectedApi = path.startsWith("v1/") || path.startsWith("v2/") || path.startsWith("user/");
        if (!isProtectedApi) {
            return;
        }

        String authHeader = Optional.ofNullable(requestContext.getHeaderString("Authorization")).orElse("");
        if (!authHeader.startsWith("Bearer ")) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Missing Bearer token");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Empty token");
            return;
        }

        Optional<String> identityOpt = apiKeyStore.findIdentityByApiKey(token);
        if (identityOpt.isEmpty()) {
            abort(requestContext, Response.Status.FORBIDDEN, "Invalid API key");
            return;
        }

        // Rate limit before proceeding
        if (!rateLimitService.tryAcquire(token)) {
            abort(requestContext, Response.Status.TOO_MANY_REQUESTS, "Rate limit exceeded");
            return;
        }
        // Optionally, we could set a security context or property with the identity
        requestContext.setProperty("api.identity", identityOpt.get());
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, String message) {
        ctx.abortWith(Response.status(status)
                .entity(message)
                .build());
    }
}
