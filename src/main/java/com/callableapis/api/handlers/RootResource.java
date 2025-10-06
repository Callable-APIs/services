package com.callableapis.api.handlers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.server.mvc.Viewable;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.servlet.ServletContext;

@Path("")
public class RootResource {
    @Context
    private ServletContext servletContext;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object index() {
        // Fallback to inline HTML when JSP engine isn't available (e.g., JerseyTest/Grizzly)
        if (servletContext == null) {
            String html = """
                    <!doctype html>
                    <html lang=\"en\">
                    <head>
                      <meta charset=\"utf-8\"/>
                      <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>
                      <title>Callable APIs</title>
                    </head>
                    <body>
                      <h1>Callable APIs</h1>
                      <p>Connect your GitHub account to obtain your API token.</p>
                      <p><a href=\"/auth/login\">Connect with GitHub</a></p>
                      <h2>Example</h2>
                      <pre><code>curl -H \"Authorization: Bearer YOUR_API_KEY\" \\\n+https://api.callableapis.com/v1/calendar/date
""";
            return Response.ok(html).build();
        }
        return new Viewable("/WEB-INF/jsp/index.jsp");
    }
}
