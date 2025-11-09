# Callable APIs Services

A modernized Java REST API service built with Jersey and deployed on Tomcat in Docker containers.

## Overview

This project provides a REST API service for Callable APIs, featuring:
- Modern Java 21 (Corretto 21) with Jakarta EE
- Jersey 3.x for REST API implementation
- Tomcat 11 for application server
- Docker containerization for consistent deployment
- OIDC authentication via GitHub
- API key-based authentication
- Health checks and monitoring
- Multi-architecture support (AMD64/ARM64)

## API Endpoints

### Public Endpoints
- `GET /` - Home page with API documentation
- `GET /health` - Health check endpoint (returns JSON)
- `GET /api/health` - API health check (returns JSON)
- `GET /api/status` - Detailed status information (returns JSON)
- `GET /api/auth/login` - Initiate GitHub OAuth login

### Authenticated Endpoints (Require Bearer Token)
- `GET /api/v1/calendar/date` - Returns current date in JSON format
- `GET /api/v2/calendar/*` - V2 calendar endpoints
- `GET /user/me` - Get current user identity and API key
- `POST /user/key/rotate` - Rotate API key
- `GET /user/stats` - Get user API usage statistics

## Prerequisites

- Docker and Docker Compose
- Java 21+ (for local development)
- Gradle 8+ (for local development)

## Quick Start

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

### Local Development Setup

1. **Build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run tests:**
   ```bash
   ./gradlew test
   ```

3. **Run validation checks:**
   ```bash
   ./run_checks.sh
   ```

4. **Build WAR file:**
   ```bash
   ./gradlew war
   ```

## Configuration

### OIDC Authentication Setup

The service uses GitHub OAuth for authentication. To set up:

1. **Create GitHub OAuth App:**
   - Go to https://github.com/settings/developers
   - Click "New OAuth App"
   - Set Authorization callback URL: `https://api.callableapis.com/api/auth/callback` (production) or `http://localhost:8080/api/auth/callback` (local)
   - Copy Client ID and Client Secret

2. **Configure Secrets** (see Secrets Management section below)

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `GITHUB_CLIENT_ID` | GitHub OAuth client ID | - | Yes |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth client secret | - | Yes |
| `GITHUB_REDIRECT_URI` | OAuth redirect URI | `https://api.callableapis.com/api/auth/callback` | No |
| `API_KEY_SALT` | API key generation salt | - | Yes |
| `API_RATE_LIMIT_QPS` | Rate limit (queries per second) | `10` | No |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` | No |
| `AWS_DEFAULT_REGION` | AWS region for Parameter Store | `us-east-1` | No |

### Secrets Management

The service supports multiple secrets management approaches:

1. **Ansible Vault** (Primary - for containerized nodes):
   - Vault password: `/app/vault-password`
   - Secrets file: `/app/secrets/all-secrets.env`
   - Format: YAML with `vault_` prefix converted to environment variables

2. **AWS Parameter Store** (Fallback - for Elastic Beanstalk):
   - Path: `/callableapis/github-oidc/`
   - Parameters: `github_client_id`, `github_client_secret`, `github_redirect_uri`
   - Uses version-based cache invalidation for immediate updates

3. **Environment Variables** (Development):
   - Direct environment variable configuration
   - Used when vault/Parameter Store unavailable

For detailed secrets management documentation, see [SECRETS_MANAGEMENT.md](SECRETS_MANAGEMENT.md).

## Testing

The project includes comprehensive testing:

- **Unit Tests**: `./gradlew test`
- **Integration Tests**: Included in test suite
- **UI Tests**: `./run_ui_tests.sh` (Selenium WebDriver tests)
- **Validation**: `./run_checks.sh` (runs all checks including static analysis)

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run UI tests (requires running application)
./run_ui_tests.sh

# Run all validation checks
./run_checks.sh
```

For detailed UI testing documentation, see [UI_TESTING.md](UI_TESTING.md).

## Docker

### Building Locally

```bash
# Build the services container
docker build -f containers/services/Dockerfile -t rl337/callableapis:services .

# Run the container
docker run -p 8080:8080 rl337/callableapis:services
```

### Docker Hub

The container is automatically built and pushed to Docker Hub as `rl337/callableapis:services` via GitHub Actions when code is pushed to `main` branch.

- **Latest**: `rl337/callableapis:services:latest`
- **Versioned**: `rl337/callableapis:services:v1.0.0` (from git tags)
- **Multi-arch**: Supports both AMD64 and ARM64

For detailed Docker Hub setup, see [DOCKER_HUB_SETUP.md](DOCKER_HUB_SETUP.md).

## Deployment

### AWS Elastic Beanstalk

This application is optimized for deployment on AWS Elastic Beanstalk:
- **AMI**: Amazon Linux 2023
- **Java**: Corretto 21 (OpenJDK 21)
- **Tomcat**: Version 11
- **Architecture**: x86_64

### Container Deployment

1. **Pull the image:**
   ```bash
   docker pull rl337/callableapis:services:latest
   ```

2. **Run with secrets:**
   ```bash
   # Using Ansible Vault
   docker run -p 8080:8080 \
     -v /app/vault-password:/app/vault-password:ro \
     -v /app/secrets:/app/secrets:ro \
     rl337/callableapis:services:latest
   
   # Using AWS Parameter Store
   docker run -p 8080:8080 \
     -e AWS_ACCESS_KEY_ID=your-key \
     -e AWS_SECRET_ACCESS_KEY=your-secret \
     -e AWS_DEFAULT_REGION=us-west-2 \
     rl337/callableapis:services:latest
   ```

### Health Checks

- **Health endpoint**: `GET /health` - Returns `{"status": "healthy", "timestamp": "...", "version": "..."}`
- **API health**: `GET /api/health` - Returns `{"status": "ok", "timestamp": "...", "version": "..."}`
- **Status endpoint**: `GET /api/status` - Detailed status information

## Project Structure

```
src/
├── main/
│   ├── java/com/callableapis/api/
│   │   ├── APIApplication.java          # Jersey application configuration
│   │   ├── config/                      # Configuration classes
│   │   ├── di/                          # Dependency injection bindings
│   │   ├── handlers/                    # REST endpoint handlers
│   │   ├── health/                      # Health check endpoints
│   │   ├── secrets/                    # Secrets management
│   │   ├── security/                    # Authentication & authorization
│   │   └── web/                        # Web resources (JSP pages)
│   └── webapp/                         # JSP pages and web.xml
├── test/                               # Unit and integration tests
└── uiTest/                             # Selenium UI tests
containers/services/                    # Docker configuration
.github/workflows/                      # CI/CD workflows
```

## Development Workflow

1. **Create a GitHub Issue** for the task
2. **Create a branch** named `{issue-id}_{snake-case-title}`
3. **Make changes** and add tests
4. **Run checks** before committing: `./gradlew compileJava compileTestJava` (minimum) or `./run_checks.sh` (full)
5. **Commit and push** to the branch
6. **Create a PR** referencing the issue
7. **Wait for CI** to pass
8. **Update the issue** when complete

For detailed agent instructions, see [AGENTS.md](AGENTS.md).

## Troubleshooting

### Common Issues

1. **Port already in use:**
   ```bash
   docker run -p 8081:8080 rl337/callableapis:services
   ```

2. **Build failures:**
   ```bash
   ./gradlew clean build
   docker-compose up --build --force-recreate
   ```

3. **Secrets not loading:**
   - Check vault files are mounted correctly
   - Verify AWS credentials and region
   - Check container logs: `docker logs <container-id>`

4. **OIDC callback failures:**
   - Verify redirect URI matches GitHub OAuth app configuration
   - Check Parameter Store has correct HTTPS redirect URI
   - Ensure `AuthenticationStatsService` is registered in DI container

### Debug Commands

```bash
# Check container logs
docker logs <container-id>

# Check health
curl http://localhost:8080/api/health

# Check status
curl http://localhost:8080/api/status

# Test authentication
curl -v http://localhost:8080/api/auth/login
```

## Additional Documentation

- [AGENTS.md](AGENTS.md) - Instructions for AI agents working on this project
- [SECRETS_MANAGEMENT.md](SECRETS_MANAGEMENT.md) - Detailed secrets management documentation
- [UI_TESTING.md](UI_TESTING.md) - UI testing setup and usage
- [DOCKER_HUB_SETUP.md](DOCKER_HUB_SETUP.md) - Docker Hub publishing setup
- [containers/README.md](containers/README.md) - Container-specific documentation

## License

[Add your license information here]
