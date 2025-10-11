package com.callableapis.api.web;

import com.callableapis.api.config.VersionService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.util.logging.Logger;

@Path("/")
public class RootResource {
    private static final Logger logger = Logger.getLogger(RootResource.class.getName());

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getRootPage() {
        logger.info("RootResource.getRootPage() called!");

        // Add version information
        VersionService versionService = VersionService.getInstance();
        String gitCommitHash = versionService.getGitCommitHash();
        String buildTime = versionService.getBuildTime();

        logger.info("Version info - Commit: " + gitCommitHash + ", Build: " + buildTime);

        // Redirect to the servlet-mapped path
        return Response.seeOther(UriBuilder.fromPath("/index").build()).build();
    }
}
