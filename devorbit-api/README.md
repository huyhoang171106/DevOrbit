# DevOrbit API

This is the backend service for the DevOrbit portal, built as a Spring Boot application. It exposes REST endpoints and WebSocket channels, integrates with AI providers (OpenCode AI, Fireworks AI, Exa, Firecrawl), and communicates with a PostgreSQL database.

## System Details

- Language: Java 21
- Framework: Spring Boot 4.0.6
- Security: Spring Security (Stateless JWT authentication)
- Cache: Caffeine
- Build System: Maven

## Package Structure

Source code is located under src/main/java/vn/edu/uit/devorbit_api/ and structured as follows:

- config/ : Configuration beans (AI, security, cache, WebSocket, database initializers).
- constant/ : Static constants and configuration definitions.
- controller/ : REST controllers exposing endpoints prefixed by /api (further split into admin, student, and public categories).
- dto/ : Data Transfer Objects for requests and responses.
- entity/ : JPA Hibernate entities representing database tables.
- event/ : Spring ApplicationEvents (e.g. CommunityPresenceEvent).
- exception/ : Centralized exception handlers (ApiExceptionHandler).
- repository/ : Spring Data JPA interfaces for database operations.
- service/ : Core business logic, split into AI generation, scraping, and entity management.

## Request Execution Flow

Typical REST requests traverse the following layers:
1. Client makes an HTTP call to a REST endpoint.
2. JwtAuthenticationFilter intercepts the request to validate the token (if the route is protected).
3. The Controller handles parameter binding, validation, and delegates to the Service.
4. The Service implements business rules, calls third-party APIs (such as OpenCode AI), and invokes Repository methods.
5. The Repository executes queries against PostgreSQL database using JPA.
6. The database returns results to the Repository, which the Service maps to a DTO and the Controller returns to the client.

## Authentication and Authorization

- Filter: JwtAuthenticationFilter intercepts incoming requests.
- Token Type: Extracted from the Authorization header (Bearer <token>). The token claims include the username and tokenType.
- Roles:
  - If tokenType is "ADMIN", the user receives ROLE_ADMIN.
  - If tokenType is "STUDENT", the user receives ROLE_STUDENT.
- Validation:
  - RevokedTokenStore: The filter extracts the JWT ID (jti) and verifies it has not been revoked.
  - Student Status: For student tokens, the database is queried to ensure the student account is active.
- Password Hashing: PasswordEncoder uses BCrypt.

## Database Initialization and Hardening

On startup, SupabaseDatabaseHardeningInitializer runs the following idempotent operations:
1. Creates the extensions schema and ensures the vector extension is installed there.
2. Creates missing foreign key indexes (e.g., idx_community_messages_channel_id).
3. Adjusts storage policies for photobooth frames.
4. Sets constraints for public student tutor registrations.
5. Denies direct API access (PostgREST bypass) on all 37 backend-owned tables to anon and authenticated roles. This ensures the database posture remains strictly backend-owned.

## AI and RAG Integrations

- OpenCode AI: Calls deepseek-v4-flash for general chat and advisory answers. If the service is unreachable or the API key is missing, it falls back to a local offline advice generator.
- Fireworks AI: Computes 4096-dimension embeddings for RAG document chunks using Qwen 3.
- Exa: Enriches prompts with web search queries.
- Firecrawl: Scrapes web resources for ingestion.
- Knowledge Base: Loads parsed markdown documents from the data directory.

## Development and Build Commands

Commands are run using the Maven wrapper (mvnw):

- Compile project: ./mvnw compile -B
- Run tests: ./mvnw test
- Start API locally: run.bat (loads variables from .env.properties)
- Clean project: ./mvnw clean

## Common Troubleshooting Backend Errors

- Connection Exception: If the database is unreachable, verify DATABASE_URL and check if local PostgreSQL or Supabase is active.
- API Key Missing: If OpenCode AI answers with static text about running offline, check if the OPENCODE_API_KEY environment variable is defined.
- Invalid token / Forbidden: Verify that the frontend is appending the Bearer token to the Authorization header, and that the token has not expired.
