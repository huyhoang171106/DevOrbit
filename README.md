# DevOrbit

DevOrbit is a multi-module school and course advisory portal built to assist students at the University of Information Technology (UIT). The system integrates course information, code repositories, student bookmarks, real-time community chat, and an AI-powered advisory tutor.

## Problem Solved

Students often struggle to find relevant reference projects (repositories) matching their curriculum courses, manage their academic path, calculate GPAs, get instant course-advisory help, or interact with fellow students in real-time. DevOrbit provides a unified interface linking academic syllabi with matching GitHub codebases, offering an AI advisor for course guidance, and real-time messaging channels.

## Key Features

1. Course Directory: Browse courses, see descriptions, sessions, syllabus details, outcomes, and assessments.
2. Repository Recommendations: List and search student-submitted GitHub repositories categorized by course, language, and tech stack. Upvote repositories and view details.
3. AI Tutor and RAG: Ask natural language questions about course curricula. The tutor uses RAG (Retrieval-Augmented Generation) over ingested syllabus documents, returning citations and course mappings.
4. AI Learning Roadmap: Generate personalized learning roadmaps based on selected academic years, target fields (e.g. Frontend, Data Science), and background.
5. Community Chat: Real-time STOMP-based chat channels partitioned by topics where students can discuss course materials. Includes automatic active presence tracking.
6. GPA Calculator: Track academic progress, calculate average grades, letter grades, and academic classifications.
7. Photobooth: Allow students to overlay frames and download pictures to celebrate academic milestones.

## System Architecture

1. Frontend: React single page application communicating with the backend via REST APIs and WebSockets.
2. Backend: Spring Boot application serving REST APIs, managing authentication, executing database queries, calling external AI models, and handling WebSocket STOMP messaging.
3. Database: PostgreSQL hosted on Supabase.
4. External Services:
   - GitHub API: Scan repository metrics, activity, and primary languages.
   - OpenCode AI: Connect to OpenCode Go API for deepseek-v4-flash chat completions.
   - Fireworks AI: Generate embeddings for knowledge chunks using Qwen 3.
   - Exa AI: Perform web searches to enrich AI advisor responses.
   - Firecrawl: Scrape course resources and reference material when compiling the knowledge base.

## Technology Stack

### Backend
- Language: Java 21
- Framework: Spring Boot 4.0.6
- Database: PostgreSQL (with vector extension)
- Security: Spring Security, JWT (io.jsonwebtoken 0.12.6)
- API Docs: Springdoc OpenAPI 2.8.6
- Real-time: Spring Boot Starter WebSocket (STOMP)
- Cache: Caffeine (Caffeine 3.2.0)
- Testing: JUnit, H2 database (test scope), Testcontainers

### Frontend
- Library: React 19
- Build Tool: Vite 6 (using TypeScript)
- State Management: Redux Toolkit (API queries) and Zustand (Galaxy store)
- Styling: TailwindCSS 3.4.0, lightningcss
- Animations: Framer Motion, GSAP
- 3D Graphics: Three.js (React Three Fiber)
- Icons: Phosphor Icons, Lucide React
- WebSockets: stompjs, sockjs-client
- Testing: Vitest, React Testing Library

### Mobile
- Language: Kotlin
- UI Framework: Jetpack Compose
- Target: Android

## Repository Structure

- devorbit-api/ : Spring Boot backend codebase.
- devorbit-web/ : React frontend codebase.
- devorbit-mobile/ : Android mobile client.
- supabase/ : Supabase database configurations and schemas.
- docs/ : System architecture, API, database, setup, deployment, and troubleshooting manuals.
- AGENTS.md : Repository guidelines and rules for agent development.

## Prerequisites

- Java 21 JDK installed
- Node.js 20 or higher installed
- Maven 3.9+ or use the Maven wrapper (mvnw)
- PostgreSQL database or Supabase account
- Android Studio / Android SDK (only for mobile development)

## Quick Start

### Backend Setup
1. Open devorbit-api/ and copy .env.example to .env
2. Configure DATABASE_URL, DATABASE_USERNAME, and DATABASE_PASSWORD
3. Build the application:
   cd devorbit-api
   ./mvnw clean compile
4. Start the backend:
   run.bat

The backend will start on port 8080.

### Frontend Setup
1. Open devorbit-web/ and copy .env.example to .env
2. Install dependencies:
   cd devorbit-web
   npm install
3. Start the development server:
   npm run dev

Vite will start the frontend on port 5173.

## Environment Variables

### Backend Environment
- SERVER_PORT: Server port (default: 8080)
- DATABASE_URL: JDBC Connection URL to PostgreSQL
- DATABASE_USERNAME: Username for the database
- DATABASE_PASSWORD: Password for the database
- JWT_SECRET: Base64 or plain text secret key for JWT signatures (minimum 256-bit)
- OPENCODE_API_KEY: API key for the OpenCode AI model
- FIREWORKS_API_KEY: API key for Fireworks AI embedding generator
- EXA_API_KEY: API key for Exa search integrations

### Frontend Environment
- WEB_PORT: Port for the Vite dev server (default: 5173)
- VITE_PROXY_TARGET: Backend API target address (default: http://localhost:8080)
- VITE_API_BASE_URL: API base path (default: /api)

## Commands Reference

### Backend Commands
- Test: ./mvnw test
- Compile: ./mvnw compile -B
- Clean: ./mvnw clean

### Frontend Commands
- Start dev server: npm run dev
- Build: npm run build
- Run tests: npm test
- Type Check: npx tsc --noEmit

## Documentation Directory

The docs directory contains detail guides:
- Architecture: docs/architecture.md
- Database Schema: docs/database.md
- REST API Reference: docs/api.md
- Setup Guide: docs/setup.md
- Deployment Checklist: docs/deployment.md
- Troubleshooting: docs/troubleshooting.md

## Contributing

Please refer to CONTRIBUTING.md for details on our coding standards, branch naming rules, and Git commit formatting guidelines.

## Security

To report security issues, please refer to our SECURITY.md file. Do not open public issues for vulnerability disclosures.

## License

This project is currently unlicensed. Refer to the LICENSE file for details.

## Project Status

DevOrbit is in active development. Features such as course lists, repository votes, chat communities, and the 3D knowledge graph are operational. Mobile client integration is underway. Database schema operations are currently applied on startup by the backend.
