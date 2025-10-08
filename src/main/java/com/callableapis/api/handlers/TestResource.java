package com.callableapis.api.handlers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/test")
public class TestResource {

    @GET
    @Path("/simple")
    @Produces(MediaType.TEXT_PLAIN)
    public Response simple() {
        System.out.println("=== TestResource.simple() called ===");
        return Response.ok("Jersey is working!").build();
    }
    
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response json() {
        System.out.println("=== TestResource.json() called ===");
        return Response.ok("{\"status\": \"success\", \"message\": \"Jersey JSON is working!\"}").build();
    }
}
