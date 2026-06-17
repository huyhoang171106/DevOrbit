# System Deployment Guide

This document describes how to deploy the DevOrbit system using containerization and outlines the checklist required for production environments.

## Deployment Model

DevOrbit is containerized using Docker, allowing service orchestration via Docker Compose. The setup consists of three containers:
1. db : PostgreSQL 16 Alpine container (stores user profiles, courses, bookmarks, and chat data).
2. api : Spring Boot 4.0.6 Java 21 container serving REST APIs and WebSockets.
3. web : React 19 single page application served via Nginx, which also acts as a reverse proxy for backend REST and WebSocket routes.

## Deployment with Docker Compose

To deploy the entire stack locally or in a staging environment:

### Step 1: Clone and Configure Environment
Copy the environment template from the root of the project (if not already done):
- Windows (PowerShell):
  ```powershell
  copy .env.example .env
  ```
- Linux / macOS (Bash):
  ```bash
  cp .env.example .env
  ```

Open the `.env` file and set the required variables:
- `POSTGRES_DB`: Name of the database (default: `devorbit_db`).
- `POSTGRES_USER`: Database username.
- `POSTGRES_PASSWORD`: Strong password for the database.
- `JWT_SECRET`: Secure cryptographic token for JWT signing (minimum 256-bit).
- `OPENCODE_API_KEY`: API key for the OpenCode Go API.
- `FIREWORKS_API_KEY`: API key for Fireworks AI embeddings.
- `EXA_API_KEY`: API key for Exa search.
- `GITHUB_TOKEN`: GitHub personal access token for scan calls.

### Step 2: Build and Run Containers
Compile, build images, and start the container group:
```bash
docker-compose up --build -d
```

This command:
1. Builds the backend `app.jar` using a Maven build image, then copies it to an Alpine JRE 21 run image.
2. Compiles frontend assets using Node 20, copies them to an Nginx image, templates the Nginx reverse-proxy configuration using `envsubst`, and starts Nginx.
3. Starts a PostgreSQL container, mounts the `pgdata` volume for persistence, and waits for a healthy database connection before launching the API container.

The web client will be accessible at `http://localhost:3000` (forwarding to Nginx port 80). The Spring Boot API is exposed at `http://localhost:8080`.

## CI/CD Workflow Checks

Automated checks are configured in the repository under `.github/workflows/`:
- `ci.yml`: Triggered on pull requests to the master branch. Compiles the Java backend (`./mvnw compile -B`) and checks frontend types and tests (`npx tsc --noEmit && npm test`).
- `security.yml`: A scheduled job running every Monday at 9:00 AM UTC. Performs `npm audit` on package dependencies and checks backend dependencies.

## Production Readiness Checklist

DevOrbit is currently designed for development and staging environments. Before promoting the system to production, complete the following tasks:

### 1. External Database Migration
- Replace the local PostgreSQL container (`db`) with a managed database service (e.g. Supabase, AWS RDS PostgreSQL).
- Disable the database container in `docker-compose.yml`.

### 2. Startup Hardening adjustments
- Set the backend environment variable `JPA_DDL_AUTO` to `none` to disable schema alterations.
- Configure `devorbit.database.hardening.enabled` to `false` in production if schema management is migrated to a tool like Liquibase or Flyway.

### 3. Secret Management
- Do not store environment variables in a plain text `.env` file on production servers.
- Use a dedicated secrets manager (e.g., AWS Secrets Manager, HashiCorp Vault, Vercel Env Secrets) to inject keys into container environments at runtime.

### 4. Domain and SSL/TLS
- Configure Nginx to use SSL/TLS by installing SSL certificates (e.g. Let's Encrypt).
- Update the backend `CORS_ALLOWED_ORIGINS` variable to match the production domain.
- Update frontend environment variables to point to the secure production backend URL.
