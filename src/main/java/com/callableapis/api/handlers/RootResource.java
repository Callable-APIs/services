package com.callableapis.api.handlers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class RootResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response index() {
        String html = """
                <!doctype html>
                <html lang=\"en\">
                <head>
                  <meta charset=\"utf-8\"/>
                  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>
                  <title>Callable APIs</title>
                  <style>
                    body { font-family: sans-serif; margin: 2rem; line-height: 1.5; }
                    code, pre { background: #f6f8fa; padding: 0.2rem 0.4rem; border-radius: 4px; }
                    pre { padding: 1rem; overflow: auto; }
                    a.button { display: inline-block; background: #2da44e; color: white; padding: 0.6rem 1rem; border-radius: 6px; text-decoration: none; }
                  </style>
                </head>
                <body>
                  <h1>Callable APIs</h1>
                  <p>Connect your GitHub account to obtain your API token.</p>
                  <p><a class=\"button\" href=\"/auth/login\">Connect with GitHub</a></p>
                  <h2>How it works</h2>
                  <ol>
                    <li>Click \"Connect with GitHub\" and authorize.</li>
                    <li>We return your <code>identity</code> and <code>apiKey</code>.</li>
                    <li>Send your API key as a Bearer token on each request.</li>
                  </ol>
                  <h2>Example</h2>
                  <p>Call the calendar endpoint (10 QPS limit):</p>
                  <pre><code>curl -H \"Authorization: Bearer YOUR_API_KEY\" \
  https://api.callableapis.com/v1/calendar/date
</code></pre>
                  <p>Rotate your key:</p>
                  <pre><code>curl -X POST -H \"Authorization: Bearer YOUR_API_KEY\" \
  https://api.callableapis.com/user/key/rotate
</code></pre>
                </body>
                </html>
                """;
        return Response.ok(html).build();
    }
}
