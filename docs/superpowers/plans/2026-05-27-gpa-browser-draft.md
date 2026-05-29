# GPA Browser Draft Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Persist GPA calculator draft state in browser localStorage so reloads do not lose student input.

**Architecture:** Keep persistence client-only in `GpaCalculatorPage.tsx`. Add small pure draft parsing/validation helpers, initialize React state from the restored draft, autosave state changes with `useEffect`, and expose a clear-draft action in the existing summary panel.

**Tech Stack:** React 19, TypeScript, Vite 6, Vitest, Testing Library, localStorage.

---

## Files

- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`
- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.test.tsx`
- Modify: `docs/product/gpa-calculator.md`
- Modify: `docs/stories/US-020-gpa-calculator.md`
- Modify: `docs/TEST_MATRIX.md`
- Modify: `GitNexus/docs/superpowers/specs/2026-05-27-gpa-browser-draft/DESIGN.md`

## Tasks

- [ ] Add failing tests for restore, autosave, corrupt draft, and clear saved draft.
- [ ] Verify tests fail because persistence UI/logic does not exist.
- [ ] Add draft key, draft type, parse helper, state initialization from draft, autosave effect, and clear-draft handler.
- [ ] Add compact save status and `Xóa bản lưu` action.
- [ ] Verify focused GPA tests pass.
- [ ] Update product docs, US-020, and test matrix.
- [ ] Run `npm test -- GpaCalculatorPage router`, `npm test`, and `npm run build`.

## Self-Review

- Spec coverage: restore, autosave, corrupt draft handling, clear saved draft, docs, and verification are covered.
- Placeholder scan: no placeholders remain.
- Type consistency: draft key and state names match the implementation surface.
