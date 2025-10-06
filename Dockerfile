# Multi-stage build: Build stage
FROM gradle:8.5-jdk21 AS builder

# Set working directory
WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/

# Download dependencies (this layer will be cached if build.gradle doesn't change)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the WAR file
RUN gradle war --no-daemon

# Runtime stage: Use official Tomcat image with Java 21
FROM tomcat:11.0-jdk21-openjdk-slim

# Set maintainer
LABEL maintainer="callableapis@example.com"
LABEL description="Modernized Callable APIs REST Service"

# Set environment variables
ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# Create app directory
WORKDIR $CATALINA_HOME

# Remove default Tomcat webapps
RUN rm -rf $CATALINA_HOME/webapps/*

# Copy the WAR file from builder stage
COPY --from=builder /app/build/libs/callableapis-webapp-1.0.0.war $CATALINA_HOME/webapps/ROOT.war

# Create logs directory
RUN mkdir -p $CATALINA_HOME/logs

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Expose port 8080
EXPOSE 8080

# Set default command
CMD ["catalina.sh", "run"]

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/calendar/date || exit 1
