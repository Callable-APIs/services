# Multi-stage build for Callable APIs Services
# Based on rl337/callableapis:base (Alpine 3.19)

# Build stage
FROM gradle:8.5-jdk21 AS builder

# Set working directory
WORKDIR /app

# Build arguments for git information
ARG GIT_COMMIT_ID
ARG GIT_BRANCH
ARG BUILD_TIME

# Copy Gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/

# Download dependencies (this layer will be cached if build.gradle doesn't change)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src/ src/

# Set git properties as system properties for gradle
ENV GIT_COMMIT_ID=${GIT_COMMIT_ID}
ENV GIT_BRANCH=${GIT_BRANCH}
ENV BUILD_TIME=${BUILD_TIME}

# Build the WAR file
RUN gradle war --no-daemon

# Runtime stage: Use our base image
FROM rl337/callableapis:base

# Set maintainer
LABEL maintainer="rlee@tokyo3.com"
LABEL description="Callable APIs REST Service with Tomcat"
LABEL version="1.0"

# Propagate build-time git info to runtime for VersionService fallback
ARG GIT_COMMIT_ID
ARG GIT_BRANCH
ARG BUILD_TIME
ENV GIT_COMMIT_ID=${GIT_COMMIT_ID} \
    GIT_BRANCH=${GIT_BRANCH} \
    BUILD_TIME=${BUILD_TIME}

# Switch to root for installation
USER root

# Install OpenJDK 21 and download Tomcat 9 manually
RUN apk add --no-cache \
    openjdk21-jre \
    curl \
    bash \
    wget

# Set Java environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Set Tomcat environment variables
ENV CATALINA_HOME=/opt/tomcat
ENV CATALINA_BASE=/opt/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# Create tomcat directories
RUN mkdir -p $CATALINA_HOME && \
    mkdir -p /opt/tomcat/logs && \
    mkdir -p /opt/tomcat/webapps && \
    mkdir -p /opt/tomcat/conf && \
    mkdir -p /opt/tomcat/work && \
    mkdir -p /opt/tomcat/temp

# Download and install Tomcat 10 (supports Jakarta EE)
RUN cd /tmp && \
    wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.18/bin/apache-tomcat-10.1.18.tar.gz && \
    tar -xzf apache-tomcat-10.1.18.tar.gz && \
    cp -r apache-tomcat-10.1.18/* $CATALINA_HOME/ && \
    rm -rf /tmp/apache-tomcat-10.1.18*

# Remove any existing ROOT directory and copy the WAR file from builder stage
RUN rm -rf $CATALINA_BASE/webapps/ROOT
COPY --from=builder /app/build/libs/ROOT.war $CATALINA_BASE/webapps/ROOT.war

# Create secrets directories for Ansible Vault integration
RUN mkdir -p /app/secrets && \
    mkdir -p /app/vault-password && \
    chown -R appuser:appuser /app

# Set ownership for Tomcat directories
RUN chown -R appuser:appuser $CATALINA_BASE && \
    chown -R appuser:appuser $CATALINA_HOME

# Switch back to appuser (same as base image)
USER appuser

# Expose port 8080
EXPOSE 8080

# Set working directory
WORKDIR $CATALINA_BASE

# Health check - use /api/health as recommended by infra agent
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Start Tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
