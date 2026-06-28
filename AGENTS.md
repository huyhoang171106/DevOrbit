# Repository Guidelines

## Project Structure & Module Organization

DevOrbit multi-module app. `devorbit-api/` = Java 21 Spring Boot backend (`src/main/java`, `src/main/resources`, `src/test/java`). `devorbit-web/` = React 19 + Vite frontend (pages, hooks, components, types, shared API helpers under `src/`). `devorbit-mobile/` = Kotlin/Gradle Android client. Supabase SQL migrations in `supabase/migrations/`. Product, architecture, story, validation notes under `docs/`.

## Build, Test, and Development Commands

- `rtk .\devorbit-api\mvnw.cmd test -f devorbit-api\pom.xml`: run backend tests.
- `rtk .\devorbit-api\mvnw.cmd compile -B -f devorbit-api\pom.xml`: compile API without tests.
- `rtk .\devorbit-api\run.bat`: start backend with repo's `.env` loading path; runtime smoke proof.
- `cd devorbit-web; rtk npm install`: install frontend deps.
- `cd devorbit-web; rtk npm run dev`: start Vite locally.
- `cd devorbit-web; rtk npm test -- --run`: run Vitest once.
- `cd devorbit-web; rtk npm run build`: type-check + build frontend.
- `cd devorbit-web; rtk npm run preview`: Vite preview for production build.
- `cd devorbit-web; rtk npm run doctor`: run React Doctor health check (package.json script). No dedicated lint npm script — use `npx eslint .` for ESLint.

## devorbit-admin Module (Android)

### Environment Requirements
- **JAVA_HOME** must point to **JDK 21** (e.g. `C:\Program Files\Android\Android Studio\jbr`).
- Kotlin 2.0.21 does NOT support JDK 25 — build fails `java.lang.IllegalArgumentException: 25.0.2`.
- **ANDROID_HOME**: `C:\Users\Hoang\AppData\Local\Android\Sdk` (SDK 35, build-tools 35+).

### Build Commands (from Git Bash)
All builds use `build_temp.bat` (sets `JAVA_HOME` + `ANDROID_HOME`):

```bash
cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat compileDebugKotlin --no-daemon"  # ~13s
cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat testDebugUnitTest --no-daemon"   # ~22s
cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat assembleDebug --no-daemon"       # ~5min
cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat lintDebug --no-daemon"           # ~4min
cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat assembleRelease --no-daemon"     # release build
```

### Gradle Wrapper Location
`gradlew.bat` at `devorbit-admin/gradlew.bat` (NOT repo root). Repo root has no gradle wrapper.

### Known Quirks
- `kapt` warns `language version 2.0+` falling back to 1.9 — harmless.
- `--no-daemon` required in Git Bash to avoid stale daemon interference.

## Coding Style & Naming Conventions

Follow `.editorconfig`: UTF-8, LF endings, spaces, final newline, 2-space indent default, 4 spaces for Java/Kotlin. Java packages use `vn.edu.uit.devorbit_api`; name classes by role (`CourseService`, `StudentAuthController`, `CommunityPresenceServiceTest`). React uses TypeScript/TSX, PascalCase components, camelCase hooks/helpers, explicit shared API helpers from `devorbit-web/src/lib`.

## Testing Guidelines

Backend: Spring Boot, JUnit, H2, Spring Security test support. Name `*Test.java`, place beside relevant package under `devorbit-api/src/test/java`. Frontend: Vitest + Testing Library, colocated `*.test.ts` or `*.test.tsx`. Run targeted tests first, then module build.

## Commit & Pull Request Guidelines

Recent history uses concise conventional-style messages: `fix(admin) delete community message`, `fix: lifecycle cleanup for course & repo deletion`. Keep commits focused, mention affected area, include validation evidence in PR descriptions. PRs link stories/issues when available, note schema or env changes, include screenshots for UI work, never claim runtime success without command or smoke-test evidence.

## Security & Configuration Tips

Don't commit secrets from `.env` files. Supabase posture backend-owned; migrations in `supabase/migrations/`, API runtime proof via `devorbit-api/run.bat`. Preserve unrelated local changes in dirty worktree.

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** Bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State assumptions explicitly. If uncertain, ask.
- Multiple interpretations exist? Present them — don't pick silently.
- Simpler approach exists? Say so. Push back when warranted.
- Something unclear? Stop. Name confusion. Ask.

## 2. Simplicity First

**Minimum code that solves problem. Nothing speculative.**

- No features beyond what asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" not requested.
- No error handling for impossible scenarios.
- 200 lines when 50 would do? Rewrite.

Ask: "Would senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things not broken.
- Match existing style, even if you'd do it differently.
- Notice unrelated dead code? Mention it — don't delete it.

When changes create orphans:
- Remove imports/variables/functions YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

Test: Every changed line traces directly to user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak ("make it work") require constant clarification.

---

**These guidelines working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, clarifying questions come before implementation rather than after mistakes.