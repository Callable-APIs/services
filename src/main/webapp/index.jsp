<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <!doctype html>
  <html lang="en">

  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Callable APIs</title>
    <style>
      body {
        font-family: system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, 'Helvetica Neue', Arial, 'Noto Sans', 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
        margin: 0;
        padding: 2rem;
        line-height: 1.6;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
      }

      .container {
        max-width: 1000px;
        margin: 0 auto;
        background: white;
        border-radius: 12px;
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
        overflow: hidden;
      }

      .header {
        background: linear-gradient(135deg, #2da44e 0%, #1a7f37 100%);
        color: white;
        padding: 2rem;
        text-align: center;
      }

      .content {
        padding: 2rem;
      }

      code,
      pre {
        background: #f6f8fa;
        padding: 0.2rem 0.4rem;
        border-radius: 4px;
        font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
      }

      pre {
        padding: 1rem;
        overflow: auto;
        background: #f6f8fa;
        border: 1px solid #e1e4e8;
        border-radius: 6px;
      }

      a.button {
        display: inline-block;
        background: #2da44e;
        color: white;
        padding: 0.75rem 1.5rem;
        border-radius: 6px;
        text-decoration: none;
        font-weight: 500;
        transition: background-color 0.2s;
      }

      a.button:hover {
        background: #1a7f37;
      }

      .api-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 1.5rem;
        margin: 2rem 0;
      }

      .api-card {
        border: 1px solid #e1e4e8;
        border-radius: 8px;
        padding: 1.5rem;
        background: #f8f9fa;
      }

      .api-card h3 {
        margin-top: 0;
        color: #2da44e;
      }

      .status-badge {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        border-radius: 12px;
        font-size: 0.875rem;
        font-weight: 500;
      }

      .status-unauthenticated {
        background: #fef3c7;
        color: #92400e;
      }

      .endpoint {
        background: #f6f8fa;
        border: 1px solid #e1e4e8;
        border-radius: 4px;
        padding: 0.5rem;
        margin: 0.5rem 0;
        font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
      }

      .method {
        display: inline-block;
        padding: 0.2rem 0.5rem;
        border-radius: 3px;
        font-size: 0.75rem;
        font-weight: bold;
        margin-right: 0.5rem;
      }

      .method-get {
        background: #dbeafe;
        color: #1e40af;
      }

      .method-post {
        background: #fef3c7;
        color: #92400e;
      }
    </style>
  </head>

  <body>
    <div class="container">
      <div class="header">
        <h1>🚀 Callable APIs</h1>
        <p>Modern REST API Platform with OIDC Authentication</p>
        <div style="margin-top: 1rem;">
          <span class="status-badge status-unauthenticated">
            🔒 Not Authenticated
          </span>
        </div>
      </div>

      <div class="content">
        <!-- API Landing Page for Unauthenticated Users -->
        <h2>🔑 Get Started with OIDC Authentication</h2>
        <p>Connect your GitHub account to obtain your API token and start using our services.</p>
        <p><a class="button" href="${oauthLoginUrl}">🔗 Connect with GitHub</a></p>

        <h3>📋 How it works</h3>
        <ol>
          <li>Click <strong>Connect with GitHub</strong> and authorize the application</li>
          <li>We'll return your <code>identity</code> and <code>apiKey</code></li>
          <li>Send your API key as a Bearer token on each request</li>
          <li>Enjoy rate-limited access to all our services!</li>
        </ol>

        <h3>🚀 Available Services</h3>
        <div class="api-grid">
          <div class="api-card">
            <h3>📅 Calendar API</h3>
            <p>Get current date information with timezone support</p>
            <div class="endpoint">
              <span class="method method-get">GET</span>/api/v1/calendar/date
            </div>
          </div>

          <div class="api-card">
            <h3>👤 User Management</h3>
            <p>Manage your API keys and account</p>
            <div class="endpoint">
              <span class="method method-get">GET</span>/api/user/me
            </div>
            <div class="endpoint">
              <span class="method method-post">POST</span>/api/user/key/rotate
            </div>
          </div>

          <div class="api-card">
            <h3>🔐 Authentication</h3>
            <p>OIDC authentication and token management</p>
            <div class="endpoint">
              <span class="method method-get">GET</span>/api/auth/login
            </div>
            <div class="endpoint">
              <span class="method method-get">GET</span>${oauthCallbackUrl}
            </div>
          </div>

          <div class="api-card">
            <h3>💚 Health Check</h3>
            <p>Service health and status monitoring</p>
            <div class="endpoint">
              <span class="method method-get">GET</span>/api/health
            </div>
          </div>
        </div>

        <h3>💡 Example Usage</h3>
        <p>Once authenticated, call the calendar endpoint (10 QPS limit):</p>
        <pre><code>curl -H "Authorization: Bearer YOUR_API_KEY" \
https://api.callableapis.com/api/v1/calendar/date</code></pre>

        <p>Rotate your API key for enhanced security:</p>
        <pre><code>curl -X POST -H "Authorization: Bearer YOUR_API_KEY" \
https://api.callableapis.com/api/user/key/rotate</code></pre>

        <div style="background: #f0f8ff; border: 1px solid #b3d9ff; border-radius: 6px; padding: 1rem; margin: 2rem 0;">
          <h4>🔒 Security Features</h4>
          <ul>
            <li><strong>OIDC Authentication:</strong> Secure GitHub OAuth integration</li>
            <li><strong>Bearer Token Auth:</strong> Industry-standard API authentication</li>
            <li><strong>Rate Limiting:</strong> 10 QPS per API key to prevent abuse</li>
            <li><strong>Key Rotation:</strong> Rotate your API keys anytime for enhanced security</li>
          </ul>
        </div>
      </div>
    </div>
  </body>

  </html>