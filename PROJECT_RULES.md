# PROJECT_RULES.md — DevOrbit Development Rules

> **Single Source of Truth** for the DevOrbit project.
>
> Combines GSD methodology with project-specific conventions.

---

## Core Protocol

**SPEC -> PLAN -> EXECUTE -> VERIFY -> COMMIT**

1. **SPEC**: Define requirements in story packets under `docs/stories/`
2. **PLAN**: Decompose into tasks with acceptance criteria
3. **EXECUTE**: Implement with atomic commits per task
4. **VERIFY**: Prove completion with empirical evidence
5. **COMMIT**: One task = one commit, message format: `type(scope): description`

---

## Proof Requirements

Every change requires verification evidence:

| Change Type | Required Proof |
|-------------|----------------|
| API endpoint | curl/HTTP response |
| UI change | Screenshot |
| Build/compile | Command output |
| Test | Test runner output |
| Config | Verification command |

**Never accept**: "It looks correct", "This should work", "I've done similar before".

**Always require**: Captured output, screenshot, or test result.

---

## Project Structure

```text
devorbit/
├── devorbit-api/           # Spring Boot 4 + Java 21 backend
│   ├── src/main/java/...   # 20 controllers, 21 services, 18 repos, 17 entities
│   └── pom.xml
├── devorbit-web/           # React 19 + Vite 6 frontend
│   └── src/
│       ├── components/     # Reusable UI components
│       ├── lib/            # API client, auth, photo compositor
│       ├── pages/          # 22 pages (admin + student)
│       └── types/          # TypeScript definitions
├── devorbit-mobile/        # Kotlin Jetpack Compose Android app
├── devorbit-showcase/      # Next.js showcase site (WIP)
├── docs/                   # All project documentation
│   ├── ARCHITECTURE.md     # System architecture
│   ├── decisions/          # Decision records
│   ├── stories/            # Story packets
│   ├── templates/          # Document templates
│   ├── product/            # Product contracts
│   └── TEST_MATRIX.md      # Behavior-to-proof mapping
├── docker-compose.yml      # 3 services (db, api, web)
└── README.md               # Project overview
```

---

## Commit Conventions

**Format:**
```
type(scope): description
```

**Types:**
| Type | Usage |
|------|-------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `refactor` | Code restructure (no behavior change) |
| `test` | Adding/updating tests |
| `chore` | Maintenance, dependencies |

**Rules:**
- One task = one commit
- Verify before commit
- Scope = module or component (e.g., `feat(photobooth):`, `fix(mobile):`)

---

## Context Management

**Context Quality Thresholds:**

| Usage | Quality |
|-------|---------|
| 0-30% | **PEAK** — Comprehensive, thorough work |
| 30-50% | **GOOD** — Solid, confident output |
| 50-70% | **DEGRADING** — Efficiency mode |
| 70%+ | **POOR** — Rushed, incomplete |

**Context Hygiene Rules:**
- Keep plans under 50% context usage
- State dump + fresh session after significant context consumption
- Search first, read targeted

---

## Token Efficiency Rules

**Goal:** Minimize token consumption while maintaining output quality.

### Loading Rules

| Action | Rule |
|--------|------|
| Before reading file | Search first (grep, ripgrep) |
| File >200 lines | Use outline, not full file |
| File already understood | Reference summary, don't reload |
| >5 files needed | Stop, reconsider approach |

---

## Quick Reference

```text
Before coding     -> Story packet must be clear
Before file read  -> Search first, then targeted read
After each task   -> Commit + update relevant docs
After each wave   -> State snapshot
Before "Done"     -> Empirical proof captured
```

---

*DevOrbit Development Guide*
*Based on GSD Methodology — Model-Agnostic Edition*
