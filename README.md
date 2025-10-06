# Callable APIs Web Application

A modernized Java REST API service built with Jersey and deployed on Tomcat in Docker containers.

## Overview

This project provides a REST API service for Callable APIs, featuring:
- Modern Java 21 (Corretto 21) with Jakarta EE
- Jersey 3.x for REST API implementation
- Tomcat 11 for application server
- Docker containerization for consistent deployment
- Health checks and monitoring
- Optimized for AWS Elastic Beanstalk deployment

## API Endpoints

- `GET /api/v1/calendar/date` - Returns current date in JSON format

## Prerequisites

- Docker and Docker Compose
- Java 21+ (for local development)
- Gradle 8+ (for local development)

## Quick Start with Docker

### Using Docker Compose (Recommended)

1. **Build and run the application:**
   ```bash
   docker-compose up --build
   ```

2. **Access the API:**
   ```bash
   curl http://localhost:8080/api/v1/calendar/date
   ```

3. **Stop the application:**
   ```bash
   docker-compose down
   ```

### Using Docker directly

1. **Build the WAR file:**
   ```bash
   ./gradlew war
   ```

2. **Build the Docker image:**
   ```bash
   docker build -t callableapis-webapp .
   ```

3. **Run the container:**
   ```bash
   docker run -p 8080:8080 callableapis-webapp
   ```

## Development

### Local Development Setup

1. **Build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run tests:**
   ```bash
   ./gradlew test
   ```

3. **Build WAR file:**
   ```bash
   ./gradlew war
   ```

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/callableapis/api/
│   │       ├── APIApplication.java          # Jersey application configuration
│   │       └── handlers/v1/
│   │           └── CalendarResource.java    # REST endpoint implementation
│   └── webapp/
│       └── WEB-INF/
│           └── web.xml                     # Tomcat deployment descriptor
├── test/
│   └── java/
│       └── com/callableapis/api/
│           └── CalendarResourceTest.java   # Unit tests
Dockerfile                                 # Docker container configuration
docker-compose.yml                        # Docker Compose configuration
build.gradle                             # Gradle build configuration
```

## Deployment

### AWS Elastic Beanstalk Deployment

This application is optimized for deployment on AWS Elastic Beanstalk using the following AMI:
- **AMI ID**: ami-061519a9509247f5b
- **Base OS**: Amazon Linux 2023
- **Java**: Corretto 21 (OpenJDK 21)
- **Tomcat**: Version 11
- **Architecture**: x86_64

#### Deployment Steps:

1. **Build the WAR file:**
   ```bash
   ./gradlew war
   ```

2. **Deploy to Elastic Beanstalk:**
   - Upload the WAR file (`build/libs/callableapis-webapp-1.0.0.war`) to Elastic Beanstalk
   - The application will be deployed to `/var/lib/tomcat/webapps/` on the instance
   - Access the API at: `http://your-eb-url/api/v1/calendar/date`

#### Docker Deployment (Alternative):

1. **Build the Docker image:**
   ```bash
   docker build -t callableapis-webapp .
   ```

2. **Tag for your registry:**
   ```bash
   docker tag callableapis-webapp:latest your-registry/callableapis-webapp:latest
   ```

3. **Push to registry:**
   ```bash
   docker push your-registry/callableapis-webapp:latest
   ```

4. **Deploy on EC2:**
   ```bash
   docker run -d -p 80:8080 --name callableapis your-registry/callableapis-webapp:latest
   ```

### Health Checks

The application includes health checks that verify the API is responding:
- Docker health check: `curl -f http://localhost:8080/api/v1/calendar/date`
- Check container health: `docker ps` (look for "healthy" status)

## Configuration

### Environment Variables

- `JAVA_OPTS`: JVM options (default: `-Xmx512m -Xms256m`)
- `PORT`: Application port (default: 8080)

### Logging

Logs are available in the container at `/usr/local/tomcat/logs/` and can be accessed via:
```bash
docker logs callableapis-webapp
```

## Migration from Legacy System

This project has been modernized from the original Grizzly-based implementation:

### Changes Made:
- ✅ Upgraded from Java 8 to Java 21 (Corretto 21)
- ✅ Migrated from Grizzly HTTP server to Tomcat 11
- ✅ Updated from Jersey 2.x to Jersey 3.x
- ✅ Migrated from javax.* to jakarta.* APIs
- ✅ Added Docker containerization
- ✅ Implemented proper WAR packaging
- ✅ Added health checks and monitoring
- ✅ Updated dependencies to latest stable versions
- ✅ Optimized for AWS Elastic Beanstalk deployment

### Removed Legacy Files:
- `App.java` - Replaced by Tomcat servlet container
- `appspec.yml` - Replaced by Docker deployment
- `codedeploy-app-start.sh` - Replaced by Docker CMD

## Troubleshooting

### Common Issues

1. **Port already in use:**
   ```bash
   # Change port in docker-compose.yml or use different port
   docker run -p 8081:8080 callableapis-webapp
   ```

2. **Build failures:**
   ```bash
   # Clean and rebuild
   ./gradlew clean build
   docker-compose up --build --force-recreate
   ```

3. **Health check failures:**
   ```bash
   # Check container logs
   docker logs callableapis-webapp
   
   # Check if application is running
   docker exec callableapis-webapp curl http://localhost:8080/api/v1/calendar/date
   ```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

[Add your license information here]
