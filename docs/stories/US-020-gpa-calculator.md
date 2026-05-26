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
- Students can select a semester preset that fills rows from the DevOrbit course catalogue.
- Semester presets skip zero-credit courses and keep grade fields blank.
- The calculator shows weighted total credits, weighted GPA on the 10-point scale, and academic classification.
- The calculator does not show or convert to the 4-point scale.
- Invalid or zero-credit rows do not break the calculation and show guidance when no valid credits exist.

## Design Notes

- Commands: `npm test -- GpaCalculatorPage`, `npm test -- router`, `npm test -- GpaCalculatorPage router`, `npm run build`.
- Queries: public course catalogue query for semester presets.
- API: `GET /api/courses` for client-side preset loading.
- Tables: none.
- Domain rules: client-side calculator using weighted 10-point average by credits; semester presets include only courses with `semester` matching the selected term and `credits > 0`.
- UI surfaces: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`, route `/gpa-calculator`.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | Vitest render tests for weighted 10-point GPA calculation, row add/remove, validation guidance, no 4-point output, semester preset loading, and route rendering |
| Integration | Not required; preset uses existing public course catalogue endpoint without changing backend contract |
| E2E | Not required for this static route slice |
| Platform | Vite production build |
| Release | Not required |

## Harness Delta

No harness changes required.

## Evidence

- `devorbit-web`: `npm test -- GpaCalculatorPage router` passed, 4 tests.
- `devorbit-web`: `npm run build` passed.
- Updated to Vietnamese-only UI copy, DevOrbit dark visual style, and 10-point GPA only.
- Added GPA calculator entry to the shared student navigation.
- Added API-backed semester presets from the DevOrbit course catalogue, excluding zero-credit courses.
