# DevOrbit

Root instructions for the whole repo. Keep changes aligned with the package-level `AGENTS.md` files in `devorbit-api/`, `devorbit-web/`, and `devorbit-mobile/`; those override this file for their subtrees.

## Repo layout
- `devorbit-api/`: Spring Boot backend (`mvnw.cmd`, Java 21)
- `devorbit-web/`: React + TypeScript + Vite frontend (`npm`, Vitest)
- `devorbit-mobile/`: Kotlin Android app (Gradle Kotlin DSL)
- `docker-compose.yml`: local stack for `db`, `api`, and `web`

## Commands that matter
- Backend: `cd devorbit-api; .\mvnw.cmd test`
- Web: `cd devorbit-web; npm run build`; `npm test` runs Vitest
- Mobile: `cd devorbit-mobile; .\gradlew.bat test` (Windows wrapper only in repo)
- Full stack local: `docker compose up -d --build`

## Repo-specific gotchas
- Trust executable config over README text when they differ.
- Web API base in Docker is `/api`; don’t hardcode a localhost base in code.
- The backend uses PostgreSQL and JWT env vars from `docker-compose.yml`; don’t reintroduce hard-coded secrets.
- Mobile API models must match backend response shapes; tech stacks are objects, not raw strings, in current API responses.
- If you add tests, place them in the package’s native test location and run the narrowest relevant command first.

## Use the package instructions
- Check the matching subdirectory `AGENTS.md` before editing anything there.
- Keep this root file short; add only repo-wide constraints that an agent would not infer from filenames.
