# Callable APIs Containers

This directory contains Docker configurations for the Callable APIs services.

## Services Container

The `services/` directory contains the Docker configuration for the main Callable APIs web application.

### Features

- **Base Image**: `rl337/callableapis:base` (Alpine 3.19)
- **Java Runtime**: OpenJDK 21
- **Web Server**: Tomcat 11
- **Secrets Management**: Dual support for Ansible Vault and AWS Parameter Store
- **Health Checks**: Built-in health monitoring
- **Production Ready**: Optimized for containerized deployment

### Building Locally

```bash
# Build the services container
docker build -f containers/services/Dockerfile -t rl337/callableapis:services .

# Run the container
docker run -p 8080:8080 rl337/callableapis:services
```

### Docker Hub

The container is automatically built and pushed to Docker Hub as `rl337/callableapis:services` via GitHub Actions.

### Environment Variables

The container supports the following environment variables:

- `JAVA_OPTS`: JVM options (default: optimized for containers)
- `CATALINA_OPTS`: Tomcat options (default: headless mode)

### Secrets Management

The container supports two secrets management approaches:

1. **Ansible Vault** (Primary): Files at `/app/secrets/all-secrets.env` and `/app/vault-password`
2. **AWS Parameter Store** (Fallback): Uses AWS credentials for parameter retrieval

### Health Check

The container includes a health check that verifies the API is responding:

```bash
curl -f http://localhost:8080/api/v1/calendar/date
```

### Logs

Tomcat logs are available at `/opt/tomcat/logs/` inside the container.
