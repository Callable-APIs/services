package com.callableapis.api.handlers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.server.mvc.Viewable;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Path("")
public class RootResource {
    @Context
    private ServletContext servletContext;
    
    @Context
    private ContainerRequestContext requestContext;
    
    @Context
    private HttpServletRequest httpRequest;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object index() {
        // Fallback to inline HTML when JSP engine isn't available (e.g., JerseyTest/Grizzly)
        if (servletContext == null) {
            String html = """
                    <!doctype html>
                    <html lang="en">
                    <head>
                      <meta charset="utf-8"/>
                      <meta name="viewport" content="width=device-width, initial-scale=1"/>
                      <title>Callable APIs</title>
                    </head>
                    <body>
                      <h1>Callable APIs</h1>
                      <p>Connect your GitHub account to obtain your API token.</p>
                      <p><a href="/api/auth/login">Connect with GitHub</a></p>
                      <h2>Example</h2>
                      <pre><code>curl -H "Authorization: Bearer YOUR_API_KEY" \
https://api.callableapis.com/api/v1/calendar/date
</code></pre>
                    </body>
                    </html>
                    """;
            return Response.ok(html).build();
        }
        
        // Prepare data for JSP
        Map<String, Object> model = new HashMap<>();
        
        // Check if user is authenticated via Bearer token
        String identity = (String) requestContext.getProperty("api.identity");
        boolean isAuthenticated = identity != null && !identity.isEmpty();
        
        model.put("isAuthenticated", isAuthenticated);
        model.put("identity", identity);
        
        // Check for Authorization header for display purposes
        String authHeader = httpRequest.getHeader("Authorization");
        boolean hasBearerToken = authHeader != null && authHeader.startsWith("Bearer ");
        model.put("hasBearerToken", hasBearerToken);
        
        return new Viewable("/WEB-INF/jsp/index.jsp", model);
    }
}
