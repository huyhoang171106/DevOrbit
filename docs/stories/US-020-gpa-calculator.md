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
- The calculator shows weighted total credits, weighted GPA on the 10-point scale, and academic classification.
- The calculator does not show or convert to the 4-point scale.
- Invalid or zero-credit rows do not break the calculation and show guidance when no valid credits exist.

## Design Notes

- Commands: `npm test -- GpaCalculatorPage`, `npm test -- router`, `npm test -- GpaCalculatorPage router`, `npm run build`.
- Queries: none.
- API: none.
- Tables: none.
- Domain rules: client-only calculator using weighted 10-point average by credits.
- UI surfaces: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`, route `/gpa-calculator`.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | Vitest render tests for weighted 10-point GPA calculation, row add/remove, validation guidance, no 4-point output, and route rendering |
| Integration | Not required; no API integration |
| E2E | Not required for this static route slice |
| Platform | Vite production build |
| Release | Not required |

## Harness Delta

No harness changes required.

## Evidence

- `devorbit-web`: `npm test -- GpaCalculatorPage router` passed, 4 tests.
- `devorbit-web`: `npm run build` passed.
- Updated to Vietnamese-only UI copy, DevOrbit dark visual style, and 10-point GPA only.
