# Docker Hub Setup and Publishing

This document describes how to set up Docker Hub publishing for the Callable APIs service.

## Overview

The service uses GitHub Actions to automatically build and push Docker images to Docker Hub when:
- Code is pushed to `main` or `develop` branches
- Tags are created (e.g., `v1.0.0`)
- Pull requests are opened (build only, no push)

## Required Setup

### 1. Docker Hub Account

1. Create a Docker Hub account at [hub.docker.com](https://hub.docker.com)
2. Create a new repository named `callableapis` (or match your GitHub repository name)
3. Set the repository visibility (public or private)

### 2. GitHub Secrets

Add the following secrets to your GitHub repository:

#### Required Secrets

| Secret Name | Description | Example Value |
|-------------|-------------|---------------|
| `DOCKERHUB_USERNAME` | Your Docker Hub username | `your-dockerhub-username` |
| `DOCKERHUB_TOKEN` | Docker Hub access token | `dckr_pat_xxxxxxxxxxxxxxxxxxxx` |

#### How to Create Docker Hub Token

1. Go to [Docker Hub Account Settings](https://hub.docker.com/settings/security)
2. Click "New Access Token"
3. Enter a description (e.g., "GitHub Actions - Callable APIs")
4. Set permissions to "Read, Write, Delete"
5. Click "Generate"
6. Copy the token and add it to GitHub secrets as `DOCKERHUB_TOKEN`

### 3. GitHub Repository Settings

1. Go to your GitHub repository
2. Navigate to Settings → Secrets and variables → Actions
3. Click "New repository secret"
4. Add both `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN`

## Image Naming Convention

The workflow uses the following naming convention:

- **Registry**: `docker.io` (Docker Hub)
- **Repository**: `{github-owner}/{github-repo}` (e.g., `callableapis/services`)
- **Tags**:
  - `latest` - Always points to the latest main branch build
  - `{version}` - Semantic version from git tags (e.g., `v1.0.0`)
  - `{major}.{minor}` - Major.minor version (e.g., `1.0`)
  - `{commit-sha}` - Short commit SHA for branch builds
  - `{commit-sha}-{branch}` - Commit SHA with branch name for non-main branches

## Workflow Triggers

### Automatic Triggers

1. **Push to main/develop**: Builds and pushes image with `latest` tag
2. **Create tag**: Builds and pushes image with version tags
3. **Pull request**: Builds image but doesn't push (for testing)

### Manual Triggers

You can also trigger the workflow manually:
1. Go to Actions tab in GitHub
2. Select "Build and Push Docker Image"
3. Click "Run workflow"

## Container Version Management

The workflow automatically generates a `CONTAINER_VERSION` file:

- **For tags**: Uses the tag name (e.g., `v1.0.0` → `1.0.0`)
- **For branches**: Uses commit SHA (e.g., `a1b2c3d4`)
- **For non-main branches**: Includes branch name (e.g., `a1b2c3d4-feature-branch`)

## Usage Examples

### Pull and Run Latest Image

```bash
# Pull the latest image
docker pull callableapis/services:latest

# Run the container
docker run -p 8080:8080 callableapis/services:latest
```

### Pull and Run Specific Version

```bash
# Pull a specific version
docker pull callableapis/services:1.0.0

# Run the specific version
docker run -p 8080:8080 callableapis/services:1.0.0
```

### Pull and Run Development Version

```bash
# Pull a development build
docker pull callableapis/services:a1b2c3d4-develop

# Run the development build
docker run -p 8080:8080 callableapis/services:a1b2c3d4-develop
```

## Environment Variables

The Docker image supports the following environment variables:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `GITHUB_CLIENT_ID` | GitHub OAuth client ID | - | Yes |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth client secret | - | Yes |
| `GITHUB_REDIRECT_URI` | GitHub OAuth redirect URI | `https://api.callableapis.com/api/auth/callback` | No |
| `API_KEY_SALT` | API key generation salt | - | Yes |
| `API_RATE_LIMIT_QPS` | API rate limit (queries per second) | `10` | No |

### Example Docker Run with Environment Variables

```bash
docker run -p 8080:8080 \
  -e GITHUB_CLIENT_ID=your-client-id \
  -e GITHUB_CLIENT_SECRET=your-client-secret \
  -e API_KEY_SALT=your-salt \
  callableapis/services:latest
```

## Secrets Management

The Docker image supports multiple secrets management approaches:

### 1. Environment Variables (Development)

```bash
docker run -p 8080:8080 \
  -e GITHUB_CLIENT_ID=dev-client-id \
  -e GITHUB_CLIENT_SECRET=dev-client-secret \
  -e GITHUB_REDIRECT_URI=http://localhost:8080/api/auth/callback \
  callableapis/services:latest
```

### 2. Ansible Vault (Production)

```bash
# Mount vault files
docker run -p 8080:8080 \
  -v /app/vault-password:/app/vault-password:ro \
  -v /app/secrets:/app/secrets:ro \
  callableapis/services:latest
```

### 3. AWS Parameter Store (Fallback)

```bash
# Ensure AWS credentials are available
docker run -p 8080:8080 \
  -e AWS_ACCESS_KEY_ID=your-access-key \
  -e AWS_SECRET_ACCESS_KEY=your-secret-key \
  -e AWS_DEFAULT_REGION=us-east-1 \
  callableapis/services:latest
```

## Monitoring and Logs

### View Container Logs

```bash
# View logs
docker logs <container-id>

# Follow logs
docker logs -f <container-id>
```

### Health Check

The container includes a health check endpoint:

```bash
# Check health
curl http://localhost:8080/api/health
```

## Troubleshooting

### Common Issues

1. **Authentication Failed**
   - Verify `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` are correct
   - Check token permissions include "Read, Write, Delete"

2. **Build Failed**
   - Check GitHub Actions logs for specific error messages
   - Verify all required secrets are set

3. **Image Not Found**
   - Ensure the repository name matches your Docker Hub repository
   - Check if the image was successfully pushed in Docker Hub

4. **Container Won't Start**
   - Check environment variables are set correctly
   - Verify secrets management configuration
   - Check container logs for error messages

### Debug Commands

```bash
# Check if image exists locally
docker images | grep callableapis

# Inspect image
docker inspect callableapis/services:latest

# Run container in interactive mode
docker run -it callableapis/services:latest /bin/bash

# Check container status
docker ps -a
```

## Security Considerations

1. **Secrets Management**: Never hardcode secrets in Docker images
2. **Token Security**: Use Docker Hub access tokens with minimal required permissions
3. **Image Scanning**: Regularly scan images for vulnerabilities
4. **Access Control**: Limit who can push to the Docker Hub repository

## Release Process

### Creating a Release

1. **Create and push a tag**:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. **GitHub Actions will automatically**:
   - Build the Docker image
   - Push to Docker Hub with version tags
   - Create a GitHub release
   - Update the `CONTAINER_VERSION` file

3. **Verify the release**:
   - Check Docker Hub for the new image
   - Verify GitHub release was created
   - Test the new image

### Rollback Process

1. **Identify the previous working version**
2. **Pull and run the previous version**:
   ```bash
   docker pull callableapis/services:0.9.0
   docker run -p 8080:8080 callableapis/services:0.9.0
   ```
3. **Update any deployment configurations**

## Support

For issues with Docker Hub publishing:

1. Check GitHub Actions logs
2. Verify Docker Hub repository settings
3. Review this documentation
4. Contact the development team

## Related Documentation

- [SECRETS_MANAGEMENT.md](SECRETS_MANAGEMENT.md) - Secrets management configuration
- [README.md](README.md) - General project documentation
- [UI_TESTING.md](UI_TESTING.md) - UI testing setup
