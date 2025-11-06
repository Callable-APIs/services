package com.callableapis.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Service to get version information including git commit hash.
 * Reads from git.properties file generated during build.
 */
public final class VersionService {
    private static final Logger logger = Logger.getLogger(VersionService.class.getName());
    private static final VersionService INSTANCE = new VersionService();
    
    private String gitCommitHash = "unknown";
    private String buildTime = "unknown";
    
    private VersionService() {
        loadVersionInfo();
    }
    
    public static VersionService getInstance() {
        return INSTANCE;
    }
    
    private void loadVersionInfo() {
        try {
            // Try to load from version.properties file (generated during build from template)
            InputStream is = getClass().getClassLoader().getResourceAsStream("version.properties");
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                gitCommitHash = props.getProperty("version.commit", "");
                buildTime = props.getProperty("version.build.time", "unknown");
                logger.info("Loaded version info from version.properties - Commit: " + gitCommitHash + ", Build: " + buildTime);
            } else {
                // Fallback: try to get git info from environment variables or system properties
                gitCommitHash = System.getProperty("version.commit", 
                    System.getenv("GIT_COMMIT_ID") != null ? 
                    System.getenv("GIT_COMMIT_ID").substring(0, Math.min(7, System.getenv("GIT_COMMIT_ID").length())) : 
                    "");
                buildTime = System.getProperty("version.build.time", 
                    System.getenv("BUILD_TIME") != null ? 
                    System.getenv("BUILD_TIME") : 
                    new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date()));
                logger.info("Using fallback version info - Commit: " + gitCommitHash + ", Build: " + buildTime);
            }
        } catch (IOException e) {
            logger.warning("Failed to load version info: " + e.getMessage());
            // Set fallback values
            gitCommitHash = "";
            buildTime = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date());
        }
    }
    
    public String getGitCommitHash() {
        return gitCommitHash;
    }
    
    public String getBuildTime() {
        return buildTime;
    }
    
    public String getShortCommitHash() {
        if (gitCommitHash == null || gitCommitHash.isEmpty() || gitCommitHash.equals("unknown")) {
            return "";
        }
        return gitCommitHash.length() > 7 ? gitCommitHash.substring(0, 7) : gitCommitHash;
    }
}
