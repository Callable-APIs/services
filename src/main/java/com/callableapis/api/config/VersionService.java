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
    private String gitCommitHashFull = "unknown";
    private String repoName = "Callable-APIs/services";
    private String buildTime = "unknown";

    private VersionService() {
        loadVersionInfo();
    }

    public static VersionService getInstance() {
        return INSTANCE;
    }

    private void loadVersionInfo() {
        try {
            // Try to load from version.properties file (generated during build from
            // template)
            InputStream is = getClass().getClassLoader().getResourceAsStream("version.properties");
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                gitCommitHash = props.getProperty("version.commit", "");
                gitCommitHashFull = props.getProperty("version.commit.full", gitCommitHash);
                repoName = props.getProperty("version.repo", "Callable-APIs/services");
                buildTime = props.getProperty("version.build.time", "unknown");
                logger.info("Loaded version info from version.properties - Repo: " + repoName + ", Commit: "
                        + gitCommitHashFull + ", Build: " + buildTime);
            } else {
                // Fallback: try to get git info from environment variables or system properties
                String envCommit = System.getenv("GIT_COMMIT_ID") != null ? System.getenv("GIT_COMMIT_ID")
                        : System.getenv("GITHUB_SHA");
                gitCommitHash = System.getProperty("version.commit",
                        envCommit != null ? envCommit.substring(0, Math.min(7, envCommit.length())) : "");
                gitCommitHashFull = System.getProperty("version.commit.full", envCommit != null ? envCommit : "");
                repoName = System.getProperty("version.repo",
                        System.getenv("GITHUB_REPOSITORY") != null ? System.getenv("GITHUB_REPOSITORY")
                                : "Callable-APIs/services");
                buildTime = System.getProperty("version.build.time",
                        System.getenv("BUILD_TIME") != null ? System.getenv("BUILD_TIME")
                                : new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                                        .format(new java.util.Date()));
                logger.info("Using fallback version info - Repo: " + repoName + ", Commit: " + gitCommitHashFull
                        + ", Build: " + buildTime);
            }
        } catch (IOException e) {
            logger.warning("Failed to load version info: " + e.getMessage());
            // Set fallback values
            gitCommitHash = "";
            gitCommitHashFull = "";
            buildTime = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date());
        }
    }

    public String getGitCommitHash() {
        return gitCommitHash;
    }

    public String getGitCommitHashFull() {
        return gitCommitHashFull;
    }

    public String getRepoName() {
        return repoName;
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

    /**
     * Get the full version string in the format: repo/full-commit-hash
     * Example: Callable-APIs/services/99f046cbd4fa2741d293310a6b1b7310e96f1ba3
     */
    public String getFullVersionString() {
        if (gitCommitHashFull != null && !gitCommitHashFull.isEmpty() && !gitCommitHashFull.equals("unknown")) {
            return repoName + "/" + gitCommitHashFull;
        }
        // Fallback to short hash if full hash not available
        if (gitCommitHash != null && !gitCommitHash.isEmpty() && !gitCommitHash.equals("unknown")) {
            return repoName + "/" + gitCommitHash;
        }
        return repoName + "/unknown";
    }
}
