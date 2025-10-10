package com.callableapis.api.web;

import com.callableapis.api.config.VersionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RootServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("RootServlet.doGet() called!");
        
        // Add version information to request attributes for JSP
        VersionService versionService = VersionService.getInstance();
        String gitCommitHash = versionService.getGitCommitHash();
        String shortCommitHash = versionService.getShortCommitHash();
        String buildTime = versionService.getBuildTime();
        
        System.out.println("Version info - Commit: " + gitCommitHash + ", Build: " + buildTime);
        
        request.setAttribute("gitCommitHash", gitCommitHash);
        request.setAttribute("shortCommitHash", shortCommitHash);
        request.setAttribute("buildTime", buildTime);
        
        // Add OAuth URLs
        request.setAttribute("oauthLoginUrl", "/api/auth/login");
        request.setAttribute("oauthCallbackUrl", "/api/auth/callback");
        
        // Forward to JSP
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
