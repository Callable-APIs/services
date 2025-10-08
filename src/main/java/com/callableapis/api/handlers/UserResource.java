package com.callableapis.api.handlers;

import com.callableapis.api.security.ApiKeyStore;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.inject.Inject;

import java.util.Map;

@Path("/user")
public class UserResource {
    @Inject
    private ApiKeyStore apiKeyStore;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response me(@Context ContainerRequestContext ctx) {
        String identity = (String) ctx.getProperty("api.identity");
        if (identity == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
        String apiKey = apiKeyStore.getOrCreateApiKeyForIdentity(identity);
        return Response.ok(Map.of(
                "identity", identity,
                "apiKey", apiKey
        )).build();
    }

    @POST
    @Path("/key/rotate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rotate(@Context ContainerRequestContext ctx) {
        String identity = (String) ctx.getProperty("api.identity");
        if (identity == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
        String apiKey = apiKeyStore.rotateApiKeyForIdentity(identity);
        return Response.ok(Map.of(
                "identity", identity,
                "apiKey", apiKey
        )).build();
    }
}
