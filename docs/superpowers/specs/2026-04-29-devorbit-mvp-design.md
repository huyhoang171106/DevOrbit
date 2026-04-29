# DevOrbit MVP Design

## Overview

DevOrbit is a knowledge-management and learning-support platform for UIT students, positioned as a curated bridge between the official curriculum and real-world GitHub project references. The first production-oriented MVP focuses on the `Legacy Repo Finder` experience instead of trying to deliver the full long-term product surface.

The MVP includes:

- A public student-facing web app.
- A public student-facing Android app in Kotlin.
- A Java Spring Boot backend extending the existing `devorbit-api` project.
- An authenticated admin web experience for scanning GitHub repositories, reviewing candidates, and publishing approved repos.

The MVP excludes roadmap authoring, knowledge graph visualization, split-view note taking, student login, and community voting. Those remain valid phase-2 directions, but they should not shape MVP architecture more than necessary.

## Product Goal

The MVP should let a UIT student quickly answer this question:

"For a given subject, what GitHub repos are worth studying, and what tech stack do they use?"

The MVP should let an admin answer this question:

"How do I scan GitHub for subject-related repos, remove noise, and publish a trusted subject-specific catalog?"

## Confirmed Scope

### Student features

- Browse a curated list of UIT subjects.
- Open subject detail pages.
- View approved GitHub repos linked to a subject.
- Filter repos by normalized tech stack tags.
- Inspect repo metadata such as description, stars, language, owner, and last updated time.
- Open the original GitHub repo.

### Admin features

- Log in with admin credentials.
- Create, edit, disable, and review subjects.
- Trigger GitHub scans for a selected subject with a manually entered query.
- Review scanned candidate repos.
- Approve or reject candidates.
- Edit repo-facing metadata and tech stack tags before approval.

### Explicit non-goals for MVP

- Learning roadmap editor.
- Knowledge graph.
- Split-view notes.
- Student account system.
- Social rating or voting.
- Scheduled background scanning.
- Full CMS or role hierarchy.

## Existing Backend Context

The existing backend already provides a useful starting point:

- Stack: Spring Boot `4.0.6`, Java `21`, Maven, Spring Data JPA, Spring Web MVC, PostgreSQL, Lombok.
- Existing entities:
  - `Course` with fields centered around `maMH`, `tenMH`, credits, lecture, practice, and subject type.
  - `GithubRepo` with fields `repoName`, `description`, `githubUrl`, `techStack`, `subjectId`, and `stars`.
- Existing configuration uses `application.yaml` with a local PostgreSQL connection and a plaintext password, which is acceptable for local development only and must be moved to environment-driven config before deployment.
- Existing API surface is read-only and too narrow for the approved MVP workflow.

The design below keeps the current project as the backend foundation instead of replacing it.

## Recommended Solution

The recommended MVP architecture is a curated, admin-mediated ingestion pipeline:

1. Admin selects a subject.
2. Admin enters a GitHub search query.
3. Backend calls the GitHub Search API using a server-side token.
4. Backend stores normalized results as `repo_candidates`.
5. Admin reviews candidates, edits presentation metadata, assigns tech stack tags, and approves or rejects each candidate.
6. Approved candidates are promoted into the public catalog.
7. Student web and mobile clients read only approved public data.

This approach is preferred over direct live GitHub search for students because it avoids noisy results, quota issues, and inconsistent quality.

## Alternative Approaches Considered

### Approach A: Curated admin approval pipeline

This is the recommended approach.

- Strong control over quality.
- Stable public experience.
- Fits the "trusted legacy repo finder" idea.
- Requires more admin workflow than pure live search, but still stays within MVP scope.

### Approach B: Live search for all users

- Fastest prototype path.
- High noise, weak quality control, and fragile rate-limit behavior.
- Not aligned with the goal of trustworthy academic references.

### Approach C: Curated approval plus scan session history

- Stronger auditability and future extensibility.
- More schema and UI complexity than needed for the first release.
- Better as a follow-up iteration after the core review loop works.

## System Architecture

DevOrbit MVP has four major parts:

- `devorbit-api`: Spring Boot backend.
- `devorbit-web`: React + Vite web application for both student and admin experiences.
- `devorbit-mobile`: Kotlin Android application for the student-facing mobile experience.
- `PostgreSQL`: persistent relational storage.

### High-level data flow

1. Student web and mobile clients call public REST endpoints.
2. Admin web calls protected admin REST endpoints with JWT authentication.
3. Backend calls GitHub APIs server-side using `GITHUB_TOKEN` from environment configuration.
4. Backend persists scan results and approval states in PostgreSQL.
5. Public clients read only approved and active repos.

### Deployment model

The intended deployment shape is:

- Backend on a managed Java-friendly platform such as Render, Railway, or Fly.io.
- PostgreSQL as a managed database service.
- Web app on Vercel or Netlify.
- Android app configured to call the deployed public API base URL.

The system should be environment-driven from the start, even if local development stays simple.

## Frontend Stack Decisions

### Web

The recommended web stack is:

- React
- Vite
- Tailwind CSS
- shadcn/ui

Reasoning:

- Fastest implementation velocity for the requested MVP.
- Strong fit for filterable lists, admin tables, dialogs, and forms.
- Easy to maintain a shared visual language across student and admin sections.

### Mobile

The mobile app should be a Kotlin Android app with scope equal to the student-facing web MVP, excluding admin features.

The mobile app should support:

- Subject browsing.
- Subject detail.
- Repo list and filtering.
- Repo detail.
- Opening GitHub in the external browser.

## Backend API Design

The backend should expose one REST API with clear namespace boundaries:

- Public endpoints under `/api/...`
- Admin endpoints under `/api/admin/...`

This is preferable to a single mixed namespace because it keeps authorization boundaries explicit while staying simple.

### Public API

The public API should support:

- `GET /api/courses`
- `GET /api/courses/{courseId}`
- `GET /api/courses/{courseId}/repos`
- `GET /api/tech-stacks`

Recommended response behavior:

- `GET /api/courses` returns active subjects only.
- `GET /api/courses/{courseId}/repos` supports filters such as tech stack and sort keys.
- Public endpoints never expose rejected or pending candidate data.

### Admin API

The admin API should support:

- `POST /api/admin/auth/login`
- `GET /api/admin/courses`
- `POST /api/admin/courses`
- `PUT /api/admin/courses/{courseId}`
- `DELETE /api/admin/courses/{courseId}` or a soft-disable equivalent
- `POST /api/admin/github/scan`
- `GET /api/admin/repo-candidates`
- `POST /api/admin/repo-candidates/{candidateId}/approve`
- `POST /api/admin/repo-candidates/{candidateId}/reject`
- `PUT /api/admin/repos/{repoId}`
- `DELETE /api/admin/repos/{repoId}` or soft-disable

Admin endpoints should be enough to manage the full ingestion and publishing workflow without introducing a second admin backend.

## Authentication and Security

### Admin authentication

The MVP should use Spring Security with JWT for admin login.

Recommended flow:

1. Admin submits username and password.
2. Backend validates credentials.
3. Backend returns a signed JWT.
4. Admin web stores the token in the chosen secure client strategy and sends it on subsequent admin requests.

### Security constraints

- Student endpoints remain public.
- Admin endpoints require JWT authentication.
- GitHub token must remain backend-only.
- Database credentials and JWT secrets must move from hardcoded config into environment variables.
- CORS should explicitly allow the deployed web origin and local development origins.

For MVP, a single admin role is sufficient. Role hierarchy can wait.

## Data Model

The existing `Course` and `GithubRepo` entities are not enough for the approved workflow because the system must distinguish between scanned raw candidates and published repos.

### Core tables

- `admin_users`
- `courses`
- `repo_candidates`
- `github_repos`
- `tech_stacks`
- `repo_tech_stacks`
- `candidate_tech_stacks`

### `admin_users`

Purpose: stores admin login accounts.

Recommended fields:

- `id`
- `username`
- `password_hash`
- `display_name`
- `is_active`
- `created_at`
- `updated_at`

### `courses`

Purpose: stores UIT subjects visible in the platform.

The existing `Course` entity should be extended carefully rather than discarded.

Recommended fields:

- `id`
- `code` mapped from current `maMH`
- `name` mapped from current `tenMH`
- `credits` mapped from current `soTC`
- `lecture_hours` mapped from current `lt`
- `practice_hours` mapped from current `th`
- `subject_type` mapped from current `loaiMonHoc`
- `department`
- `description`
- `is_active`
- `created_at`
- `updated_at`

The current Vietnamese field naming in Java is acceptable for continuity, but the API contract should be normalized consistently. If the codebase adopts English DTOs, the database does not need a disruptive rename immediately.

### `repo_candidates`

Purpose: stores raw GitHub scan results awaiting review.

Recommended fields:

- `id`
- `course_id`
- `scan_query`
- `github_owner`
- `github_name`
- `github_url`
- `description`
- `primary_language`
- `topics`
- `stars`
- `forks`
- `last_pushed_at`
- `readme_excerpt`
- `status` with values `NEW`, `APPROVED`, `REJECTED`
- `review_note`
- `created_at`
- `updated_at`

### `github_repos`

Purpose: stores public, approved repos shown to students.

The existing `GithubRepo` entity should be expanded rather than replaced blindly.

Recommended fields:

- `id`
- `course_id`
- `source_candidate_id`
- `github_owner`
- `github_name`
- `github_url`
- `display_name`
- `description`
- `primary_language`
- `topics`
- `stars`
- `forks`
- `last_pushed_at`
- `quality_score`
- `is_active`
- `created_at`
- `updated_at`

### `tech_stacks`

Purpose: stores normalized technology tags used for filtering.

Recommended fields:

- `id`
- `slug`
- `display_name`
- `category`
- `is_active`

Examples include `java`, `spring-boot`, `react`, `csharp`, `kotlin`, and `sql`.

### Join tables

- `repo_tech_stacks`: approved repo to tech stack many-to-many.
- `candidate_tech_stacks`: candidate repo to provisional tech stack many-to-many.

This is preferable to a free-text comma-separated column because student filtering is a core feature.

## Data Integrity Rules

- Public student queries read only from `github_repos` where `is_active = true`.
- Candidate repos use lifecycle states `NEW`, `APPROVED`, `REJECTED`.
- Rejecting a candidate should prevent repeated review churn for the same subject and same repo URL when the same query is scanned again.
- The system should prevent duplicates for the same `course_id + github_url` in the public catalog.
- Candidate approval should create or upsert an approved repo snapshot instead of exposing candidates directly.

For MVP, GitHub metadata refresh can be manual or one-shot during scan time. Scheduled refresh is not required.

## GitHub Scanning Workflow

The admin workflow should work like this:

1. Admin logs in.
2. Admin chooses a course.
3. Admin enters a search query such as `SE101 UIT` or `software engineering UIT project`.
4. Backend calls GitHub Search API using a personal access token stored in environment config.
5. Backend normalizes the results into candidate records.
6. Admin reviews candidate records in the admin UI.
7. Admin approves or rejects candidates.
8. Approved candidates become public repos.

### Candidate review UI expectations

The admin candidate table should show at least:

- Repo name.
- Owner.
- Description.
- Stars.
- Primary language.
- Topics if available.
- Last updated time.
- GitHub link.
- Current candidate status.

The admin should also be able to:

- Filter by course.
- Filter by candidate status.
- Filter by language.
- Search by repo name or owner.
- Edit tech stack tags before approval.

## Student Experience Design

### Web student experience

Recommended screens:

- Landing or home page.
- Course list page.
- Course detail page.
- Repo list section with tech stack filtering.
- Repo detail drawer or detail page.

Core interaction:

1. Student searches or browses for a subject.
2. Student opens the subject.
3. Student filters approved repos by tech stack.
4. Student compares options quickly and opens a chosen GitHub repo.

### Mobile student experience

Recommended screens:

- Course list.
- Course detail.
- Repo list.
- Filter UI, likely a modal or bottom sheet.
- Repo detail.

The mobile information architecture should mirror the web student flow closely so that the shared backend stays simple and the product stays consistent.

## Admin Experience Design

Recommended admin screens:

- Login page.
- Dashboard summary.
- Course management page.
- GitHub scan page.
- Candidate review page.
- Approved repo management page.

The dashboard can stay minimal for MVP. Its main job is navigation and quick operational visibility.

## Error Handling and Operational Rules

### GitHub API failures

If GitHub search fails due to quota, connectivity, or invalid query:

- The backend should return a clear admin-facing error.
- No partial candidate rows should be published.
- Existing approved repo data remains unaffected.

### Invalid or noisy results

If a scan returns many irrelevant repos:

- The system should still persist them as candidates.
- The admin remains the trust boundary.
- Query refinement stays manual in MVP and does not require auto-ranking logic.

### Data deletion

Soft delete or deactivation is preferable for public repos and courses because it is safer operationally and avoids breaking references.

## Testing Strategy

The existing backend currently has only a Spring context test. That is not enough for this MVP.

Recommended testing scope for implementation:

- Controller tests for public course and repo endpoints.
- Controller tests for admin auth, course management, scan, and approve or reject flows.
- Service tests for candidate approval logic.
- Repository tests where query behavior matters.
- Basic frontend tests around filtering and admin review flows if the frontend stack includes a test runner.

The MVP does not require exhaustive end-to-end automation on day one, but approval flow correctness is important enough to deserve dedicated tests.

## Migration and Refactoring Notes

The backend should evolve from its current shape incrementally:

- Keep the existing Spring Boot project.
- Expand entities rather than restarting the backend.
- Introduce DTOs to normalize API contracts and avoid leaking legacy field names directly into clients.
- Move configuration secrets out of `application.yaml` and into environment variables.
- Add new entities for admin users, candidate repos, and tech stack normalization.

This keeps the system aligned with the existing codebase while fixing the biggest structural gaps for the MVP.

## Implementation Order

Recommended implementation order:

1. Clean up backend configuration for environment-based secrets and deployment readiness.
2. Introduce the new database model and entity relationships.
3. Implement public read APIs for courses and approved repos.
4. Implement Spring Security and JWT for admin login.
5. Implement admin course management.
6. Implement GitHub scanning and candidate persistence.
7. Implement candidate approval and rejection workflow.
8. Build the React web application with student and admin routes.
9. Build the Kotlin Android student app against the public API.

This order reduces risk by stabilizing the backend contract before the clients expand.

## Acceptance Criteria For MVP

The MVP is successful when all of the following are true:

- An admin can log in to the web admin area.
- An admin can create or update subjects.
- An admin can run a GitHub scan for a subject using a manual query.
- Scan results appear as candidates.
- An admin can approve or reject candidates.
- Approved repos become visible on the public student web and mobile experiences.
- Students can filter approved repos by tech stack.
- The system runs with environment-based secrets and does not expose GitHub credentials to clients.

## Deferred Phase-2 Ideas

The following remain good follow-up directions once the MVP proves useful:

- Learning Roadmap with account-backed persistence.
- Split-view note taking.
- Knowledge graph of prerequisites and related subjects.
- Scheduled refresh of GitHub metadata.
- Stronger ranking and quality heuristics.
- UIT-specific login or domain-restricted access.
- Community feedback and quality signals.

## Final Recommendation

DevOrbit should launch its first version as a curated GitHub reference platform, not a broad all-in-one academic workspace. The right MVP is the one that makes subject-specific repo discovery trustworthy, fast, and easy to maintain.

That means:

- Public browsing for students.
- Admin-authenticated curation.
- Cache-first GitHub ingestion.
- Shared REST backend for web and mobile.
- Minimal but structured data modeling that separates raw candidates from approved public repos.

This is the smallest design that still behaves like a real product rather than a one-off demo.
