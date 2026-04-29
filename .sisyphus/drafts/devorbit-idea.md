# Draft: DevOrbit Idea

## Requirements (confirmed)
- DevOrbit is a knowledge-management and learning-support platform for UIT students for the school 20th anniversary.
- Build one web app, one mobile app, and Java backend.
- Web frontend should prioritize fastest development.
- Mobile should use Kotlin.
- Product direction: real product, not just demo/hackathon.
- MVP core: Legacy Repo Finder.
- Web stack decision: React + Vite.
- Core features proposed: Subject Orbit, Legacy Repo Finder, Learning Roadmap, Knowledge Graph, Split-view Notes.

## Technical Decisions
- Backend: Java Spring Boot already exists under `devorbit-api/devorbit-api`.
- Web frontend: React + Vite for fastest delivery with strong ecosystem.
- Mobile: Kotlin Android, architecture TBD.
- MVP should prioritize production viability: auth, persistent data, API limits, privacy/security, maintainable data ingestion.

## Research Findings
- Backend existing stack: Spring Boot 4.0.6, Java 21, Maven, Spring Data JPA, Spring Web MVC, PostgreSQL, Lombok.
- Existing config: `application.yaml` connects to local PostgreSQL `devorbit_db`, username `postgres`, plaintext password currently present.
- Existing entities: `Course` with maMH/tenMH/credits/lecture/practice/type; `GithubRepo` with repoName/description/githubUrl/techStack/subjectId/stars.
- Existing endpoints: `GET /course`, `GET /course/{maMH}`, `GET /repos`, `GET /repos/subject/{subjectId}`.
- Existing tests: only Spring context load test.
- External API guidance: GitHub/YouTube calls must be server-side and cache-first due rate limits/quotas.

## Open Questions
- MVP data strategy: live GitHub/YouTube APIs vs curated/cache-first database.
- Authentication model: UIT email only vs open account vs anonymous first.
- Source of subject catalog: official UIT curriculum import vs manually seeded MVP list.
- Mobile MVP scope relative to web.

## Scope Boundaries
- INCLUDE: Product clarification, MVP definition, technical architecture plan for real product.
- INCLUDE: Legacy Repo Finder as MVP centerpiece.
- INCLUDE: Extend existing Java backend instead of starting backend from scratch.
- EXCLUDE: Implementation in this planning session.
