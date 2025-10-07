package com.callableapis.api.web;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Context;

@Provider
public class NotFoundRedirectMapper implements ExceptionMapper<NotFoundException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(NotFoundException exception) {
        java.net.URI location = (uriInfo != null) ? uriInfo.getBaseUriBuilder().path("/").build()
                : java.net.URI.create("/");
        return Response.seeOther(location).build();
    }
}
