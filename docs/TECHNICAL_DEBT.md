# Technical Debt Report

> Generated: 2026-05-31 | Project: DevOrbit | Status: Active

---

## P0 — Must Fix

| # | Issue | Status | Notes |
|---|-------|--------|-------|
| 1 | 4 failing API tests | ✅ FIXED | Spring WebFlux 7.x compatibility + H2 config resolved |
| 2 | 2 TypeScript errors blocking web tests | ✅ FIXED | AiTutorPage stub + useNavigate fixed |

**All P0 items are resolved.** CI passes for both API and web.

---

## P1 — Important

| # | Issue | Status | Notes | Est. Effort |
|---|-------|--------|-------|-------------|
| 1 | Room allbackToDestructiveMigration() | ⚠️ MITIGATED | Migration infrastructure added (Flyway + V002 migration), but no real migrations exist yet for schema changes. Destructive migration still the fallback for Room in mobile. | Medium (2-3 days) |
| 2 | devorbit-web ESLint not installed | ⚠️ OPEN | .eslintrc.js config exists but 
pm install of eslint + plugins not yet run in CI or locally | Low (0.5 day) |
| 3 | OWASP dependency-check not in pom.xml | ⚠️ OPEN | security.yml workflow runs dependency-check:check but plugin not declared in pom.xml; falls back to error message | Low (0.5 day) |

### Notes on P1 Items

- **Room destructive migration**: The mobile app uses Room's allbackToDestructiveMigration() which drops all user data on schema changes. A Migration infrastructure was scaffolded but no actual versioned migrations have been written. Any schema change in production will wipe local data.
- **ESLint**: The config file exists at devorbit-web/.eslintrc.js but the eslint package and plugins (e.g., @typescript-eslint/eslint-plugin, eslint-plugin-react-hooks) are not listed as devDependencies. Running 
pm run lint will fail.
- **OWASP**: The security.yml GitHub Action calls mvnw dependency-check:check with continue-on-error: true. The plugin org.owasp:dependency-check-maven is not in pom.xml so the step silently fails every run.

---

## P2 — Optional

| # | Issue | Category | Est. Effort | Recommendation |
|---|-------|----------|-------------|----------------|
| 1 | 5 s any casts in devorbit-web | Type Safety | Low (1 hr) | Add global.d.ts for window.__lenis and 
avigator.deviceMemory |
| 2 | ~20 bare console.error statements without context | Observability | Low (2 hrs) | Prefix with [ComponentName] or use a structured logger |
| 3 | Hardcoded IP 192.168.1.193 in showcase 
ext.config.ts | Config Hygiene | Low (10 min) | Use process.env.ALLOWED_DEV_ORIGINS or remove |
| 4 | Template leftover URLs (mohitvirli.github.io) in showcase sitemap.xml and obots.txt | SEO / Branding | Low (15 min) | Replace with actual DevOrbit domain |
| 5 | Duplicate graph loading in mobile (CourseViewModel + KnowledgeViewModel) | Performance | Medium (1 day) | Extract shared graph-fetching logic into a GraphRepository |
| 6 | Circular dependency: KnowledgeGraphService ↔ CourseRelationshipService | Architecture | Medium (2 hrs) | Extract shared interface or invert dependency via @Lazy (currently workaround) |
| 7 | 5 layer violations in API (repos injected into controllers/AI services) | Architecture | Medium (1 day) | Move repository access behind service layer |
| 8 | God class: RoadmapGenerator.java (728 lines) | Maintainability | High (2-3 days) | Extract PhaseBuilder, PrerequisiteResolver, CourseSorter helpers |
| 9 | God components: epoEvaluation.ts (1,071 lines / 61KB), GpaCalculatorPage.tsx (979 lines / 47KB), KanbanBoard.tsx (935 lines / 41KB) | Maintainability | High (3-4 days) | Split into custom hooks + smaller sub-components |
| 10 | No coverage tools configured | Quality Assurance | Medium (1 day) | Add JaCoCo (API) + Vitest coverage (web) + Kotlin test coverage (mobile) |
| 11 | No mutation testing | Quality Assurance | Medium (2 days) | Add PITest (API) + Stryker (web) for mutation score |

---

## Detailed Breakdown

### 1. s any Casts (devorbit-web)

| File | Cast | Purpose |
|------|------|---------|
| src/performance/usePerformanceProfile.ts | (navigator as any).deviceMemory | Device memory API (non-standard) |
| src/components/SmoothScrollProvider.tsx | (window as any).__lenis × 4 | Lenis smooth scroll instance storage |

### 2. Layer Violations (API)

| # | Class | Repository Injected | Should Use |
|---|-------|-------------------|------------|
| 1 | PublicDiscoveryController | GithubRepoRepository | GithubRepoService |
| 2 | PublicDiscoveryController | TechStackRepository | TechStackService |
| 3 | AiService | GithubRepoRepository | GithubRepoService |
| 4 | RoadmapGenerator | CourseRepository | CourseService |
| 5 | SummaryGenerator | CourseRepository | CourseService |

### 3. God Files

| File | Lines | Size | Components | Recommendation |
|------|-------|------|------------|----------------|
| devorbit-api/.../ai/RoadmapGenerator.java | 728 | 39KB | Phase generation, prerequisite resolution, course sorting, AI prompt building | Extract to 4-5 focused classes |
| devorbit-web/src/lib/repoEvaluation.ts | 1,071 | 61KB | Evaluation scoring, X-ray analysis, README parsing, Vietnamese formatting | Split into evaluation/, analysis/, formatting/ modules |
| devorbit-web/src/pages/student/GpaCalculatorPage.tsx | 979 | 47KB | GPA calc, semester presets, goal planner, what-if, UI | Extract GpaEngine, useGoalPlanner, PresetManager |
| devorbit-web/src/components/student/KanbanBoard.tsx | 935 | 41KB | Drag-drop, column management, card rendering, persistence | Extract KanbanColumn, KanbanCard, useKanbanDrag |

### 4. console.error Without Context

Approximately 20 console.error() calls across 20 files in devorbit-web/src/ lack component-level context strings. This makes production debugging difficult when errors are logged without knowing which component triggered them.

**Pattern to fix:**
`	ypescript
// Before
console.error('Failed to load repos');

// After
console.error('[CourseDetailPage] Failed to load repos:', error);
`

---

## Debt Trend

| Period | P0 | P1 | P2 | Total |
|--------|----|----|-----|-------|
| 2026-05-31 (current) | 0 (2 fixed) | 3 | 11 | 14 |

**Trend: Improving.** Two P0 issues were resolved in the current sprint. P1 items require focused attention to prevent data loss (Room) and improve CI security (OWASP). P2 items are long-term quality investments.