package com.callableapis.api.handlers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/test")
public class TestResource {
	private static final Logger logger = Logger.getLogger(TestResource.class.getName());

	@GET
	@Path("/simple")
	@Produces(MediaType.TEXT_PLAIN)
	public Response simple() {
		logger.info("=== TestResource.simple() called ===");
		return Response.ok("Jersey is working!").build();
	}
	
	@GET
	@Path("/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response json() {
		logger.info("=== TestResource.json() called ===");
		return Response.ok("{\"status\": \"success\", \"message\": \"Jersey JSON is working!\"}").build();
	}
}
