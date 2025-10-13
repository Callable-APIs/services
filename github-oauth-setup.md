# GitHub OAuth Setup

The OIDC flow is currently hanging because GitHub OAuth environment variables are not configured.

## Quick Fix

1. **Get GitHub OAuth App Credentials:**
   - Go to https://github.com/settings/developers
   - Click "New OAuth App"
   - Set Application name: "Callable APIs Local Dev"
   - Set Homepage URL: `http://localhost:8080`
   - Set Authorization callback URL: `http://localhost:8080/api/auth/callback`
   - Click "Register application"
   - Copy the Client ID and Client Secret

2. **Set Environment Variables:**
   
   **Option A: Update docker-compose.yml**
   ```yaml
   environment:
     - JAVA_OPTS=-Xmx512m -Xms256m
     - GITHUB_CLIENT_ID=your_actual_client_id
     - GITHUB_CLIENT_SECRET=your_actual_client_secret
     - GITHUB_REDIRECT_URI=http://localhost:8080/api/auth/callback
     - GITHUB_OAUTH_SCOPE=read:user user:email
   ```

   **Option B: Create .env file**
   ```bash
   # Create .env file in project root
   echo "GITHUB_CLIENT_ID=your_actual_client_id" > .env
   echo "GITHUB_CLIENT_SECRET=your_actual_client_secret" >> .env
   echo "GITHUB_REDIRECT_URI=http://localhost:8080/api/auth/callback" >> .env
   echo "GITHUB_OAUTH_SCOPE=read:user user:email" >> .env
   ```

3. **Restart the application:**
   ```bash
   docker compose down
   docker compose up -d
   ```

## Current Issue

The application is using placeholder values:
- `client_id=dev-client-id-placeholder` ❌
- `redirect_uri=https://api.callableapis.com/api/auth/callback` ❌

This causes GitHub OAuth to fail because the client ID doesn't exist.

## After Setup

Once you have real GitHub OAuth credentials, the flow will work:
1. User clicks login → redirects to GitHub OAuth ✅
2. GitHub redirects back with code → callback processes authentication ✅
3. Callback redirects to authenticated page ✅
4. User sees beautiful HTML page with API key ✅
