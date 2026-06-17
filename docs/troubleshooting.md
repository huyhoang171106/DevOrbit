# Troubleshooting Guide

This document lists common issues that may arise during development, setup, or deployment of DevOrbit, along with their causes, checks, and resolution steps.

---

## 1. Database Connection Failures

### Symptom
The backend console outputs connection timeouts or JDBC execution exceptions on startup, for example:
`Connection to localhost:5432 refused` or `Cannot acquire connection from HikariPool`.

### Cause
- The PostgreSQL database server is not running locally.
- The connection parameters in the backend `.env` file do not match the database setup.
- Network access restrictions (firewall, IP white-listing) prevent connection to a remote Supabase instance.

### Verification Check
- Test connection using a PostgreSQL CLI or GUI client (e.g. pgAdmin, DBeaver) using the credentials from `.env`.
- Check if PostgreSQL service is running:
  - Windows: Run `Get-Service -Name postgresql*` in PowerShell.
  - Linux: Run `systemctl status postgresql` in Bash.

### Resolution Steps
1. Start the database server if it is offline.
2. Verify the `DATABASE_URL` in `devorbit-api/.env` follows the pattern:
   `jdbc:postgresql://<host>:<port>/<database_name>`
3. If using Supabase, ensure your IP address is allowed in the Supabase network settings, or connect using the transaction pooler string.

---

## 2. Backend Fails to Start (Port in Use)

### Symptom
Backend server shutdown during startup with a message:
`Web server failed to start. Port 8080 was already in use.`

### Cause
Another process (e.g., another running Spring instance, a Docker container, or an alternative web server) is already listening on port 8080.

### Verification Check
Find the PID of the process using port 8080:
- Windows: Run `netstat -ano | findstr 8080` in Command Prompt.
- Linux / macOS: Run `lsof -i :8080` in terminal.

### Resolution Steps
- Kill the conflicting process using its PID:
  - Windows: `taskkill /F /PID <PID>`
  - Linux / macOS: `kill -9 <PID>`
- Alternatively, modify the port in `devorbit-api/.env` by setting `SERVER_PORT=8081` (remember to update the frontend `VITE_PROXY_TARGET` accordingly).

---

## 3. RLS Access Denied (PostgREST direct bypass)

### Symptom
Direct queries to Supabase via JavaScript client return:
`403 Forbidden` or `permission denied for table`.

### Cause
The system database posture is backend-owned. Direct API access to public tables from client-side code is denied via RLS (Row Level Security) policies enforced by `SupabaseDatabaseHardeningInitializer` on startup.

### Verification Check
Check if the table is listed in `SupabaseDatabaseHardeningInitializer.java` under the `declareBackendOwnedTables` loop.

### Resolution Steps
- Do not call the Supabase client directly from React code for backend-owned tables.
- Route all data requests through the Spring Boot API instead.

---

## 4. Frontend Proxy Errors (CORS or 504 Gateway Timeout)

### Symptom
React client requests to `/api/*` fail with:
`504 Gateway Timeout`, `ERR_CONNECTION_REFUSED`, or `Access-Control-Allow-Origin` missing headers.

### Cause
- The backend server is not running.
- Vite dev server proxy target does not match the port or hostname of the Spring Boot backend.
- The backend CORS origins do not include the frontend URL (e.g. `http://localhost:5173`).

### Verification Check
- Confirm the backend is running by navigating to `http://localhost:8080/v3/api-docs` (admin credentials required).
- Check the value of `VITE_PROXY_TARGET` in `devorbit-web/.env`.
- Check the value of `CORS_ALLOWED_ORIGINS` in `devorbit-api/.env`.

### Resolution Steps
1. Start the backend application.
2. Synchronize target hosts: if the backend runs on port 8081, set `VITE_PROXY_TARGET=http://localhost:8081` in the frontend configuration.
3. Update `CORS_ALLOWED_ORIGINS` in `devorbit-api/.env` to include your exact frontend URL (with port).

---

## 5. JWT Expiration and Access Denied

### Symptom
API calls return `401 Unauthorized` or `403 Forbidden` with the payload:
`{"error": "Vui lòng đăng nhập"}` or `{"error": "Bạn không có quyền truy cập"}`.

### Cause
- The user is attempting to access a route without providing a JWT token.
- The JWT has expired (default limit: 120 minutes).
- The student token corresponds to a student account that has been set to inactive (`active = false`) in the database.
- The token signature is invalid (JWT secret key was rotated).

### Verification Check
- Inspect the request headers in your browser developer tools to ensure the `Authorization` header has the form `Bearer <token>`.
- Check if the JWT secret in `devorbit-api/.env` matches the key used when generating the token.

### Resolution Steps
1. Log out and log back in to obtain a fresh token.
2. For student users, verify in the database that `active` is set to `true` in the `student_users` table.

---

## 6. WebSocket Community Chat Connection Failures

### Symptom
The community chat interface displays connectivity alerts, and the console reports:
`STOMP: Connect failed` or `SockJS connection failure`.

### Cause
- The token passed in the connection headers is missing, invalid, or expired.
- An administrator is attempting to connect to the community chat (the channel interceptor requires the token type to be `STUDENT`).
- The WebSocket port does not match the running backend instance.

### Verification Check
Check the browser console logs to see the exception thrown by `WebSocketConfig.java` (e.g., `Invalid community WebSocket token` or `Community WebSocket requires a student token`).

### Resolution Steps
1. Ensure you are logged in as a student (not an administrator) before accessing the community chat.
2. Verify that the WebSocket proxy path `/ws` is properly defined in `vite.config.ts` and matches the proxy target.
