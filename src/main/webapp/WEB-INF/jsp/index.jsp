<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Callable APIs</title>
  <style>
    body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, 'Helvetica Neue', Arial, 'Noto Sans', 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol'; margin: 2rem; line-height: 1.6; }
    code, pre { background: #f6f8fa; padding: 0.2rem 0.4rem; border-radius: 4px; }
    pre { padding: 1rem; overflow: auto; }
    a.button { display: inline-block; background: #2da44e; color: white; padding: 0.6rem 1rem; border-radius: 6px; text-decoration: none; }
    .container { max-width: 800px; margin: 0 auto; }
  </style>
</head>
<body>
<div class="container">
  <h1>Callable APIs</h1>
  <p>Connect your GitHub account to obtain your API token.</p>
  <p><a class="button" href="/auth/login">Connect with GitHub</a></p>

  <h2>How it works</h2>
  <ol>
    <li>Click <strong>Connect with GitHub</strong> and authorize.</li>
    <li>We return your <code>identity</code> and <code>apiKey</code>.</li>
    <li>Send your API key as a Bearer token on each request.</li>
  </ol>

  <h2>Example</h2>
  <p>Call the calendar endpoint (10 QPS limit):</p>
  <pre><code>curl -H "Authorization: Bearer YOUR_API_KEY" \
https://api.callableapis.com/v1/calendar/date
</code></pre>

  <p>Rotate your key:</p>
  <pre><code>curl -X POST -H "Authorization: Bearer YOUR_API_KEY" \
https://api.callableapis.com/user/key/rotate
</code></pre>
</div>
</body>
</html>
