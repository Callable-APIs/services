#!/bin/bash

# Script to restart Tomcat after WAR deployment
# This script is executed by AWS CodeDeploy

echo "Starting Tomcat restart process..."

# Set proper permissions on the WAR file
chown tomcat:tomcat /var/lib/tomcat/webapps/ROOT.war
chmod 644 /var/lib/tomcat/webapps/ROOT.war

# Remove old deployment if it exists
if [ -d "/var/lib/tomcat/webapps/ROOT" ]; then
    echo "Removing old deployment..."
    rm -rf /var/lib/tomcat/webapps/ROOT
fi

# Restart Tomcat service
echo "Restarting Tomcat service..."
systemctl restart tomcat

# Wait for Tomcat to start
echo "Waiting for Tomcat to start..."
sleep 10

# Check if Tomcat is running
if systemctl is-active --quiet tomcat; then
    echo "Tomcat restarted successfully"
    
    # Wait a bit more for the application to deploy
    sleep 30
    
    # Test the application
    echo "Testing application deployment..."
    if curl -f http://localhost:8080/api/v1/calendar/date > /dev/null 2>&1; then
        echo "Application is responding correctly"
        exit 0
    else
        echo "Application is not responding - checking Tomcat logs"
        tail -n 50 /var/log/tomcat/catalina.out
        exit 1
    fi
else
    echo "Failed to restart Tomcat"
    systemctl status tomcat
    exit 1
fi
