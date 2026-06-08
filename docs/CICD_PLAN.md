# CI/CD Plan

> Generated: 2026-05-31 | Project: DevOrbit | Current State: Partial

---

## Current Pipeline Overview

`
Pull Request  ci.yml (3 parallel jobs)
                api-check: Java compile
                web-check: TypeScript + tests
                showcase-check: Lint + TypeScript

Weekly        security.yml (3 parallel jobs)
                npm-audit-web
                npm-audit-showcase
                api-dependency-check (OWASP)

Continuous    dependabot.yml (4 ecosystems)
                npm (devorbit-web)
                npm (devorbit-showcase)
                pip (scripts/)
                github-actions (/)
`

---

## PR Pipeline (ci.yml)

**Trigger:** Pull requests targeting master branch.

### Job 1: API Compile Check (api-check)

| Step | Command | Purpose |
|------|---------|---------|
| 1 | actions/checkout@v4 | Clone repository |
| 2 | actions/setup-java@v4 (JDK 21, Temurin) | Java runtime |
| 3 | Maven cache | Speed up builds |
| 4 | cd devorbit-api and ./mvnw compile -B | Verify Java compilation |

**Checks:** Code compiles against Spring Boot 4.0.6 dependencies.
**Does NOT run:** Tests, integration tests, or dependency vulnerability scans.

### Job 2: Web Type Check and Tests (web-check)

| Step | Command | Purpose |
|------|---------|---------|
| 1 | actions/checkout@v4 | Clone repository |
| 2 | actions/setup-node@v4 (Node 20) | Node.js runtime |
| 3 | cd devorbit-web and npm ci | Install dependencies |
| 4 | cd devorbit-web and npx tsc --noEmit | TypeScript type checking |
| 5 | cd devorbit-web and npm test | Run Vitest test suite |

**Checks:** Type safety + unit test correctness.
**Does NOT run:** Linting, build, coverage.

### Job 3: Showcase Lint and Type Check (showcase-check)

| Step | Command | Purpose |
|------|---------|---------|
| 1 | actions/checkout@v4 | Clone repository |
| 2 | actions/setup-node@v4 (Node 20) | Node.js runtime |
| 3 | cd devorbit-showcase and npm ci | Install dependencies |
| 4 | cd devorbit-showcase and npm run lint | ESLint check |
| 5 | cd devorbit-showcase and npx tsc --noEmit | TypeScript type checking |

**Checks:** Code style + type safety.
**Does NOT run:** Tests, build.

---

## Security Pipeline (security.yml)

**Trigger:** Weekly cron (Monday 9am UTC) + manual workflow_dispatch.

### Job 1: NPM Audit - Web (npm-audit-web)

| Step | Command | Purpose |
|------|---------|---------|
| 1 | Checkout + Node 20 setup | Environment |
| 2 | cd devorbit-web and npm ci | Install deps |
| 3 | cd devorbit-web and npm audit --audit-level=high | Find high/critical vulns |

**Scope:** devorbit-web/package.json dependency tree.
**Severity threshold:** high and above.

### Job 2: NPM Audit - Showcase (npm-audit-showcase)

| Step | Command | Purpose |
|------|---------|---------|
| 1 | Checkout + Node 20 setup | Environment |
| 2 | cd devorbit-showcase and npm ci | Install deps |
| 3 | cd devorbit-showcase and npm audit --audit-level=high | Find high/critical vulns |

**Scope:** devorbit-showcase/package.json dependency tree.
**Severity threshold:** high and above.

### Job 3: API Dependency Check (api-dependency-check)

| Step | Command | Purpose |
|------|---------|---------|
| 1 | Checkout + JDK 21 setup | Environment |
| 2 | cd devorbit-api and ./mvnw dependency-check:check -B | OWASP scan |
| 3 | Fallback message | Warns if plugin not configured |

**Status:** Non-functional. The dependency-check-maven plugin is not declared in pom.xml. The step runs with continue-on-error: true and prints a warning. OWASP scanning needs the plugin added to pom.xml to work.

---

## Dependabot Configuration

| Ecosystem | Directory | Schedule | Limit | Notes |
|-----------|-----------|----------|-------|-------|
| npm | /devorbit-web | Weekly | 10 PRs | Frontend dependencies |
| npm | /devorbit-showcase | Weekly | 10 PRs | Showcase dependencies |
| pip | /scripts | Weekly | 5 PRs | Python scripts dependencies |
| github-actions | / | Weekly | 5 PRs | GitHub Actions versions |

**Not covered by Dependabot:**
- devorbit-api/pom.xml (Maven) - no Maven ecosystem configured
- devorbit-mobile/gradle/libs.versions.versions.toml (Gradle) - no Gradle ecosystem configured

---

## Pre-commit Hooks

**Status:** Not configured.

Husky is listed as a recommendation but not installed. No .husky/ directory exists.

### Recommended Setup

`
At project root: npm install husky --save-dev; npx husky init

.husky/pre-commit: npx lint-staged

package.json (root) lint-staged config:
  *.{ts,tsx}: npx tsc --noEmit, npx eslint
  *.{java}: cd devorbit-api and ./mvnw compile
`

---

## Future Improvements

### High Priority

| Improvement | Status | Effort | Impact |
|-------------|--------|--------|--------|
| Add OWASP dependency-check-maven to pom.xml | Not started | Low (0.5 day) | Security vulnerability detection for Java deps |
| Install Husky + lint-staged at root | Not started | Low (0.5 day) | Catch issues before commit |
| Add Maven tests to CI (mvnw test) | Not started | Low (0.5 day) | Verify API correctness, not just compilation |
| Add build verification to CI (mvnw package) | Not started | Low (0.5 day) | Ensure deployable artifact |

### Medium Priority

| Improvement | Status | Effort | Impact |
|-------------|--------|--------|--------|
| Add Vitest coverage reporting | Not started | Medium (1 day) | Track test coverage over time |
| Add JaCoCo coverage reporting (API) | Not started | Medium (1 day) | Track Java test coverage |
| Add bundle size check (web) | Not started | Medium (1 day) | Prevent bundle bloat |
| Add mobile Gradle build to CI | Not started | Medium (1 day) | Verify Kotlin compilation |
| Add Dependabot for Maven (devorbit-api) | Not started | Low (10 min) | Auto-update Java deps |
| Add Dependabot for Gradle (devorbit-mobile) | Not started | Low (10 min) | Auto-update Kotlin deps |

### Low Priority

| Improvement | Status | Effort | Impact |
|-------------|--------|--------|--------|
| Add Docker build verification | Not started | Medium (1 day) | Ensure Dockerfile works |
| Add E2E tests (Playwright) | Not started | High (3-5 days) | Full user flow verification |
| Add mutation testing (PITest/Stryker) | Not started | High (2-3 days) | Test quality assurance |
| Add performance regression checks | Not started | High (2-3 days) | Prevent perf degradation |
| Deploy preview environment on PR | Not started | High (3-5 days) | Visual review of changes |

---

## Pipeline Architecture

### Current

`
PR opened/updated
  ci.yml
    api-check (parallel)
    web-check (parallel)
    showcase-check (parallel)
  Dependabot PRs (weekly)
  Security scans (weekly)

Merge to master
  (no deployment pipeline yet)
`

### Target State

`
PR opened/updated
  ci.yml
    api-compile (parallel)
    api-test (parallel)
    web-typecheck + test + coverage (parallel)
    showcase-lint + typecheck (parallel)
    mobile-gradle-build (parallel)
  security-scan (on PR for high-risk changes)
  bundle-size-check (parallel)
  preview-deploy (optional)

Merge to master
  build Docker images
  deploy to staging
  run smoke tests
  deploy to production (manual gate)

Weekly
  Dependabot updates
  OWASP dependency scan
  NPM audit
  Coverage report diff
`

---

## Metrics to Track

| Metric | Current | Target | Tool |
|--------|---------|--------|------|
| CI build time | ~3 min | less than 5 min | GitHub Actions |
| Test pass rate | 100% | 100% | CI logs |
| Coverage (web) | Not tracked | greater than 70% | Vitest |
| Coverage (API) | Not tracked | greater than 60% | JaCoCo |
| Vulnerability count | Unknown | 0 critical | OWASP + npm audit |
| Bundle size | Not tracked | less than 500KB gzipped | webpack-bundle-analyzer |