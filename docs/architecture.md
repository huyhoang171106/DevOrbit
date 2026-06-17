# System Architecture

This document describes the high-level architecture, module relationships, and request execution flows of the DevOrbit system.

## System Overview

DevOrbit is built as a three-tier system:
1. Client Tier: Represents user interfaces (devorbit-web, devorbit-mobile) that interact with the application.
2. Logic Tier: Represents the Spring Boot backend (devorbit-api) handling business logic, caching, authentication, and external integrations.
3. Data Tier: Represents the PostgreSQL database hosted on Supabase, along with storage buckets for static frames and uploads.

Here is a block diagram of the system components and their interactions:

```mermaid
graph TD
    subgraph Client Tier
        Web[React Web Client]
        Mobile[Kotlin Android Client]
    end

    subgraph Logic Tier
        Spring[Spring Boot API]
        Filter[JWT Auth Filter]
        WebSocket[STOMP WebSocket]
        Cache[Caffeine Cache]
        AI[AI and RAG Service]
    end

    subgraph Data Tier
        DB[(Supabase PostgreSQL)]
        Storage[Supabase Storage]
    end

    subgraph External Services
        OpenCode[OpenCode Go API]
        Fireworks[Fireworks AI Embeddings]
        Exa[Exa Search API]
        GitHub[GitHub API]
    end

    Web -->|HTTP / REST| Filter
    Mobile -->|HTTP / REST| Filter
    Web -->|WS / STOMP| WebSocket
    Filter --> Spring
    WebSocket --> Spring
    Spring --> Cache
    Spring --> DB
    Spring --> Storage
    Spring --> AI
    AI --> OpenCode
    AI --> Fireworks
    AI --> Exa
    Spring --> GitHub
```

## Module Dependencies

The project is structured into three main modules:
- devorbit-api : Built with Maven, depends on Java 21, Spring Boot, Spring Security, Hibernate JPA, PostgreSQL, Caffeine caching, and WebFlux.
- devorbit-web : Built with npm/Vite, depends on Node.js 20, React 19, Redux Toolkit, Zustand, TailwindCSS, stompjs, and Three.js.
- devorbit-mobile : Built with Gradle/Kotlin, compiles to an Android application using Jetpack Compose.

There are no direct code dependencies between the modules; communication is established strictly over network protocols (HTTP REST and WebSocket STOMP).

## Request Execution Flows

### REST Request Flow
The following diagram illustrates how standard API requests are authenticated and processed:

```mermaid
sequenceDiagram
    participant Client as Client Application
    participant Filter as JWT Auth Filter
    participant Controller as REST Controller
    participant Service as Business Service
    participant Repo as JPA Repository
    participant DB as PostgreSQL Database

    Client->>Filter: HTTP Request + Bearer Token
    alt Token Invalid
        Filter-->>Client: 401 Unauthorized
    else Token Valid
        Filter->>Controller: Delegate Request
        Controller->>Service: Process Request
        Service->>Repo: Fetch Data
        Repo->>DB: SQL Query
        DB-->>Repo: SQL Results
        Repo-->>Service: Entity Data
        Service-->>Controller: DTO Response
        Controller-->>Client: HTTP 200 OK + JSON
    end
```

### Community Chat Flow
The community chat uses WebSockets with the STOMP protocol. Authenticated students can send and receive real-time messages within channels:

```mermaid
sequenceDiagram
    participant Student as Student Client
    participant Broker as Spring WebSocket Broker
    participant Service as Community Chat Service
    participant DB as PostgreSQL Database

    Student->>Broker: CONNECT /ws/community (with JWT in headers)
    Note over Broker: Interceptor validates token type is STUDENT
    Broker-->>Student: CONNECTED

    Student->>Broker: SUBSCRIBE /topic/channel/123
    
    Student->>Broker: SEND /app/chat.send/123 (Payload)
    Broker->>Service: Delegate payload
    Service->>DB: Save Chat Message to community_messages
    DB-->>Service: Saved Entity
    Service-->>Broker: Message Response DTO
    Broker->>Student: Broadcast to /topic/channel/123
```

### RAG and AI Query Flow
The AI Tutor uses Retrieval-Augmented Generation (RAG) to answer queries about course materials. The workflow involves looking up vector embeddings of documents and feeding them to the LLM:

```mermaid
sequenceDiagram
    participant Student as Student Client
    participant Controller as AI Controller
    participant Service as Subject QA Service
    participant EmbedService as Fireworks Embedding Service
    participant DB as PostgreSQL Database
    participant LLM as OpenCode AI Service

    Student->>Controller: POST /api/ai/subject-qa/stream (Query)
    Controller->>Service: Process Advisory Query
    Service->>EmbedService: Convert query string to Vector (Qwen 3)
    EmbedService-->>Service: Vector representation (4096 dimensions)
    Service->>DB: Cosine Similarity search on knowledge_chunks
    DB-->>Service: Matching chunks and source references
    Service->>LLM: Generate Answer (System Prompt + Chunks Context + User Query)
    LLM-->>Service: Streamed Answer Tokens (SSE)
    Service-->>Student: SSE stream events (status, search_result, delta, complete)
```

## Architectural Decisions

1. Backend-Owned Database Posture: 
   To protect database integrity and enforce central business logic, direct client access via Supabase's PostgREST API is denied. The backend automatically applies RLS (Row Level Security) rules on startup that deny SELECT, INSERT, UPDATE, and DELETE operations to `anon` and `authenticated` roles for all tables except public SELECT on photobooth frames and limited public INSERT validation on tutor registrations.
2. Stateless Sessions:
   Authentication is completely stateless using JWTs. Session revocations are managed by storing logged-out token identifiers (jti) in Caffeine cache (via RevokedTokenStore).
3. Offline Stubbing Fallback:
   When external AI APIs are down or credentials are not configured, the backend falls back to offline stub responses for course queries. This ensures local development is possible without external keys.
