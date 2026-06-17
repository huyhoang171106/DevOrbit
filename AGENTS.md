# Repository Guidelines

## Project Structure & Module Organization

DevOrbit is a multi-module app. `devorbit-api/` contains the Java 21 Spring Boot backend; source lives in `src/main/java`, configuration in `src/main/resources`, and tests in `src/test/java`. `devorbit-web/` is the React 19 + Vite frontend with pages, hooks, components, types, and shared API helpers under `src/`. `devorbit-mobile/` contains the Kotlin/Gradle Android client. Supabase SQL migrations live in `supabase/migrations/`. Product, architecture, story, and validation notes are under `docs/`.

## Build, Test, and Development Commands

- `rtk .\devorbit-api\mvnw.cmd test -f devorbit-api\pom.xml`: run backend tests.
- `rtk .\devorbit-api\mvnw.cmd compile -B -f devorbit-api\pom.xml`: compile the API without running tests.
- `rtk .\devorbit-api\run.bat`: start the backend with the repository’s `.env` loading path; use this for runtime smoke proof.
- `cd devorbit-web; rtk npm install`: install frontend dependencies.
- `cd devorbit-web; rtk npm run dev`: start Vite locally.
- `cd devorbit-web; rtk npm test -- --run`: run Vitest once.
- `cd devorbit-web; rtk npm run build`: type-check and build the frontend.
- `cd devorbit-mobile; rtk .\gradlew.bat test`: run Android/JVM tests when touching mobile code.

## Coding Style & Naming Conventions

Follow `.editorconfig`: UTF-8, LF endings, spaces, final newline, 2-space indentation by default, and 4 spaces for Java/Kotlin. Java packages use `vn.edu.uit.devorbit_api`; keep classes named by role, such as `CourseService`, `StudentAuthController`, or `CommunityPresenceServiceTest`. React code uses TypeScript/TSX, PascalCase components, camelCase hooks/helpers, and explicit shared API helpers from `devorbit-web/src/lib`.

## Testing Guidelines

Backend tests use Spring Boot, JUnit, H2, and Spring Security test support. Name tests `*Test.java` and place them beside the relevant package under `devorbit-api/src/test/java`. Frontend tests use Vitest and Testing Library; prefer colocated `*.test.ts` or `*.test.tsx`. Run targeted tests first, then the relevant module build before finishing.

## Commit & Pull Request Guidelines

Recent history uses concise conventional-style messages such as `fix(admin) delete community message` and `fix: lifecycle cleanup for course & repo deletion`. Keep commits focused, mention the affected area, and include validation evidence in PR descriptions. PRs should link stories/issues when available, note schema or environment changes, include screenshots for UI work, and never claim runtime success without command or smoke-test evidence.

## Security & Configuration Tips

Do not commit secrets from `.env` files. Supabase posture is backend-owned; migrations belong in `supabase/migrations/`, and API runtime proof should use `devorbit-api/run.bat`. Preserve unrelated local changes in this dirty worktree.
