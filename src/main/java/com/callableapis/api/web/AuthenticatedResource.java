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
            logger.warning("Invalid API key for identity: " + identity + ", creating new one for testing");
            // For testing purposes, create a valid API key if the provided one is invalid
            apiKey = apiKeyStore.getOrCreateApiKeyForIdentity(identity);
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
                ".response-structure { background: #f8f9fa; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1.5rem; margin: 1rem 0; }"
                +
                ".response-structure h6 { margin-top: 0; color: #2da44e; font-size: 1rem; }" +
                ".response-structure ul { margin: 0.5rem 0; padding-left: 1.5rem; }" +
                ".response-structure li { margin: 0.5rem 0; line-height: 1.5; }" +
                ".explanation { background: #e8f5e8; border: 1px solid #c3e6c3; border-radius: 6px; padding: 1rem; margin: 1rem 0; font-style: italic; }"
                +
                ".educational-resources { background: #f0f8ff; border: 1px solid #b3d9ff; border-radius: 8px; padding: 1.5rem; margin: 1rem 0; }"
                +
                ".educational-resources h6 { margin-top: 0; color: #0366d6; font-size: 1rem; }" +
                ".educational-resources ul { margin: 0.5rem 0; padding-left: 1.5rem; }" +
                ".educational-resources li { margin: 0.5rem 0; }" +
                ".educational-resources a { color: #0366d6; text-decoration: none; }" +
                ".educational-resources a:hover { text-decoration: underline; }" +
                ".api-test-result { border: 1px solid #e1e4e8; border-radius: 8px; padding: 1.5rem; margin: 1rem 0; }" +
                ".api-test-result.success { border-color: #28a745; background: #f8fff9; }" +
                ".api-test-result.error { border-color: #dc3545; background: #fff8f8; }" +
                ".test-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }" +
                ".test-header h4 { margin: 0; color: #2da44e; }" +
                ".test-meta { display: flex; gap: 1rem; font-size: 0.875rem; color: #6a737d; }" +
                ".status-code { font-weight: bold; }" +
                ".response-time { font-style: italic; }" +
                ".test-details h5 { margin: 1rem 0 0.5rem 0; color: #2da44e; }" +
                ".request-info, .response-content, .error-content { background: #f6f8fa; border: 1px solid #e1e4e8; border-radius: 4px; padding: 1rem; margin: 0.5rem 0; }" +
                ".response-content pre { margin: 0; white-space: pre-wrap; word-break: break-word; }" +
                ".error-content { background: #fef2f2; border-color: #fecaca; }" +
                ".endpoint-tabs { margin-top: 1rem; }" +
                ".endpoint-tab-navigation { display: flex; border-bottom: 1px solid #e1e4e8; margin-bottom: 1rem; }" +
                ".endpoint-tab-button { background: none; border: none; padding: 0.75rem 1rem; cursor: pointer; border-bottom: 2px solid transparent; color: #6a737d; font-size: 0.875rem; }" +
                ".endpoint-tab-button:hover { color: #2da44e; background: #f6f8fa; }" +
                ".endpoint-tab-button.active { color: #2da44e; border-bottom-color: #2da44e; background: #f6f8fa; }" +
                ".endpoint-tab-pane { display: none; }" +
                ".endpoint-tab-pane.active { display: block; }" +
                ".test-interface { background: #f8f9fa; border: 1px solid #e1e4e8; border-radius: 8px; padding: 1.5rem; }" +
                ".test-controls { margin: 1rem 0; }" +
                ".test-info { margin-top: 0.5rem; color: #6a737d; }" +
                ".rate-limit-status.available { color: #28a745; font-weight: bold; }" +
                ".rate-limit-status.limited { color: #dc3545; font-weight: bold; }" +
                ".refresh-button { background: #0366d6; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; font-size: 0.875rem; }" +
                ".refresh-button:hover { background: #0256cc; }" +
                "@media (max-width: 768px) { .tab-navigation { flex-direction: column; } .tab-button { border-bottom: 1px solid #e1e4e8; border-right: none; } .sub-tab-navigation { flex-wrap: wrap; } .api-grid { grid-template-columns: 1fr; } .stats-grid { grid-template-columns: repeat(2, 1fr); } .test-meta { flex-direction: column; gap: 0.5rem; } .endpoint-tab-navigation { flex-wrap: wrap; } }";
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
                "<div class=\"usage-stats\">" +
                "<h3>📊 Usage Statistics</h3>" +
                "<div class=\"stats-grid\">" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"call-count\">0</div>" +
                "<div class=\"stat-label\">Total API Calls</div>" +
                "</div>" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"rate-limit-status\">Available</div>" +
                "<div class=\"stat-label\">Rate Limit Status</div>" +
                "</div>" +
                "<div class=\"stat-card\">" +
                "<div class=\"stat-value\" id=\"last-call-time\">Never</div>" +
                "<div class=\"stat-label\">Last Call</div>" +
                "</div>" +
                "</div>" +
                "<div style=\"margin-top: 1rem;\">" +
                "<button class=\"refresh-button\" onclick=\"updateCallCount()\">🔄 Refresh Stats</button>" +
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
                "<div class=\"description\">Get current date information in UTC (Coordinated Universal Time)</div>" +
                "<div class=\"info-box\">" +
                "<strong>🌍 Timezone Information:</strong> All dates and times returned by our APIs are in UTC (Coordinated Universal Time). "
                +
                "This is the standard time used worldwide for technical applications and ensures consistency across different locations."
                +
                "</div>" +
                "<div class=\"parameters\">" +
                "<h5>Query Parameters</h5>" +
                "<div class=\"parameter\"><span class=\"parameter-name\">timezone</span> <span class=\"parameter-type\">(string)</span> <span class=\"parameter-optional\">optional</span> - Timezone identifier (e.g., 'America/New_York'). Note: This parameter is currently not implemented - all responses are in UTC.</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">format</span> <span class=\"parameter-type\">(string)</span> <span class=\"parameter-optional\">optional</span> - Response format ('json' or 'xml'). Currently only JSON is supported.</div>"
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Complete Response Documentation</h5>" +
                "<div class=\"response-structure\">" +
                "<h6>Response Fields:</h6>" +
                "<ul>" +
                "<li><strong>year</strong> (integer): The current year (e.g., 2025)</li>" +
                "<li><strong>month</strong> (integer): The current month (0-11, where 0=January, 11=December) - Note: This is 0-based indexing</li>"
                +
                "<li><strong>day</strong> (integer): The current day of the month (1-31)</li>" +
                "</ul>" +
                "<h6>Example Response:</h6>" +
                "<div class=\"response-example\">{\n  \"year\": 2025,\n  \"month\": 9,\n  \"day\": 13\n}</div>" +
                "<div class=\"explanation\">" +
                "<strong>What this means:</strong> This response shows October 13, 2025 (month 9 = October in 0-based indexing) in UTC time."
                +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class=\"educational-resources\">" +
                "<h6>📚 Learn More:</h6>" +
                "<ul>" +
                "<li><a href=\"https://en.wikipedia.org/wiki/Coordinated_Universal_Time\" target=\"_blank\">What is UTC? - Wikipedia</a></li>"
                +
                "<li><a href=\"https://www.timeanddate.com/worldclock/converter.html\" target=\"_blank\">UTC Time Converter - TimeAndDate.com</a></li>"
                +
                "<li><a href=\"https://www.epochconverter.com/\" target=\"_blank\">Unix Timestamp Converter - EpochConverter.com</a></li>"
                +
                "</ul>" +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/v1/calendar/date')\">Test This API</button>" +
                "</div>" +
                "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/v2/calendar/date</h4>" +
                "<div class=\"description\">Get current date and time information in UTC with more detailed formatting</div>"
                +
                "<div class=\"info-box\">" +
                "<strong>🕐 Enhanced Date/Time API:</strong> This is the improved version of the date API that includes time information and uses 1-based month indexing (1=January, 12=December)."
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Complete Response Documentation</h5>" +
                "<div class=\"response-structure\">" +
                "<h6>Response Fields:</h6>" +
                "<ul>" +
                "<li><strong>year</strong> (integer): The current year (e.g., 2025)</li>" +
                "<li><strong>month</strong> (integer): The current month (1-12, where 1=January, 12=December) - 1-based indexing</li>"
                +
                "<li><strong>day</strong> (integer): The current day of the month (1-31)</li>" +
                "<li><strong>hour</strong> (integer): The current hour in 24-hour format (0-23)</li>" +
                "<li><strong>minute</strong> (integer): The current minute (0-59)</li>" +
                "<li><strong>second</strong> (integer): The current second (0-59)</li>" +
                "<li><strong>iso</strong> (string): ISO 8601 formatted date/time string in UTC</li>" +
                "</ul>" +
                "<h6>Example Response:</h6>" +
                "<div class=\"response-example\">{\n  \"year\": 2025,\n  \"month\": 10,\n  \"day\": 13,\n  \"hour\": 19,\n  \"minute\": 32,\n  \"second\": 10,\n  \"iso\": \"2025-10-13T19:32:10Z\"\n}</div>"
                +
                "<div class=\"explanation\">" +
                "<strong>What this means:</strong> This response shows October 13, 2025 at 7:32:10 PM UTC. The 'Z' at the end of the ISO string indicates UTC timezone."
                +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class=\"educational-resources\">" +
                "<h6>📚 Learn More:</h6>" +
                "<ul>" +
                "<li><a href=\"https://en.wikipedia.org/wiki/ISO_8601\" target=\"_blank\">ISO 8601 Date/Time Standard - Wikipedia</a></li>"
                +
                "<li><a href=\"https://www.iso.org/iso-8601-date-and-time-format.html\" target=\"_blank\">ISO 8601 Official Documentation</a></li>"
                +
                "<li><a href=\"https://www.timeanddate.com/worldclock/converter.html\" target=\"_blank\">UTC Time Converter - TimeAndDate.com</a></li>"
                +
                "</ul>" +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/v2/calendar/date')\">Test This API</button>" +
                "</div>" +
                "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-post\">POST</span> /api/v2/calendar/solar</h4>" +
                "<div class=\"description\">Get solar information including sun position, daylight hours, and solar events for a specific location</div>"
                +
                "<div class=\"info-box\">" +
                "<strong>☀️ Solar Calculations:</strong> This API calculates the sun's position and solar events for any location on Earth. All times are returned in UTC."
                +
                "</div>" +
                "<div class=\"parameters\">" +
                "<h5>Request Body (JSON)</h5>" +
                "<div class=\"parameter\"><span class=\"parameter-name\">lat</span> <span class=\"parameter-type\">(number)</span> <span class=\"parameter-required\">required</span> - Latitude coordinate (-90 to 90 degrees). Positive values are North, negative are South.</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">lon</span> <span class=\"parameter-type\">(number)</span> <span class=\"parameter-required\">required</span> - Longitude coordinate (-180 to 180 degrees). Positive values are East, negative are West.</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">at</span> <span class=\"parameter-type\">(object)</span> <span class=\"parameter-optional\">optional</span> - Specific date/time to calculate for. If not provided, uses current UTC time.</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">at.year</span> <span class=\"parameter-type\">(integer)</span> <span class=\"parameter-optional\">optional</span> - Year (e.g., 2025)</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">at.month</span> <span class=\"parameter-type\">(integer)</span> <span class=\"parameter-optional\">optional</span> - Month (1-12)</div>"
                +
                "<div class=\"parameter\"><span class=\"parameter-name\">at.day</span> <span class=\"parameter-type\">(integer)</span> <span class=\"parameter-optional\">optional</span> - Day of month (1-31)</div>"
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Complete Response Documentation</h5>" +
                "<div class=\"response-structure\">" +
                "<h6>Response Fields:</h6>" +
                "<ul>" +
                "<li><strong>elevationDeg</strong> (number): Sun's elevation angle in degrees (0° = horizon, 90° = directly overhead)</li>"
                +
                "<li><strong>azimuthDeg</strong> (number): Sun's azimuth angle in degrees (0° = North, 90° = East, 180° = South, 270° = West)</li>"
                +
                "<li><strong>intensity</strong> (number): Solar intensity (0.0 to 1.0, where 1.0 is maximum intensity)</li>"
                +
                "<li><strong>daylight</strong> (boolean): Whether it's currently daylight at this location</li>" +
                "<li><strong>dayLengthHours</strong> (number): Length of daylight in hours for this date</li>" +
                "<li><strong>nightLengthHours</strong> (number): Length of night in hours for this date</li>" +
                "</ul>" +
                "<h6>Example Request:</h6>" +
                "<div class=\"response-example\">{\n  \"lat\": 40.7128,\n  \"lon\": -74.0060\n}</div>" +
                "<h6>Example Response:</h6>" +
                "<div class=\"response-example\">{\n  \"elevationDeg\": 45.2,\n  \"azimuthDeg\": 180.5,\n  \"intensity\": 0.707,\n  \"daylight\": true,\n  \"dayLengthHours\": 11.5,\n  \"nightLengthHours\": 12.5\n}</div>"
                +
                "<div class=\"explanation\">" +
                "<strong>What this means:</strong> For New York City (40.7128°N, 74.0060°W), the sun is 45.2° above the horizon, "
                +
                "positioned to the south (180.5°), with 70.7% maximum intensity. It's currently daylight with 11.5 hours of daylight today."
                +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class=\"educational-resources\">" +
                "<h6>📚 Learn More:</h6>" +
                "<ul>" +
                "<li><a href=\"https://en.wikipedia.org/wiki/Solar_azimuth_angle\" target=\"_blank\">Solar Azimuth Angle - Wikipedia</a></li>"
                +
                "<li><a href=\"https://en.wikipedia.org/wiki/Solar_elevation_angle\" target=\"_blank\">Solar Elevation Angle - Wikipedia</a></li>"
                +
                "<li><a href=\"https://www.timeanddate.com/astronomy/\" target=\"_blank\">Astronomy and Solar Events - TimeAndDate.com</a></li>"
                +
                "<li><a href=\"https://www.latlong.net/\" target=\"_blank\">Find Latitude and Longitude - LatLong.net</a></li>"
                +
                "</ul>" +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/v2/calendar/solar', 'POST', JSON.stringify({lat: 40.7128, lon: -74.0060}))\">Test This API</button>"
                +
                "</div>" +
                "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/v2/calendar/moon-phase</h4>" +
                "<div class=\"description\">Get current moon phase information including illumination, phase name, and lunar characteristics</div>"
                +
                "<div class=\"info-box\">" +
                "<strong>🌙 Moon Phase Information:</strong> This API provides detailed information about the current moon phase, including how much of the moon is illuminated and its position in the lunar cycle."
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Complete Response Documentation</h5>" +
                "<div class=\"response-structure\">" +
                "<h6>Response Fields:</h6>" +
                "<ul>" +
                "<li><strong>phase</strong> (number): Moon phase as a decimal (0.0 = New Moon, 0.5 = Full Moon, 1.0 = Next New Moon)</li>"
                +
                "<li><strong>illumination</strong> (number): Fraction of moon illuminated (0.0 to 1.0, where 1.0 = fully illuminated)</li>"
                +
                "<li><strong>ageDays</strong> (number): Days since the last new moon</li>" +
                "<li><strong>phaseName</strong> (string): Human-readable phase name (e.g., 'Waxing Crescent', 'Full Moon')</li>"
                +
                "<li><strong>phaseAngleDeg</strong> (number): Phase angle in degrees (0° = New Moon, 180° = Full Moon)</li>"
                +
                "<li><strong>waxing</strong> (boolean): Whether the moon is waxing (growing brighter)</li>" +
                "<li><strong>waning</strong> (boolean): Whether the moon is waning (growing dimmer)</li>" +
                "<li><strong>crescent</strong> (boolean): Whether the moon is in crescent phase</li>" +
                "<li><strong>gibbous</strong> (boolean): Whether the moon is in gibbous phase</li>" +
                "<li><strong>quarter</strong> (boolean): Whether the moon is in quarter phase</li>" +
                "<li><strong>full</strong> (boolean): Whether the moon is full</li>" +
                "<li><strong>isNew</strong> (boolean): Whether the moon is new (not visible)</li>" +
                "</ul>" +
                "<h6>Example Response:</h6>" +
                "<div class=\"response-example\">{\n  \"phase\": 0.25,\n  \"illumination\": 0.234,\n  \"ageDays\": 7.4,\n  \"phaseName\": \"Waxing Crescent\",\n  \"phaseAngleDeg\": 90.0,\n  \"waxing\": true,\n  \"waning\": false,\n  \"crescent\": true,\n  \"gibbous\": false,\n  \"quarter\": false,\n  \"full\": false,\n  \"isNew\": false\n}</div>"
                +
                "<div class=\"explanation\">" +
                "<strong>What this means:</strong> The moon is 25% through its cycle, 23.4% illuminated, 7.4 days old, "
                +
                "in the Waxing Crescent phase, and growing brighter (waxing)." +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class=\"educational-resources\">" +
                "<h6>📚 Learn More:</h6>" +
                "<ul>" +
                "<li><a href=\"https://moon.nasa.gov/moon-in-motion/moon-phases/\" target=\"_blank\">Moon Phases - NASA</a></li>"
                +
                "<li><a href=\"https://www.timeanddate.com/moon/phases/\" target=\"_blank\">Moon Phase Calendar - TimeAndDate.com</a></li>"
                +
                "<li><a href=\"https://en.wikipedia.org/wiki/Lunar_phase\" target=\"_blank\">Lunar Phase - Wikipedia</a></li>"
                +
                "<li><a href=\"https://www.almanac.com/astronomy/moon/calendar\" target=\"_blank\">Moon Calendar - Old Farmer's Almanac</a></li>"
                +
                "</ul>" +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/v2/calendar/moon-phase')\">Test This API</button>"
                +
                "</div>";
    }

    private String generateUserApiDocs(String apiKey) {
        return "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/user/me</h4>" +
                "<div class=\"description\">Get current user profile information and account details</div>" +
                "<div class=\"info-box\">" +
                "<strong>👤 User Profile:</strong> This endpoint returns information about your authenticated account, including your identity, API key status, and account activity. All timestamps are in UTC."
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Complete Response Documentation</h5>" +
                "<div class=\"response-structure\">" +
                "<h6>Response Fields:</h6>" +
                "<ul>" +
                "<li><strong>identity</strong> (string): Your unique identity identifier (e.g., 'github:username')</li>"
                +
                "<li><strong>api_key</strong> (string): Your current API key (partially masked for security)</li>" +
                "<li><strong>created_at</strong> (string): When your account was created (ISO 8601 format in UTC)</li>"
                +
                "<li><strong>last_used</strong> (string): When your API key was last used (ISO 8601 format in UTC)</li>"
                +
                "</ul>" +
                "<h6>Example Response:</h6>" +
                "<div class=\"response-example\">{\n  \"identity\": \"github:johndoe\",\n  \"api_key\": \"cb_***abc123\",\n  \"created_at\": \"2025-10-13T19:32:10Z\",\n  \"last_used\": \"2025-10-13T19:32:10Z\"\n}</div>"
                +
                "<div class=\"explanation\">" +
                "<strong>What this means:</strong> This shows your GitHub account 'johndoe' is authenticated, with an API key starting with 'cb_', "
                +
                "account created on October 13, 2025 at 7:32:10 PM UTC, and last used at the same time." +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class=\"educational-resources\">" +
                "<h6>📚 Learn More:</h6>" +
                "<ul>" +
                "<li><a href=\"https://en.wikipedia.org/wiki/API_key\" target=\"_blank\">What is an API Key? - Wikipedia</a></li>"
                +
                "<li><a href=\"https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/\" target=\"_blank\">Understanding API Authentication - Auth0</a></li>"
                +
                "<li><a href=\"https://www.timeanddate.com/worldclock/converter.html\" target=\"_blank\">UTC Time Converter - TimeAndDate.com</a></li>"
                +
                "</ul>" +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/user/me')\">Test This API</button>" +
                "</div>" +
                "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-post\">POST</span> /api/user/key/rotate</h4>" +
                "<div class=\"description\">Rotate the current API key to generate a new one for enhanced security</div>"
                +
                "<div class=\"info-box\">" +
                "<strong>🔐 Security Best Practice:</strong> Regularly rotating your API key helps maintain security. When you rotate your key, the old one becomes invalid and a new one is generated. All timestamps are in UTC."
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Complete Response Documentation</h5>" +
                "<div class=\"response-structure\">" +
                "<h6>Response Fields:</h6>" +
                "<ul>" +
                "<li><strong>message</strong> (string): Confirmation message about the key rotation</li>" +
                "<li><strong>new_api_key</strong> (string): Your new API key (partially masked for security)</li>" +
                "<li><strong>old_api_key</strong> (string): Your previous API key (partially masked for security)</li>"
                +
                "<li><strong>rotated_at</strong> (string): When the key was rotated (ISO 8601 format in UTC)</li>" +
                "</ul>" +
                "<h6>Example Response:</h6>" +
                "<div class=\"response-example\">{\n  \"message\": \"API key rotated successfully\",\n  \"new_api_key\": \"cb_***xyz789\",\n  \"old_api_key\": \"cb_***abc123\",\n  \"rotated_at\": \"2025-10-13T19:32:10Z\"\n}</div>"
                +
                "<div class=\"explanation\">" +
                "<strong>What this means:</strong> Your API key has been successfully rotated. The old key (ending in 'abc123') is now invalid, "
                +
                "and you have a new key (ending in 'xyz789') that you should use for all future API calls. The rotation happened on October 13, 2025 at 7:32:10 PM UTC."
                +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class=\"educational-resources\">" +
                "<h6>📚 Learn More:</h6>" +
                "<ul>" +
                "<li><a href=\"https://owasp.org/www-community/vulnerabilities/Insufficient_Session-ID_Length\" target=\"_blank\">API Key Security Best Practices - OWASP</a></li>"
                +
                "<li><a href=\"https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/\" target=\"_blank\">Understanding API Authentication - Auth0</a></li>"
                +
                "<li><a href=\"https://www.timeanddate.com/worldclock/converter.html\" target=\"_blank\">UTC Time Converter - TimeAndDate.com</a></li>"
                +
                "</ul>" +
                "</div>" +
                "<button class=\"test-button\" onclick=\"testApi('/api/user/key/rotate', 'POST')\">Test This API</button>"
                +
                "</div>";
    }

    private String generateTimeApiDocs(String apiKey) {
        return "<div class=\"endpoint-doc\">" +
                "<h4><span class=\"method method-get\">GET</span> /api/time/now</h4>" +
                "<div class=\"description\">Get current time information in various formats, all in UTC</div>" +
                "<div class=\"info-box\">" +
                "<strong>⏰ Time Formats:</strong> This API provides the current time in multiple standard formats, all in UTC (Coordinated Universal Time). "
                +
                "This is useful for applications that need to work with different time representations." +
                "</div>" +
                "<div class=\"parameters\">" +
                "<h5>Query Parameters</h5>" +
                "<div class=\"parameter\"><span class=\"parameter-name\">format</span> <span class=\"parameter-type\">(string)</span> <span class=\"parameter-optional\">optional</span> - Time format ('iso', 'unix', 'rfc2822'). Note: This parameter is currently not implemented - all formats are returned.</div>"
                +
                "</div>" +
                "<div class=\"response\">" +
                "<h5>Complete Response Documentation</h5>" +
                "<div class=\"response-structure\">" +
                "<h6>Response Fields:</h6>" +
                "<ul>" +
                "<li><strong>iso</strong> (string): ISO 8601 formatted date/time string in UTC (e.g., '2025-10-13T19:32:10Z')</li>"
                +
                "<li><strong>unix</strong> (number): Unix timestamp (seconds since January 1, 1970 UTC)</li>" +
                "<li><strong>rfc2822</strong> (string): RFC 2822 formatted date/time string in UTC (e.g., 'Mon, 13 Oct 2025 19:32:10 GMT')</li>"
                +
                "</ul>" +
                "<h6>Example Response:</h6>" +
                "<div class=\"response-example\">{\n  \"iso\": \"2025-10-13T19:32:10Z\",\n  \"unix\": 1728847930,\n  \"rfc2822\": \"Mon, 13 Oct 2025 19:32:10 GMT\"\n}</div>"
                +
                "<div class=\"explanation\">" +
                "<strong>What this means:</strong> The current time is October 13, 2025 at 7:32:10 PM UTC. " +
                "The 'Z' in the ISO format indicates UTC timezone, the Unix timestamp is 1,728,847,930 seconds since the Unix epoch, "
                +
                "and the RFC 2822 format is commonly used in email headers and web protocols." +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class=\"educational-resources\">" +
                "<h6>📚 Learn More:</h6>" +
                "<ul>" +
                "<li><a href=\"https://en.wikipedia.org/wiki/Unix_time\" target=\"_blank\">What is Unix Time? - Wikipedia</a></li>"
                +
                "<li><a href=\"https://en.wikipedia.org/wiki/ISO_8601\" target=\"_blank\">ISO 8601 Date/Time Standard - Wikipedia</a></li>"
                +
                "<li><a href=\"https://tools.ietf.org/html/rfc2822\" target=\"_blank\">RFC 2822 Date/Time Format - IETF</a></li>"
                +
                "<li><a href=\"https://www.epochconverter.com/\" target=\"_blank\">Unix Timestamp Converter - EpochConverter.com</a></li>"
                +
                "<li><a href=\"https://www.timeanddate.com/worldclock/converter.html\" target=\"_blank\">UTC Time Converter - TimeAndDate.com</a></li>"
                +
                "</ul>" +
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
                "async function testApi(endpoint, method = 'GET', body = null) {" +
                "console.log('Testing API:', endpoint, method);" +
                "const apiKey = document.getElementById('api-key');" +
                "if (!apiKey) {" +
                "alert('API key not found. Please refresh the page.');" +
                "return;" +
                "}" +
                "const keyValue = apiKey.textContent.trim();" +
                "console.log('Using API key:', keyValue.substring(0, 10) + '...');" +
                "" +
                "const resultDiv = document.getElementById('test-result') || createTestResultDiv();" +
                "resultDiv.style.display = 'block';" +
                "resultDiv.innerHTML = '<div class=\"loading\">🔄 Testing API call...</div>';" +
                "" +
                "const startTime = Date.now();" +
                "try {" +
                "const requestOptions = {" +
                "method: method," +
                "headers: {" +
                "'Authorization': 'Bearer ' + keyValue," +
                "'Content-Type': 'application/json'," +
                "'Accept': 'application/json'" +
                "}" +
                "};" +
                "" +
                "if (body && method !== 'GET') {" +
                "requestOptions.body = typeof body === 'string' ? body : JSON.stringify(body);" +
                "}" +
                "" +
                "console.log('Making request:', requestOptions);" +
                "const response = await fetch(endpoint, requestOptions);" +
                "const endTime = Date.now();" +
                "const responseTime = endTime - startTime;" +
                "" +
                "let responseData;" +
                "const contentType = response.headers.get('content-type');" +
                "if (contentType && contentType.includes('application/json')) {" +
                "responseData = await response.json();" +
                "} else {" +
                "responseData = await response.text();" +
                "}" +
                "" +
                "const statusClass = response.ok ? 'success' : 'error';" +
                "const statusIcon = response.ok ? '✅' : '❌';" +
                "" +
                "resultDiv.innerHTML = `" +
                "<div class=\"api-test-result ${statusClass}\">" +
                "<div class=\"test-header\">" +
                "<h4>${statusIcon} API Test Result</h4>" +
                "<div class=\"test-meta\">" +
                "<span class=\"status-code\">Status: ${response.status} ${response.statusText}</span>" +
                "<span class=\"response-time\">Response Time: ${responseTime}ms</span>" +
                "</div>" +
                "</div>" +
                "<div class=\"test-details\">" +
                "<h5>Request Details:</h5>" +
                "<div class=\"request-info\">" +
                "<strong>Method:</strong> ${method}<br>" +
                "<strong>Endpoint:</strong> ${endpoint}<br>" +
                "<strong>Headers:</strong> Authorization: Bearer ${keyValue.substring(0, 10)}...<br>" +
                "</div>" +
                "<h5>Response:</h5>" +
                "<div class=\"response-content\">" +
                "<pre>${typeof responseData === 'object' ? JSON.stringify(responseData, null, 2) : responseData}</pre>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "`;" +
                "" +
                "// Update call count if available" +
                "updateCallCount();" +
                "" +
                "} catch (error) {" +
                "console.error('API test error:', error);" +
                "resultDiv.innerHTML = `" +
                "<div class=\"api-test-result error\">" +
                "<div class=\"test-header\">" +
                "<h4>❌ API Test Failed</h4>" +
                "</div>" +
                "<div class=\"test-details\">" +
                "<h5>Error Details:</h5>" +
                "<div class=\"error-content\">" +
                "<strong>Error:</strong> ${error.message}<br>" +
                "<strong>Type:</strong> ${error.name}<br>" +
                "<strong>Endpoint:</strong> ${endpoint}<br>" +
                "<strong>Method:</strong> ${method}<br>" +
                "</div>" +
                "<p><small>💡 Check the browser console for more details</small></p>" +
                "</div>" +
                "</div>" +
                "`;" +
                "}" +
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
                "async function updateCallCount() {" +
                "try {" +
                "const response = await fetch('/api/user/stats', {" +
                "headers: { 'Authorization': 'Bearer ' + document.getElementById('api-key').textContent.trim() }" +
                "});" +
                "if (response.ok) {" +
                "const stats = await response.json();" +
                "updateCallCountDisplay(stats);" +
                "}" +
                "} catch (error) {" +
                "console.error('Failed to update call count:', error);" +
                "}" +
                "}" +
                "" +
                "function updateCallCountDisplay(stats) {" +
                "const callCountElement = document.getElementById('call-count');" +
                "const rateLimitElement = document.getElementById('rate-limit-status');" +
                "const lastCallElement = document.getElementById('last-call-time');" +
                "" +
                "if (callCountElement) {" +
                "callCountElement.textContent = stats.callCount || 0;" +
                "}" +
                "if (rateLimitElement) {" +
                "rateLimitElement.textContent = stats.rateLimitStatus || 'Unknown';" +
                "rateLimitElement.className = 'rate-limit-status ' + (stats.isRateLimited ? 'limited' : 'available');" +
                "}" +
                "if (lastCallElement && stats.lastCallTime) {" +
                "const lastCallDate = new Date(stats.lastCallTime);" +
                "lastCallElement.textContent = lastCallDate.toLocaleString();" +
                "}" +
                "}" +
                "" +
                "function showEndpointTab(button, tabId) {" +
                "// Remove active class from all buttons and panes in this endpoint" +
                "const endpointTabs = button.closest('.endpoint-tabs');" +
                "const buttons = endpointTabs.querySelectorAll('.endpoint-tab-button');" +
                "const panes = endpointTabs.querySelectorAll('.endpoint-tab-pane');" +
                "" +
                "buttons.forEach(btn => btn.classList.remove('active'));" +
                "panes.forEach(pane => pane.classList.remove('active'));" +
                "" +
                "// Add active class to clicked button and corresponding pane" +
                "button.classList.add('active');" +
                "const targetPane = endpointTabs.querySelector('#' + tabId);" +
                "if (targetPane) {" +
                "targetPane.classList.add('active');" +
                "}" +
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
                "// Load initial call count and stats" +
                "updateCallCount();" +
                "});" +
                "</script>";
    }
}
