<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!doctype html>
    <html lang="en">

    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Callable APIs - Authenticated</title>
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

            a.button.secondary {
                background: #6c757d;
            }

            a.button.secondary:hover {
                background: #545b62;
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

            .status-authenticated {
                background: #d4edda;
                color: #155724;
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

            .api-key-section {
                background: #f8f9fa;
                border: 1px solid #e1e4e8;
                border-radius: 8px;
                padding: 1.5rem;
                margin: 2rem 0;
            }

            .api-key-display {
                background: #2d3748;
                color: #e2e8f0;
                padding: 1rem;
                border-radius: 6px;
                font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
                word-break: break-all;
                margin: 1rem 0;
                position: relative;
            }

            .copy-button {
                position: absolute;
                top: 0.5rem;
                right: 0.5rem;
                background: #4a5568;
                color: white;
                border: none;
                padding: 0.25rem 0.5rem;
                border-radius: 4px;
                cursor: pointer;
                font-size: 0.75rem;
            }

            .copy-button:hover {
                background: #2d3748;
            }

            .example-section {
                background: #e3f2fd;
                border: 1px solid #bbdefb;
                border-radius: 8px;
                padding: 1.5rem;
                margin: 2rem 0;
            }

            .user-info {
                background: #f0f8ff;
                border: 1px solid #b3d9ff;
                border-radius: 6px;
                padding: 1rem;
                margin: 1rem 0;
            }

            .version-info {
                background: #f8f9fa;
                border-top: 1px solid #e1e4e8;
                padding: 1rem 2rem;
                text-align: center;
                font-size: 0.875rem;
                color: #6a737d;
                font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
            }

            .version-info a {
                color: #0366d6;
                text-decoration: none;
            }

            .version-info a:hover {
                text-decoration: underline;
            }

            .test-button {
                background: #007bff;
                color: white;
                border: none;
                padding: 0.5rem 1rem;
                border-radius: 4px;
                cursor: pointer;
                font-size: 0.875rem;
                margin: 0.5rem 0.5rem 0.5rem 0;
            }

            .test-button:hover {
                background: #0056b3;
            }

            .test-result {
                background: #f8f9fa;
                border: 1px solid #e1e4e8;
                border-radius: 4px;
                padding: 1rem;
                margin: 1rem 0;
                font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
                white-space: pre-wrap;
                max-height: 300px;
                overflow-y: auto;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <div class="header">
                <h1>🚀 Callable APIs</h1>
                <p>Welcome back, <strong>${userIdentity}</strong>!</p>
                <div style="margin-top: 1rem;">
                    <span class="status-badge status-authenticated">
                        ✅ Authenticated
                    </span>
                </div>
            </div>

            <div class="content">
                <!-- User Information -->
                <div class="user-info">
                    <h3>👤 Your Account</h3>
                    <p><strong>Identity:</strong> ${userIdentity}</p>
                    <p><strong>Status:</strong> Active and ready to use APIs</p>
                </div>

                <!-- API Key Section -->
                <div class="api-key-section">
                    <h3>🔑 Your API Key</h3>
                    <p>Use this API key to authenticate your requests. Keep it secure and don't share it publicly.</p>
                    <div class="api-key-display">
                        <button class="copy-button" onclick="copyApiKey()">Copy</button>
                        <span id="api-key">${apiKey}</span>
                    </div>
                    <p><small>💡 Click "Copy" to copy your API key to clipboard</small></p>
                </div>

                <!-- Example API Calls -->
                <div class="example-section">
                    <h3>💡 Example API Calls</h3>
                    <p>Here are some example commands you can run with your API key:</p>

                    <h4>📅 Get Current Date</h4>
                    <pre><code>curl -H "Authorization: Bearer ${apiKey}" \
https://api.callableapis.com/api/v1/calendar/date</code></pre>
                    <button class="test-button" onclick="testApi('/api/v1/calendar/date')">Test This API</button>

                    <h4>👤 Get Your Profile</h4>
                    <pre><code>curl -H "Authorization: Bearer ${apiKey}" \
https://api.callableapis.com/api/user/me</code></pre>
                    <button class="test-button" onclick="testApi('/api/user/me')">Test This API</button>

                    <h4>🔄 Rotate Your API Key</h4>
                    <pre><code>curl -X POST -H "Authorization: Bearer ${apiKey}" \
https://api.callableapis.com/api/user/key/rotate</code></pre>
                    <button class="test-button" onclick="testApi('/api/user/key/rotate', 'POST')">Test This API</button>

                    <div id="test-result" class="test-result" style="display: none;"></div>
                </div>

                <!-- Available Services -->
                <h3>🚀 Available Services</h3>
                <div class="api-grid">
                    <div class="api-card">
                        <h3>📅 Calendar API</h3>
                        <p>Get current date information with timezone support</p>
                        <div class="endpoint">
                            <span class="method method-get">GET</span>/api/v1/calendar/date
                        </div>
                        <div class="endpoint">
                            <span class="method method-get">GET</span>/api/v2/calendar/solar
                        </div>
                        <div class="endpoint">
                            <span class="method method-get">GET</span>/api/v2/calendar/moon
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
                        <h3>🪐 Planetary API</h3>
                        <p>Planetary positions and astrological data</p>
                        <div class="endpoint">
                            <span class="method method-get">GET</span>/api/v2/planetary/planets
                        </div>
                        <div class="endpoint">
                            <span class="method method-post">POST</span>/api/v2/planetary/position
                        </div>
                        <div class="endpoint">
                            <span class="method method-post">POST</span>/api/v2/planetary/constellation
                        </div>
                    </div>

                    <div class="api-card">
                        <h3>🎲 Random API</h3>
                        <p>Random number generation with multiple algorithms</p>
                        <div class="endpoint">
                            <span class="method method-get">GET</span>/api/v2/random/types
                        </div>
                        <div class="endpoint">
                            <span class="method method-post">POST</span>/api/v2/random/numbers
                        </div>
                        <div class="endpoint">
                            <span class="method method-post">POST</span>/api/v2/random/integers
                        </div>
                        <div class="endpoint">
                            <span class="method method-get">GET</span>/api/v2/random/boolean
                        </div>
                    </div>

                    <div class="api-card">
                        <h3>🔮 Inspiration API</h3>
                        <p>Mystical inspiration for AI agents</p>
                        <div class="endpoint">
                            <span class="method method-post">POST</span>/api/v2/inspiration/horoscope
                        </div>
                        <div class="endpoint">
                            <span class="method method-post">POST</span>/api/v2/inspiration/tarot
                        </div>
                        <div class="endpoint">
                            <span class="method method-get">GET</span>/api/v2/inspiration/tarot/single
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

                <!-- Actions -->
                <div style="text-align: center; margin: 2rem 0;">
                    <a href="/" class="button secondary">🏠 Back to Home</a>
                    <a href="/api/user/key/rotate" class="button"
                        onclick="return confirm('Are you sure you want to rotate your API key? This will invalidate your current key.')">🔄
                        Rotate API Key</a>
                </div>
            </div>

            <!-- Version Information -->
            <div class="version-info">
                <div>Deployed:
                    <% if (!"unknown".equals(request.getAttribute("gitCommitHash"))) { %>
                        <a href="https://github.com/Callable-APIs/services/commit/${gitCommitHash}" target="_blank"
                            rel="noopener">${shortCommitHash}</a>
                        <% } else { %>
                            <span>${shortCommitHash}</span>
                            <% } %>
                                • Built: ${buildTime}
                </div>
            </div>
        </div>

        <script>
            function copyApiKey() {
                const apiKey = document.getElementById('api-key').textContent;
                navigator.clipboard.writeText(apiKey).then(function () {
                    const button = document.querySelector('.copy-button');
                    const originalText = button.textContent;
                    button.textContent = 'Copied!';
                    button.style.background = '#28a745';
                    setTimeout(function () {
                        button.textContent = originalText;
                        button.style.background = '#4a5568';
                    }, 2000);
                }).catch(function (err) {
                    console.error('Could not copy text: ', err);
                    alert('Could not copy to clipboard. Please copy manually.');
                });
            }

            async function testApi(endpoint, method = 'GET') {
                const resultDiv = document.getElementById('test-result');
                const apiKey = document.getElementById('api-key').textContent;

                resultDiv.style.display = 'block';
                resultDiv.textContent = 'Testing...';

                try {
                    const response = await fetch(endpoint, {
                        method: method,
                        headers: {
                            'Authorization': 'Bearer ' + apiKey,
                            'Content-Type': 'application/json'
                        }
                    });

                    const data = await response.text();
                    resultDiv.textContent = `Status: ${response.status}\n\n${data}`;
                } catch (error) {
                    resultDiv.textContent = `Error: ${error.message}`;
                }
            }
        </script>
    </body>

    </html>