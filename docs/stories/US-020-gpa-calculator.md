# US-020 GPA Calculator

## Status

implemented

## Lane

normal

## Product Contract

DevOrbit provides a public student-facing GPA calculator that estimates 10-point GPA from course credits and grades. The calculator uses Vietnamese UI copy and matches DevOrbit's existing visual style.

## Relevant Product Docs

- `docs/product/gpa-calculator.md`

## Acceptance Criteria

- A new `/gpa-calculator` route renders independently from existing student pages.
- Students can enter course name, credits, and grade on the 10-point scale.
- Students can add and remove course rows.
- Students can choose between semester GPA calculation and cumulative GPA estimation.
- Students can choose a GPA goal planning mode.
- Semester GPA mode only uses the entered course rows for the current term.
- Cumulative GPA mode asks for current GPA and completed credits, then projects the new cumulative GPA after adding the current term.
- GPA goal mode asks for current GPA, completed credits, and target GPA.
- GPA goal mode calculates the current-term average needed to reach the target cumulative GPA.
- GPA goal mode marks targets above the 10-point scale as not feasible for the current term.
- GPA goal mode shows approximate per-course target grades from the current valid course rows.
- Students can select a semester preset that fills rows from the DevOrbit course catalogue.
- Semester presets skip zero-credit courses and keep grade fields blank.
- Semester presets respect saved learning roadmap assignments from `devorbit_kanban_semester_map` before falling back to catalogue semesters.
- The calculator shows current-term credits, weighted semester GPA on the 10-point scale, academic classification, and projected cumulative GPA when cumulative mode is active.
- The calculator does not show or convert to the 4-point scale.
- Invalid or zero-credit rows do not break the calculation and show guidance when no valid credits exist.

## Design Notes

- Commands: `npm test -- GpaCalculatorPage`, `npm test -- router`, `npm test -- GpaCalculatorPage router`, `npm run build`.
- Queries: public course catalogue query for semester presets.
- API: `GET /api/courses` for client-side preset loading.
- Tables: none.
- Domain rules: client-side calculator using weighted 10-point average by credits; cumulative estimate uses `(current GPA * completed credits + semester GPA * semester credits) / (completed credits + semester credits)`; GPA goal planning uses `(target GPA * (completed credits + semester credits) - current GPA * completed credits) / semester credits`; per-course targets initially use the same required term GPA capped to the 0-10 scale; semester presets include only courses with the effective semester matching the selected term and `credits > 0`. Effective semester comes from the saved learning roadmap map when present, otherwise from the course catalogue.
- UI surfaces: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`, route `/gpa-calculator`.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | Vitest render tests for weighted 10-point GPA calculation, row add/remove, validation guidance, no 4-point output, semester preset loading, saved learning roadmap assignment precedence, cumulative GPA estimate, goal GPA reverse calculation, infeasible goals, already-above-target goals, per-course target rows, and route rendering |
| Integration | Not required; preset uses existing public course catalogue endpoint without changing backend contract |
| E2E | Not required for this static route slice |
| Platform | Vite production build |
| Release | Not required |

## Harness Delta

No harness changes required.

## Evidence

- `devorbit-web`: `npm test -- GpaCalculatorPage` passed, 6 tests.
- `devorbit-web`: `npm test -- GpaCalculatorPage router` passed.
- `devorbit-web`: `npm run build` passed.
- Updated to Vietnamese-only UI copy, DevOrbit dark visual style, and 10-point GPA only.
- Added GPA calculator entry to the shared student navigation.
- Added API-backed semester presets from the DevOrbit course catalogue, excluding zero-credit courses.
- Fixed semester presets to respect saved learning roadmap assignments instead of only catalogue semesters.
- Added separate semester GPA and cumulative GPA estimate modes.
- Added GPA goal planner mode with reverse GPA calculation, feasibility states, and per-course target estimates.
