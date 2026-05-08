# DevOrbit

Root instructions for the whole repo. Keep changes aligned with the package-level `AGENTS.md` files in `devorbit-api/`, `devorbit-web/`, and `devorbit-mobile/`; those override this file for their subtrees.

## Interaction Protocol

Follow these steps for each interaction with a user:

### 1. User Identification
- Assume you are interacting with `default_user` unless evidence suggests otherwise.
- If you have not identified `default_user`, proactively try to do so.

### 2. Memory Retrieval
- Always begin your chat by saying only "Remembering..." and retrieve all relevant information from your knowledge graph (memory MCP server).
- Always refer to your knowledge graph as your "memory".

### 3. Memory Capture
- While conversing with the user, be attentive to any new information that falls into these categories:
  - **a) Basic Identity** (age, gender, location, job title, education level, etc.)
  - **b) Behaviors** (interests, habits, etc.)
  - **c) Preferences** (communication style, preferred language, etc.)
  - **d) Goals** (goals, targets, aspirations, etc.)
  - **e) Relationships** (personal and professional relationships up to 3 degrees of separation)

### 4. Memory Update
- If any new information was gathered during the interaction, update your memory as follows:
  - a) Create entities for recurring organizations, people, and significant events
  - b) Connect them to the current entities using relations
  - c) Store facts about them as observations

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
- Web API base in Docker is `/api`; don�t hardcode a localhost base in code.
- The backend uses PostgreSQL and JWT env vars from `docker-compose.yml`; don�t reintroduce hard-coded secrets.
- Mobile API models must match backend response shapes; tech stacks are objects, not raw strings, in current API responses.
- If you add tests, place them in the package�s native test location and run the narrowest relevant command first.

## Use the package instructions
- Check the matching subdirectory `AGENTS.md` before editing anything there.
- Keep this root file short; add only repo-wide constraints that an agent would not infer from filenames.

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **DevOrbit** (1618 symbols, 3046 relationships, 124 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/DevOrbit/context` | Codebase overview, check index freshness |
| `gitnexus://repo/DevOrbit/clusters` | All functional areas |
| `gitnexus://repo/DevOrbit/processes` | All execution flows |
| `gitnexus://repo/DevOrbit/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->
