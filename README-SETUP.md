# Setup: OIDC + API Key Auth

Environment variables required:

- `GITHUB_CLIENT_ID`: GitHub OAuth app Client ID
- `GITHUB_CLIENT_SECRET`: GitHub OAuth app Client Secret
- `API_KEY_SALT`: Random secret string used to salt API key hashes
- `API_RATE_LIMIT_QPS` (optional): Default 10

Endpoints:

- `GET /` → public instructions page (with GitHub connect link)
- `GET /auth/login` → redirects to GitHub
- `GET /auth/callback?code=...` → returns `{ identity, apiKey }` (callback hardcoded to `https://api.callableapis.com/auth/callback`)
- `GET /user/me` → requires `Authorization: Bearer <apiKey>`, returns `{ identity, apiKey }`
- `POST /user/key/rotate` → requires bearer; rotates and returns new key
- `GET /v1/calendar/date` → requires bearer; returns date
- `GET /health` → public

Notes:
- API keys are deterministic salted hashes of the OIDC identity for initial creation; rotations append a nonce.
- All requests authenticated via bearer are rate limited at 10 QPS per API key by in-memory limiter (Guava).
- For production, persist identities and keys and store rate limits in an external store.
