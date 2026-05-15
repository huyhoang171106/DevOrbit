# Changelog

All notable changes to DevOrbit and GSD for Antigravity.

## DevOrbit — Current (HEAD 816a121)

### Latest Changes
- **feat:** Remove auth links from nav, fix merge conflicts, add pagination
- **refactor(photobooth):** Cleanup seed logic, fix schema id type, polish UI
- **fix:** Remove client-side course sort, rely on backend ORDER BY COUNT(r) DESC
- **feat(mobile):** Wire course hub detail navigation
- **feat(mobile):** Add repo tech stack filter
- **feat:** Photobooth frame compositing with document unlock flow
- **feat:** AI Roadmap Generator endpoint
- **feat:** Knowledge Graph blueprint grid with simulation mode
- **feat:** Student auth with OTP verification
- **feat:** Admin course resources (articles, tutorials, YouTube playlists)
- **feat:** Elective course system with group config
- **feat:** Mobile explore screen with search and tech stack filters

### Additional Recent Commits
- **refactor(mobile):** mobile-refactor-v2 — clean architecture overhaul (104 files, +2958/-2760)
  - Wire AuthInterceptor into OkHttpClient, AuthViewModel observes token flow
  - Move 6 DTO files from model/ to data/remote/dto/
  - Move 9 engine files to domain/engine/ (Burnout, Risk, Workload, Gpa, Recommendation, etc.)
  - Move domain models to domain/model/ (AcademicHealth, RiskProfile, etc.)
  - Add AuthScreen, ProfileScreen, ExploreScreen, AuthViewModel, ExploreViewModel, ProfileViewModel
  - Add 9 unit test files covering all domain engines
  - Remove old analytics screens (4), risk screens (4), repository module (6 files)
  - Add AiRepository, AuthRepository, BookmarkRepository, DiscoveryRepository, ResourceRepository
- **feat:** SE2025 curriculum integration — backend data import, relationship mapping, AI roadmap generator
- **feat(mobile):** Local Room database layer, course hub/explore screens, galaxy graph visualization
- **feat(knowledge-graph):** Floating elective selection card with improved group display
- **feat:** Email verification on signup flow
- **fix:** totalCredits now sums from visible columns instead of all nodes in Knowledge Graph
- **docs:** Update directory tree in README (remove stale superpowers/plans refs)
- **clean:** Remove orphaned .md files (ArchLine design, stale plans, review notes)
- **feat(photobooth):** Enhance frame management, per-slot filters, crop adjustment with zoom
- **fix(photobooth):** Correct slots JSON serialization in entity/DTO/service layers
- **fix:** Supabase crash causing blank page + add Photobooth nav link
- **fix(web):** Resolve StudentLoginPage import error and update photobooth route
- **chore:** Initialize DevOrbit Mobile rebuild project
- **fix(gradle):** Gradle build configuration fixes

### Current Codebase Stats
- **Backend:** 21 controllers, 25 services (20 main + 5 AI sub-services), 18 repositories, 22 entities (18 + 4 enums), 43 DTOs
  - KnowledgeGraphService, AI Roadmap Generator, Photobooth API, Auth endpoints
  - Course resources (articles, tutorials, YouTube playlists)
  - Security config with .authenticated() guards, AuthInterceptor support
- **Frontend:** 11 admin pages, 11 student pages (including Galaxy 3D, Knowledge Graph canvas), React 19 + Vite 6
  - Dark orbit design system (emerald + zinc, Baloo 2 + Inter, glassmorphism)
  - Photobooth with frame compositing, per-slot filters, crop adjustment
  - Knowledge Graph blueprint grid with simulation mode, elective selection
  - Pagination, tech stack filter, course hub navigation
- **Mobile:** Kotlin Compose with course hub, repo detail, bookmarks, explore, auth, profile
  - Clean architecture (data/domain/ui layers), 9 domain engines with unit tests
  - Course detail navigation, repo tech stack filter, galaxy graph visualization
  - Cosmic design system (plasma violet + void dark)
- **Infrastructure:** Docker Compose (db, api, web), Nginx, Supabase Storage, PostgreSQL on Docker
- **Scripts:** LLM-based auto-review pipeline (auto-review-candidates.py, describe-repos.py)
- **Knowledge Graph:** 27K nodes, 48K edges, 2027 communities (graphify indexed)

---

## GSD for Antigravity

## [1.5.0] - 2026-04-01

### Breaking Changes
- **Skills moved from `.agent/skills/` to `.agents/skills/`** — aligns with the [Agent Skills open standard](https://agentskills.io/specification), the universal cross-agent discovery path used by Gemini CLI, Claude Code, Cursor, VS Code Copilot, and other compatible agents
- **SKILL.md `name` fields updated** — all 11 skills now use lowercase-hyphenated names matching their folder names per spec (e.g., `GSD Executor` → `executor`)

### Added
- `/sprint` workflow — time-boxed sprints (new/status/close) for quick focused work outside the milestone cycle
- **Test Quality Rules** in `/plan` — prevents agents from gaming test suites with mock-everything, tautological assertions, or always-pass tests
- **Discovery template reference** (Level 1.5) in `/plan` — bridges the gap between quick verification and full research
- **Journal/decisions archival** in `/complete-milestone` — archives DECISIONS.md and JOURNAL.md into milestone folder, resets for next milestone
- **Architecture auto-refresh** in `/complete-milestone` — refreshes ARCHITECTURE.md and STACK.md after milestone completion
- **Requirements tracking** — `/plan` loads REQUIREMENTS.md, `/execute` updates requirement status, `/complete-milestone` archives and marks requirements
- **Session file reset** in `/new-milestone` — resets DECISIONS.md and JOURNAL.md if they've grown beyond a header
- SVG banner in README

### Changed
- README updated with `/sprint` commands section, new file structure, command count (29 total)
- All references across docs, scripts, and workflows updated for `.agents/skills/` path
- `validate-skills.ps1/.sh` updated to scan `.agents/skills/`
- `install.md` and `update.md` workflows handle both `.agent/` (workflows) and `.agents/` (skills)

### Fixed
- Skills not appearing in Antigravity after updates (closes #10)

---

## [1.4.0] - 2026-01-17

### Added
- **Template Parity** — 8 new templates (22 total)
  - `architecture.md`, `decisions.md`, `journal.md`, `stack.md`
  - `phase-summary.md`, `sprint.md`, `todo.md`, `spec.md`
- `validate-templates.ps1/.sh` — template validation scripts
- `validate-all` now includes template validation

---

## [1.3.0] - 2026-01-17

### Added
- **Validation Scripts** — expanded testing infrastructure
  - `validate-skills.ps1/.sh` — verify skill directory structure
  - `validate-all.ps1/.sh` — master script runs all validators
- **VERSION file** — single source of truth for version
- `/help` now displays current version

### Changed
- README.md updated with Testing section

---

## [1.2.0] - 2026-01-17

### Added
- **Cross-Platform Support** — All 16 workflow files now have Bash equivalents
- `/web-search` — Search the web for technical research

### Changed
- README.md updated with dual-syntax Getting Started (PowerShell + Bash)
- README.md added Cross-Platform Support section
- Git commands in workflows use `bash` syntax (cross-platform)

---

## [1.1.0] - 2026-01-17

### Added
- **Template Parity** — 14 templates aligned with original repository
  - `DEBUG.md`, `UAT.md`, `discovery.md`, `requirements.md`, etc.
- **Examples** — `.gsd/examples/` directory
  - `workflow-example.md` — Full workflow walkthrough
  - `quick-reference.md` — Command cheat sheet
  - `cross-platform.md` — Platform-specific guidance
- `/add-todo` — Quick capture workflow
- `/check-todos` — List pending items workflow
- `/whats-new` — Show recent changes

### Changed
- Workflows now have "Related" sections for discoverability
- Cross-linked workflows and skills

---

## [1.0.0] - 2026-01-17

### Added

**Core Workflows (21)**
- `/map` — Analyze codebase, generate ARCHITECTURE.md
- `/plan` — Create PLAN.md with XML task structure
- `/execute` — Wave-based execution with atomic commits
- `/verify` — Must-haves validation with empirical proof
- `/debug` — Systematic debugging with 3-strike rule
- `/new-project` — Deep questioning initialization (10 phases)
- `/new-milestone` — Create milestone with phases
- `/complete-milestone` — Archive and tag milestone
- `/audit-milestone` — Quality review
- `/add-phase` — Add phase to roadmap
- `/insert-phase` — Insert with renumbering
- `/remove-phase` — Remove with safety checks
- `/discuss-phase` — Clarify scope before planning
- `/research-phase` — Technical deep dive
- `/list-phase-assumptions` — Surface assumptions
- `/plan-milestone-gaps` — Gap closure plans
- `/progress` — Show current position
- `/pause` — State preservation
- `/resume` — Context restoration
- `/add-todo` — Quick capture
- `/check-todos` — List todos
- `/help` — Command reference

**Skills (8)**
- `planner` — Task anatomy, goal-backward methodology
- `executor` — Atomic commits, Need-to-Know context
- `verifier` — Must-haves extraction, evidence requirements
- `debugger` — 3-strike rule, systematic diagnosis
- `codebase-mapper` — Structure analysis, debt discovery
- `plan-checker` — Plan validation before execution
- `context-health-monitor` — Prevents context rot
- `empirical-validation` — Requires proof for changes

**Documentation**
- README.md with full methodology explanation
- GSD-STYLE.md comprehensive style guide
- Templates: PLAN.md, VERIFICATION.md, RESEARCH.md, SUMMARY.md
- Examples: workflow-example.md, quick-reference.md, cross-platform.md

**Rules**
- GEMINI.md with 4 core rules enforcement
- Planning Lock, State Persistence, Context Hygiene, Empirical Validation

### Attribution
Adapted from [glittercowboy/get-shit-done](https://github.com/glittercowboy/get-shit-done) for Google Antigravity.
