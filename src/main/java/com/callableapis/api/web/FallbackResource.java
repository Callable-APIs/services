package com.callableapis.api.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/{path: (?!auth|v1|user|health).+}")
public class FallbackResource {
    @GET
    public Response fallback() {
        return Response.seeOther(java.net.URI.create("/")).build();
    }
}
