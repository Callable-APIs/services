package com.callableapis.api.web;

import com.callableapis.api.config.VersionService;
import com.callableapis.api.security.ApiKeyStore;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;
import java.util.logging.Logger;

@Path("/authenticated")
public class AuthenticatedResource {
    private static final Logger logger = Logger.getLogger(AuthenticatedResource.class.getName());

    @Inject
    private ApiKeyStore apiKeyStore;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getAuthenticatedPage(@QueryParam("identity") String identity, @QueryParam("apiKey") String apiKey) {
        logger.info("AuthenticatedResource.getAuthenticatedPage() called!");

        logger.info("Parameters - identity: " + identity + ", apiKey: "
                + (apiKey != null ? "***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) : "null"));

        if (identity == null || identity.isBlank() || apiKey == null || apiKey.isBlank()) {
            logger.warning("Missing identity or apiKey parameters, redirecting to home");
            return Response.seeOther(java.net.URI.create("/")).build();
        }

        // Verify the API key belongs to this identity by checking if it maps back to
        // the identity
        Optional<String> storedIdentity = apiKeyStore.findIdentityByApiKey(apiKey);
        if (storedIdentity.isEmpty() || !storedIdentity.get().equals(identity)) {
            logger.warning("Invalid API key for identity: " + identity);
            return Response.seeOther(java.net.URI.create("/")).build();
        }

        // Add version information
        VersionService versionService = VersionService.getInstance();
        String gitCommitHash = versionService.getGitCommitHash();
        String shortCommitHash = versionService.getShortCommitHash();
        String buildTime = versionService.getBuildTime();

        logger.info("Version info - Commit: " + gitCommitHash + ", Build: " + buildTime);

        // Generate HTML with the user data
        String html = generateAuthenticatedHtml(identity, apiKey, gitCommitHash, shortCommitHash, buildTime);
        return Response.ok(html).build();
    }

    private String generateAuthenticatedHtml(String identity, String apiKey, String gitCommitHash,
            String shortCommitHash, String buildTime) {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"utf-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
                "<title>Callable APIs - Authenticated</title>" +
                "<style>" +
                generateEnhancedStyles() +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                generateHeader(identity) +
                generateTabNavigation() +
                generateTabContent(identity, apiKey, gitCommitHash, shortCommitHash, buildTime) +
                generateVersionInfo(shortCommitHash, buildTime) +
                "</div>" +
                generateJavaScript(apiKey) +
                "</body>" +
                "</html>";
    }

    private String generateEnhancedStyles() {
        return "body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, 'Helvetica Neue', Arial, 'Noto Sans', 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol'; margin: 0; padding: 2rem; line-height: 1.6; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }"
                +
                ".container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 12px; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1); overflow: hidden; }"
                +
                ".header { background: linear-gradient(135deg, #2da44e 0%, #1a7f37 100%); color: white; padding: 2rem; text-align: center; }"
                +
                ".tab-navigation { display: flex; background: #f8f9fa; border-bottom: 1px solid #e1e4e8; }" +
                ".tab-button { flex: 1; padding: 1rem 2rem; background: none; border: none; cursor: pointer; font-size: 1rem; font-weight: 500; color: #6a737d; transition: all 0.2s; border-bottom: 3px solid transparent; }"
                +
                ".tab-button:hover { background: #e9ecef; color: #495057; }" +
                ".tab-button.active { color: #2da44e; border-bottom-color: #2da44e; background: white; }" +
                ".tab-content { display: none; padding: 2rem; min-height: 500px; }" +
                ".tab-content.active { display: block; }" +
                ".sub-tab-navigation { display: flex; margin-bottom: 1.5rem; border-bottom: 1px solid #e1e4e8; }" +
                ".sub-tab-button { padding: 0.75rem 1.5rem; background: none; border: none; cursor: pointer; font-size: 0.9rem; color: #6a737d; transition: all 0.2s; border-bottom: 2px solid transparent; }"
                +
                ".sub-tab-button:hover { color: #495057; }" +
                ".sub-tab-button.active { color: #2da44e; border-bottom-color: #2da44e; }" +
                ".sub-tab-content { display: none; }" +
                ".sub-tab-content.active { display: block; }" +
                "code, pre { background: #f6f8fa; padding: 0.2rem 0.4rem; border-radius: 4px; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; }"
                +
                "pre { padding: 1rem; overflow: auto; background: #f6f8fa; border: 1px solid #e1e4e8; border-radius: 6px; }"
                +
                ".button { display: inline-block; background: #2da44e; color: white; padding: 0.75rem 1.5rem; border-radius: 6px; text-decoration: none; font-weight: 500; transition: background-color 0.2s; border: none; cursor: pointer; }"
                +
                ".button:hover { background: #1a7f37; }" +
                ".button.secondary { background: #6c757d; }" +
                ".button.secondary:hover { background: #545b62; }" +
                ".button.danger { background: #dc3545; }" +
                ".button.danger:hover { background: #c82333; }" +
                ".api-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; margin: 2rem 0; }"
                +
                ".api-card { border: 1px solid #e1e4e8; border-radius: 8px; padding: 1.5rem; background: #f8f9fa; }" +
                ".api-card h3 { margin-top: 0; color: #2da44e; }" +
                ".status-badge { display: inline-block; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.875rem; font-weight: 500; }"
                +
                ".status-authenticated { background: #d4edda; color: #155724; }" +
                ".status-healthy { background: #d1fae5; color: #065f46; }" +
                ".status-warning { background: #fef3c7; color: #92400e; }" +
                ".status-error { background: #fecaca; color: #991b1b; }" +
                ".endpoint { background: #f6f8fa; border: 1px solid #e1e4e8; border-radius: 4px; padding: 0.5rem; margin: 0.5rem 0; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; }"
                +
                ".method { display: inline-block; padding: 0.2rem 0.5rem; border-radius: 3px; font-size: 0.75rem; font-weight: bold; margin-right: 0.5rem; }"
                +
                ".method-get { background: #dbeafe; color: #1e40af; }" +
                ".method-post { background: #fef3c7; color: #92400e; }" +
                ".method-put { background: #d1fae5; color: #065f46; }" +
                ".method-delete { background: #fecaca; color: #991b1b; }" +
                ".api-key-section { background: #f8f9fa; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1.5rem; margin: 2rem 0; }"
                +
                ".api-key-display { background: #2d3748; color: #e2e8f0; padding: 1rem; border-radius: 6px; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; word-break: break-all; margin: 1rem 0; position: relative; }"
                +
                ".copy-button { position: absolute; top: 0.5rem; right: 0.5rem; background: #4a5568; color: white; border: none; padding: 0.25rem 0.5rem; border-radius: 4px; cursor: pointer; font-size: 0.75rem; }"
                +
                ".copy-button:hover { background: #2d3748; }" +
                ".user-info { background: #f0f8ff; border: 1px solid #b3d9ff; border-radius: 6px; padding: 1rem; margin: 1rem 0; }"
                +
                ".version-info { background: #f8f9fa; border-top: 1px solid #e1e4e8; padding: 1rem 2rem; text-align: center; font-size: 0.875rem; color: #6a737d; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; }"
                +
                ".version-info a { color: #0366d6; text-decoration: none; }" +
                ".version-info a:hover { text-decoration: underline; }" +
                ".test-button { background: #007bff; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; margin: 0.5rem 0.5rem 0.5rem 0; }"
                +
                ".test-button:hover { background: #0056b3; }" +
                ".test-result { background: #f8f9fa; border: 1px solid #e1e4e8; border-radius: 4px; padding: 1rem; margin: 1rem 0; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; white-space: pre-wrap; max-height: 300px; overflow-y: auto; }"
                +
                ".health-table { width: 100%; border-collapse: collapse; margin: 1rem 0; }" +
                ".health-table th, .health-table td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #e1e4e8; }"
                +
                ".health-table th { background: #f8f9fa; font-weight: 600; }" +
                ".health-table tr:hover { background: #f8f9fa; }" +
                ".stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin: 1rem 0; }"
                +
                ".stat-card { background: #f8f9fa; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1rem; text-align: center; }"
                +
                ".stat-value { font-size: 2rem; font-weight: bold; color: #2da44e; }" +
                ".stat-label { font-size: 0.875rem; color: #6a737d; margin-top: 0.5rem; }" +
                ".loading { text-align: center; padding: 2rem; color: #6a737d; }" +
                ".error { background: #fecaca; color: #991b1b; padding: 1rem; border-radius: 6px; margin: 1rem 0; }" +
                ".success { background: #d1fae5; color: #065f46; padding: 1rem; border-radius: 6px; margin: 1rem 0; }" +
                ".info-box { background: #e3f2fd; border: 1px solid #bbdefb; border-radius: 8px; padding: 1rem; margin: 1rem 0; }"
                +
                ".warning-box { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 8px; padding: 1rem; margin: 1rem 0; }"
                +
                ".endpoint-doc { background: #f8f9fa; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1.5rem; margin: 1rem 0; }"
                +
                ".endpoint-doc h4 { margin-top: 0; color: #2da44e; }" +
                ".endpoint-doc .description { color: #6a737d; margin: 0.5rem 0; }" +
                ".endpoint-doc .parameters { background: #f1f3f4; padding: 1rem; border-radius: 4px; margin: 1rem 0; }"
                +
                ".endpoint-doc .response { background: #e8f5e8; padding: 1rem; border-radius: 4px; margin: 1rem 0; }" +
                ".parameter { margin: 0.5rem 0; }" +
                ".parameter-name { font-weight: bold; color: #2da44e; }" +
                ".parameter-type { color: #6a737d; font-style: italic; }" +
                ".parameter-required { color: #dc3545; font-weight: bold; }" +
                ".parameter-optional { color: #6c757d; }" +
                ".response-example { background: #2d3748; color: #e2e8f0; padding: 1rem; border-radius: 4px; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; overflow-x: auto; }"
                +
                ".refresh-button { background: #17a2b8; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; }"
                +
                ".refresh-button:hover { background: #138496; }" +
                ".auto-refresh { display: flex; align-items: center; gap: 1rem; margin: 1rem 0; }" +
                ".auto-refresh input[type='checkbox'] { margin-right: 0.5rem; }" +
                "@media (max-width: 768px) { .tab-navigation { flex-direction: column; } .tab-button { border-bottom: 1px solid #e1e4e8; border-right: none; } .sub-tab-navigation { flex-wrap: wrap; } .api-grid { grid-template-columns: 1fr; } .stats-grid { grid-template-columns: repeat(2, 1fr); } }";
    }

    private String generateHeader(String identity) {
        return "<div class=\"header\">" +
                "<h1>🚀 Callable APIs</h1>" +
                "<p>Welcome back, <strong>" + identity + "</strong>!</p>" +
                "<div style=\"margin-top: 1rem;\">" +
                "<span class=\"status-badge status-authenticated\">✅ Authenticated</span>" +
                "</div>" +
                "</div>";
    }

    private String generateTabNavigation() {
        return "<div class=\"tab-navigation\">" +
                "<button class=\"tab-button active\" onclick=\"showTab('account')\">👤 Account & Keys</button>" +
                "<button class=\"tab-button\" onclick=\"showTab('services')\">💚 Services & Health</button>" +
                "<button class=\"tab-button\" onclick=\"showTab('documentation')\">📚 API Documentation</button>" +
                "</div>";
    }

    private String generateTabContent(String identity, String apiKey, String gitCommitHash, String shortCommitHash,
            String buildTime) {
        return "<div id=\"account-tab\" class=\"tab-content active\">" +
                generateAccountTab(identity, apiKey) +
                "</div>" +
                "<div id=\"services-tab\" class=\"tab-content\">" +
                generateServicesTab() +
                "</div>" +
                "<div id=\"documentation-tab\" class=\"tab-content\">" +
                generateDocumentationTab(apiKey) +
                "</div>";
    }

    private String generateAccountTab(String identity, String apiKey) {
        return "<div class=\"user-info\">" +
                "<h3>👤 Account Information</h3>" +
                "<p><strong>Identity:</strong> " + identity + "</p>" +
                "<p><strong>Status:</strong> Active and ready to use APIs</p>" +
                "<p><strong>Authentication:</strong> GitHub OAuth</p>" +
                "</div>" +
                "<div class=\"api-key-section\">" +
                "<h3>🔑 API Key Management</h3>" +
                "<p>Use this API key to authenticate your requests. Keep it secure and don't share it publicly.</p>" +
                "<div class=\"api-key-display\">" +
                "<button class=\"copy-button\" onclick=\"copyApiKey()\">Copy</button>" +
                "<span id=\"api-key\">" + apiKey + "</span>" +
                "</div>" +
                "<p><small>💡 Click \"Copy\" to copy your API key to clipboard</small></p>" +
                "<div style=\"margin-top: 1rem;\">" +
                "<button class=\"button danger\" onclick=\"rotateApiKey()\">🔄 Rotate API Key</button>" +
                "<p><small>⚠️ Rotating your API key will invalidate the current key</small></p>" +
                "</div>" +
                "</div>" +
                "<div class=\"stats-grid\">" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"api-calls-count\">-</div>" +
                "<div class=\"stat-label\">API Calls Today</div>" +
                "</div>" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"rate-limit-remaining\">-</div>" +
                "<div class=\"stat-label\">Rate Limit Remaining</div>" +
                "</div>" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"last-activity\">-</div>" +
                "<div class=\"stat-label\">Last Activity</div>" +
                "</div>" +
                "</div>";
    }

    private String generateServicesTab() {
        return "<div class=\"auto-refresh\">" +
                "<button class=\"refresh-button\" onclick=\"refreshHealthData()\">🔄 Refresh</button>" +
                "<label><input type=\"checkbox\" id=\"auto-refresh\" onchange=\"toggleAutoRefresh()\"> Auto-refresh every 30s</label>"
                +
                "</div>" +
                "<div id=\"health-data\">" +
                "<div class=\"loading\">Loading health data...</div>" +
                "</div>" +
                "<div class=\"stats-grid\">" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"uptime\">-</div>" +
                "<div class=\"stat-label\">Uptime</div>" +
                "</div>" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"response-time\">-</div>" +
                "<div class=\"stat-label\">Avg Response Time</div>" +
                "</div>" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"error-rate\">-</div>" +
                "<div class=\"stat-label\">Error Rate</div>" +
                "</div>" +
                "</div>";
    }

    private String generateDocumentationTab(String apiKey) {
        return "<div class=\"sub-tab-navigation\">" +
                "<button class=\"sub-tab-button active\" onclick=\"showSubTab('calendar')\">📅 Calendar API</button>" +
                "<button class=\"sub-tab-button\" onclick=\"showSubTab('user')\">👤 User Management</button>" +
                "<button class=\"sub-tab-button\" onclick=\"showSubTab('time')\">⏰ Time Services</button>" +
                "</div>" +
                "<div id=\"calendar-subtab\" class=\"sub-tab-content active\">" +
                generateCalendarApiDocs(apiKey) +
                "</div>" +
                "<div id=\"user-subtab\" class=\"sub-tab-content\">" +
                generateUserApiDocs(apiKey) +
                "</div>" +
                "<div id=\"time-subtab\" class=\"sub-tab-content\">" +
                generateTimeApiDocs(apiKey) +
                "</div>";
    }

    private String generateCalendarApiDocs(String apiKey) {
        return "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/v1/calendar/date</h4>" +
                "<div class=\"description\">Get current date information with timezone support</div>" +
                "<div class=\"parameters\">" +
                "<h5>Query Parameters</h5>" +
                "<div class=\"parameter\"><span class=\"parameter-name\">timezone</span> <span class=\"parameter-type\">(string)</span> <span class=\"parameter-optional\">optional</span> - Timezone identifier (e.g., 'America/New_York')</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">format</span> <span class=\"parameter-type\">(string)</span> <span class=\"parameter-optional\">optional</span> - Response format ('json' or 'xml')</div>"
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Response Example</h5>" +
                "<div class=\"response-example\">{\n  \"date\": \"2025-10-13\",\n  \"time\": \"19:32:10\",\n  \"timezone\": \"UTC\",\n  \"timestamp\": 1728847930000\n}</div>"
                +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/v1/calendar/date')\">Test This API</button>" +
                "</div>" +
                "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/v2/calendar/solar</h4>" +
                "<div class=\"description\">Get solar information including sunrise, sunset, and solar noon</div>" +
                "<div class=\"parameters\">" +
                "<h5>Query Parameters</h5>" +
                "<div class=\"parameter\"><span class=\"parameter-name\">latitude</span> <span class=\"parameter-type\">(number)</span> <span class=\"parameter-required\">required</span> - Latitude coordinate</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">longitude</span> <span class=\"parameter-type\">(number)</span> <span class=\"parameter-required\">required</span> - Longitude coordinate</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">date</span> <span class=\"parameter-type\">(string)</span> <span class=\"parameter-optional\">optional</span> - Date in YYYY-MM-DD format (defaults to today)</div>"
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Response Example</h5>" +
                "<div class=\"response-example\">{\n  \"sunrise\": \"06:45:23\",\n  \"sunset\": \"18:30:45\",\n  \"solar_noon\": \"12:38:04\",\n  \"day_length\": \"11:45:22\"\n}</div>"
                +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/v2/calendar/solar?latitude=40.7128&longitude=-74.0060')\">Test This API</button>"
                +
                "</div>" +
                "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/v2/calendar/moon</h4>" +
                "<div class=\"description\">Get lunar information including moon phase and illumination</div>" +
                "<div class=\"parameters\">" +
                "<h5>Query Parameters</h5>" +
                "<div class=\"parameter\"><span class=\"parameter-name\">date</span> <span class=\"parameter-type\">(string)</span> <span class=\"parameter-optional\">optional</span> - Date in YYYY-MM-DD format (defaults to today)</div>"
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Response Example</h5>" +
                "<div class=\"response-example\">{\n  \"phase\": \"Waxing Crescent\",\n  \"illumination\": 23.4,\n  \"age\": 4.2,\n  \"next_full_moon\": \"2025-10-20\"\n}</div>"
                +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/v2/calendar/moon')\">Test This API</button>" +
                "</div>";
    }

    private String generateUserApiDocs(String apiKey) {
        return "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/user/me</h4>" +
                "<div class=\"description\">Get current user profile information</div>" +
                "<div class=\"response\">" +
                "<h5>Response Example</h5>" +
                "<div class=\"response-example\">{\n  \"identity\": \"github:username\",\n  \"api_key\": \"***\",\n  \"created_at\": \"2025-10-13T19:32:10Z\",\n  \"last_used\": \"2025-10-13T19:32:10Z\"\n}</div>"
                +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/user/me')\">Test This API</button>" +
                "</div>" +
                "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-post\">POST</span> /api/user/key/rotate</h4>" +
                "<div class=\"description\">Rotate the current API key to generate a new one</div>" +
                "<div class=\"response\">" +
                "<h5>Response Example</h5>" +
                "<div class=\"response-example\">{\n  \"message\": \"API key rotated successfully\",\n  \"new_api_key\": \"***\",\n  \"old_api_key\": \"***\"\n}</div>"
                +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/user/key/rotate', 'POST')\">Test This API</button>"
                +
                "</div>";
    }

    private String generateTimeApiDocs(String apiKey) {
        return "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/time/now</h4>" +
                "<div class=\"description\">Get current time information in various formats</div>" +
                "<div class=\"parameters\">" +
                "<h5>Query Parameters</h5>" +
                "<div class=\"parameter\"><span class=\"parameter-name\">format</span> <span class=\"parameter-type\">(string)</span> <span class=\"parameter-optional\">optional</span> - Time format ('iso', 'unix', 'rfc2822')</div>"
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Response Example</h5>" +
                "<div class=\"response-example\">{\n  \"iso\": \"2025-10-13T19:32:10Z\",\n  \"unix\": 1728847930,\n  \"rfc2822\": \"Mon, 13 Oct 2025 19:32:10 GMT\"\n}</div>"
                +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/time/now')\">Test This API</button>" +
                "</div>";
    }

    private String generateVersionInfo(String shortCommitHash, String buildTime) {
        return "<div class=\"version-info\">" +
                "<div>Deployed: " + shortCommitHash + " • Built: " + buildTime + "</div>" +
                "</div>";
    }

    private String generateJavaScript(String apiKey) {
        return "<script>" +
                "let autoRefreshInterval = null;" +
                "let currentTab = 'account';" +
                "let currentSubTab = 'calendar';" +
                "" +
                "function showTab(tabName) {" +
                "document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));" +
                "document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));" +
                "document.getElementById(tabName + '-tab').classList.add('active');" +
                "document.querySelector(`[onclick=\"showTab('${tabName}')\"]`).classList.add('active');" +
                "currentTab = tabName;" +
                "if (tabName === 'services') { refreshHealthData(); }" +
                "}" +
                "" +
                "function showSubTab(subTabName) {" +
                "document.querySelectorAll('.sub-tab-content').forEach(tab => tab.classList.remove('active'));" +
                "document.querySelectorAll('.sub-tab-button').forEach(btn => btn.classList.remove('active'));" +
                "document.getElementById(subTabName + '-subtab').classList.add('active');" +
                "document.querySelector(`[onclick=\"showSubTab('${subTabName}')\"]`).classList.add('active');" +
                "currentSubTab = subTabName;" +
                "}" +
                "" +
                "function copyApiKey() {" +
                "const apiKey = document.getElementById('api-key').textContent;" +
                "navigator.clipboard.writeText(apiKey).then(function() {" +
                "const button = document.querySelector('.copy-button');" +
                "const originalText = button.textContent;" +
                "button.textContent = 'Copied!';" +
                "button.style.background = '#28a745';" +
                "setTimeout(function() { button.textContent = originalText; button.style.background = '#4a5568'; }, 2000);"
                +
                "}).catch(function(err) { console.error('Could not copy text: ', err); alert('Could not copy to clipboard. Please copy manually.'); });"
                +
                "}" +
                "" +
                "async function testApi(endpoint, method = 'GET') {" +
                "const resultDiv = document.getElementById('test-result') || createTestResultDiv();" +
                "const apiKey = document.getElementById('api-key').textContent;" +
                "resultDiv.style.display = 'block';" +
                "resultDiv.textContent = 'Testing...';" +
                "try {" +
                "const response = await fetch(endpoint, { method: method, headers: { 'Authorization': 'Bearer ' + apiKey, 'Content-Type': 'application/json' } });"
                +
                "const data = await response.text();" +
                "resultDiv.textContent = 'Status: ' + response.status + '\\n\\n' + data;" +
                "} catch (error) { resultDiv.textContent = 'Error: ' + error.message; }" +
                "}" +
                "" +
                "function createTestResultDiv() {" +
                "const div = document.createElement('div');" +
                "div.id = 'test-result';" +
                "div.className = 'test-result';" +
                "div.style.display = 'none';" +
                "document.querySelector('.tab-content.active').appendChild(div);" +
                "return div;" +
                "}" +
                "" +
                "async function refreshHealthData() {" +
                "const healthDiv = document.getElementById('health-data');" +
                "if (!healthDiv) return;" +
                "healthDiv.innerHTML = '<div class=\"loading\">Loading health data...</div>';" +
                "try {" +
                "const response = await fetch('/api/health');" +
                "const data = await response.json();" +
                "healthDiv.innerHTML = generateHealthTable(data);" +
                "} catch (error) {" +
                "healthDiv.innerHTML = '<div class=\"error\">Error loading health data: ' + error.message + '</div>';" +
                "}" +
                "}" +
                "" +
                "function generateHealthTable(healthData) {" +
                "return '<table class=\\\"health-table\\\">' +" +
                "'<thead><tr><th>Service</th><th>Status</th><th>Response Time</th><th>Last Check</th></tr></thead>' +" +
                "'<tbody>' +" +
                "'<tr><td>API Server</td><td><span class=\\\"status-badge status-healthy\\\">Healthy</span></td><td>' + (healthData.responseTime || 'N/A') + 'ms</td><td>' + new Date().toLocaleTimeString() + '</td></tr>' +"
                +
                "'<tr><td>Database</td><td><span class=\\\"status-badge status-healthy\\\">Healthy</span></td><td>N/A</td><td>' + new Date().toLocaleTimeString() + '</td></tr>' +"
                +
                "'<tr><td>External APIs</td><td><span class=\\\"status-badge status-healthy\\\">Healthy</span></td><td>N/A</td><td>' + new Date().toLocaleTimeString() + '</td></tr>' +"
                +
                "'</tbody></table>';" +
                "}" +
                "" +
                "function toggleAutoRefresh() {" +
                "const checkbox = document.getElementById('auto-refresh');" +
                "if (checkbox.checked) {" +
                "autoRefreshInterval = setInterval(refreshHealthData, 30000);" +
                "} else {" +
                "if (autoRefreshInterval) clearInterval(autoRefreshInterval);" +
                "}" +
                "}" +
                "" +
                "async function rotateApiKey() {" +
                "if (!confirm('Are you sure you want to rotate your API key? This will invalidate your current key.')) return;"
                +
                "try {" +
                "const response = await fetch('/api/user/key/rotate', { method: 'POST', headers: { 'Authorization': 'Bearer ' + document.getElementById('api-key').textContent } });"
                +
                "const data = await response.json();" +
                "if (response.ok) {" +
                "document.getElementById('api-key').textContent = data.new_api_key;" +
                "alert('API key rotated successfully!');" +
                "} else {" +
                "alert('Error rotating API key: ' + data.message);" +
                "}" +
                "} catch (error) {" +
                "alert('Error rotating API key: ' + error.message);" +
                "}" +
                "}" +
                "" +
                "// Initialize page" +
                "document.addEventListener('DOMContentLoaded', function() {" +
                "refreshHealthData();" +
                "});" +
                "</script>";
    }
}
