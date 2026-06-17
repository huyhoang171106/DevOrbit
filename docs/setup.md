# Development Environment Setup Guide

This document describes how to set up, configure, run, and test DevOrbit from scratch.

## Prerequisites

Ensure the following tools are installed on your machine:
- Java 21 JDK (e.g. Eclipse Temurin or Oracle JDK 21)
- Node.js 20 (LTS version recommended)
- Maven 3.9+ (optional, can use the included Maven wrapper `./mvnw` or `.\mvnw.cmd`)
- PostgreSQL or a Supabase account
- Git
- Android Studio with Android SDK (only for mobile app development)

## Step-by-Step Installation

### Step 1: Clone the Repository
Clone the project repository to your local machine:
```bash
git clone https://github.com/huyhoang171106/DevOrbit.git
cd DevOrbit
```

### Step 2: Database Provisioning
DevOrbit requires a PostgreSQL database (locally hosted or a Supabase project).

1. Create a database named `devorbit_db`.
2. Apply the initial schema:
   - For Supabase: run the contents of the `supabase_complete_schema.sql` file in the Supabase SQL Editor.
   - For local PostgreSQL: run the schema SQL using psql:
     ```bash
     psql -U postgres -d devorbit_db -f supabase_complete_schema.sql
     ```
3. The backend is configured to automatically harden and configure policies on startup via `SupabaseDatabaseHardeningInitializer`.

### Step 3: Configure Environment Variables

#### Backend Configuration
Copy the template and configure the variables:
- Windows (PowerShell):
  ```powershell
  copy devorbit-api\.env.example devorbit-api\.env
  ```
- Linux / macOS (Bash):
  ```bash
  cp devorbit-api/.env.example devorbit-api/.env
  ```

Open the `devorbit-api/.env` file and configure:
- `DATABASE_URL`: Connection string (e.g. `jdbc:postgresql://localhost:5432/devorbit_db` or your Supabase connection string).
- `DATABASE_USERNAME`: Database user (e.g. `postgres`).
- `DATABASE_PASSWORD`: Database password.
- `JWT_SECRET`: A secure random key of at least 256 bits.
- `OPENCODE_API_KEY`: API key to connect to OpenCode.
- `FIREWORKS_API_KEY`: API key for Fireworks embedding computations.
- `EXA_API_KEY`: API key for Exa search.

#### Frontend Configuration
Copy the template and configure the variables:
- Windows (PowerShell):
  ```powershell
  copy devorbit-web\.env.example devorbit-web\.env
  ```
- Linux / macOS (Bash):
  ```bash
  cp devorbit-web/.env.example devorbit-web/.env
  ```

Open the `devorbit-web/.env` file and verify:
- `VITE_PROXY_TARGET=http://localhost:8080`
- `WEB_PORT=5173`
- `VITE_API_BASE_URL=/api`

### Step 4: Run the Backend
Start the Spring Boot backend application:
- Windows (PowerShell):
  ```powershell
  cd devorbit-api
  .\mvnw.cmd compile -B
  .\run.bat
  ```
- Linux / macOS (Bash):
  ```bash
  cd devorbit-api
  ./mvnw compile -B
  ./mvnw spring-boot:run
  ```

The backend starts on `http://localhost:8080`.

### Step 5: Run the Frontend
Start the React application:
- All platforms:
  ```bash
  cd devorbit-web
  npm install
  npm run dev
  ```

The frontend starts on `http://localhost:5173`.

### Step 6: System Verification
Check that the services are communicating:
1. Open `http://localhost:5173` in your browser.
2. Go to the student login page and click Register. Create an account.
3. Access the Course Directory or the 3D Galaxy map. If courses render, the frontend is successfully retrieving data from the backend.
4. Try typing a query into the AI Tutor panel. If it returns an advice response, the AI pipeline is functional.

## Running Tests

Verify that your environment matches specifications by executing the test suites.

### Backend Tests
Run the JUnit test suite:
- Windows (PowerShell):
  ```powershell
  .\devorbit-api\mvnw.cmd test -f devorbit-api\pom.xml
  ```
- Linux / macOS (Bash):
  ```bash
  ./devorbit-api/mvnw test -f devorbit-api/pom.xml
  ```

### Frontend Tests
Run the Vitest suite:
- All platforms:
  ```bash
  cd devorbit-web
  npm test -- --run
  ```
