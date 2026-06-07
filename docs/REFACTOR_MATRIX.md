# Refactor Priority Matrix

> Generated: 2026-05-31 | Project: DevOrbit | Priority-ordered action items

---

## Priority Matrix

| Priority | Issue | Impact | Effort | Status | Recommendation |
|----------|-------|--------|--------|--------|----------------|
| P0 | API test failures (4 tests) | CI confidence | Low | FIXED | Spring WebFlux 7.x compatibility + H2 config resolved |
| P0 | TypeScript errors blocking web (2 errors) | Dev velocity | Low | FIXED | AiTutorPage stub + useNavigate fixed |
| P1 | Room fallbackToDestructiveMigration() | User data loss | Medium | MITIGATED | Migration infrastructure added (Flyway + V002); write real Room migrations before next schema change |
| P1 | devorbit-web ESLint not installed | Code quality | Low | OPEN | Run npm install with eslint + plugins; add to CI |
| P1 | OWASP dependency-check not in pom.xml | Security | Low | OPEN | Add dependency-check-maven plugin to devorbit-api/pom.xml |
| P2 | 5 as any casts in devorbit-web | Type safety | Low | OPEN | Create global.d.ts for window.__lenis and navigator.deviceMemory |
| P2 | ~20 bare console.error without context | Observability | Low | OPEN | Prefix all with [ComponentName] for debugging |
| P2 | Hardcoded IP 192.168.1.193 in showcase | Config hygiene | Low | OPEN | Use process.env.ALLOWED_DEV_ORIGINS or remove |
| P2 | Template leftover URLs (mohitvirli.github.io) | Branding | Low | OPEN | Replace sitemap.xml and robots.txt with actual domain |
| P2 | Duplicate graph loading in mobile | Performance | Medium | OPEN | Extract shared GraphRepository between CourseViewModel and KnowledgeViewModel |
| P2 | Circular dependency (KnowledgeGraphService <-> CourseRelationshipService) | Architecture | Medium | OPEN | Extract shared GraphDataProvider interface; currently uses @Lazy workaround |
| P2 | 5 layer violations in API | Architecture | Medium | OPEN | Route PublicDiscoveryController and AI services through service layer |
| P2 | God class: RoadmapGenerator.java (728 lines) | Maintainability | High | OPEN | Extract PhaseBuilder, PrerequisiteResolver, CourseSorter, PromptBuilder |
| P2 | God component: repoEvaluation.ts (1,071 lines / 61KB) | Maintainability | High | OPEN | Split into evaluation/, analysis/, formatting/ modules |
| P2 | God component: GpaCalculatorPage.tsx (979 lines / 47KB) | Maintainability | High | OPEN | Extract useGoalPlanner, usePresetManager hooks; split UI into sub-components |
| P2 | God component: KanbanBoard.tsx (935 lines / 41KB) | Maintainability | High | OPEN | Extract KanbanColumn, KanbanCard, useKanbanDrag hooks |
| P2 | No coverage tools configured | Quality assurance | Medium | OPEN | Add JaCoCo (API), Vitest coverage (web), Kotlin test coverage (mobile) |
| P2 | No mutation testing | Quality assurance | Medium | OPEN | Add PITest (API), Stryker (web) for mutation score |
| P2 | No pre-commit hooks | Code quality | Low | OPEN | Add Husky + lint-staged at project root |

---

## Effort Estimation Summary

| Effort | Count | Items |
|--------|-------|-------|
| Low (less than 0.5 day) | 9 | as any casts, console.error prefixing, hardcoded IP, leftover URLs, ESLint install, OWASP plugin, pre-commit hooks, branch protection, Dependabot config |
| Medium (0.5-2 days) | 6 | Room migrations, duplicate graph loading, circular dependency, layer violations, coverage tools, mutation testing |
| High (2-5 days) | 4 | RoadmapGenerator refactor, repoEvaluation split, GpaCalculatorPage split, KanbanBoard split |

**Total estimated effort:** 15-25 days for all P2 items.

---

## Recommended Execution Order

### Phase 1: Quick Wins (1-2 days)

Items with low effort and immediate impact:

1. Install ESLint in devorbit-web and add to CI
2. Add OWASP dependency-check-maven plugin to pom.xml
3. Install Husky + lint-staged for pre-commit checks
4. Add Dependabot for Maven and Gradle ecosystems
5. Fix 5 as any casts with global.d.ts
6. Add context prefixes to 20 console.error statements
7. Remove hardcoded IP and template leftover URLs

### Phase 2: Architecture Cleanup (3-5 days)

Items that improve structural integrity:

1. Fix 5 layer violations (repos in controllers/AI services)
2. Resolve circular dependency (KnowledgeGraphService <-> CourseRelationshipService)
3. Write Room migrations before next schema change
4. Extract shared GraphRepository for mobile ViewModels

### Phase 3: Code Quality (7-10 days)

Items that improve maintainability:

1. Refactor RoadmapGenerator (728 lines) into 4-5 focused classes
2. Split repoEvaluation.ts (1,071 lines) into modules
3. Split GpaCalculatorPage.tsx (979 lines) into hooks + sub-components
4. Split KanbanBoard.tsx (935 lines) into hooks + sub-components
5. Add JaCoCo + Vitest coverage tools
6. Add PITest + Stryker mutation testing

### Phase 4: Process Maturity (Ongoing)

Items that sustain quality:

1. Enforce coverage thresholds in CI
2. Set up mutation score gates
3. Add performance regression checks
4. Deploy preview environments for PRs

---

## Impact vs Effort Visualization

`
High Impact  |  [P0 Fixed]  [Room Mig]  [Layer Viol]  [God Files]
             |
Med Impact   |  [ESLint]  [OWASP]  [Circular Dep]  [Coverage]
             |
Low Impact   |  [as any]  [console]  [IP/URLs]  [Pre-commit]
             |
             +--------------------------------------------------
               Low Effort              High Effort
`

**Sweet spot:** Top-right items (high impact, low effort) should be done first.
**Strategic investment:** Bottom-right items (high impact, high effort) should be planned for dedicated sprints.

---

## Risk Assessment

| Item | Risk if Deferred | Mitigation |
|------|------------------|------------|
| Room destructive migration | User data loss on next schema change | Write at least one real migration |
| OWASP not working | Undetected Java dependency vulnerabilities | Add plugin to pom.xml |
| No coverage tools | Regression bugs slip through | Add basic coverage reporting |
| God files | Increasing complexity, harder onboarding | Start extraction in Phase 3 |
| No pre-commit hooks | Inconsistent code quality | Install Husky in Phase 1 |

---

## Tracking

Use this matrix to track progress. Update the Status column as items are completed:

| Status | Meaning |
|--------|---------|
| OPEN | Not started |
| IN PROGRESS | Actively being worked on |
| FIXED | Resolved and verified |
| MITIGATED | Risk reduced but not fully resolved |
| DEFERRED | Intentionally postponed |
| N/A | No longer applicable |