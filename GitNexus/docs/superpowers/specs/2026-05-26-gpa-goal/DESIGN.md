# GPA Goal Planner Design

## Status

Approved for specification review.

## Context

DevOrbit already has a public `/gpa-calculator` page that calculates semester GPA and estimates cumulative GPA on the 10-point scale. Students can manually enter course rows or load a semester preset from the course catalogue. The next improvement is a goal-oriented planning mode: "Toi muon dat GPA X".

## Intake

- Type: Change request.
- Lane: Normal.
- Reason: Extends an existing student-facing calculator with new client-side behavior, touches current UI behavior, and needs stronger unit proof. It does not change auth, backend APIs, database schema, or external providers.
- Product docs affected: `docs/product/gpa-calculator.md`.
- Story affected: `docs/stories/US-020-gpa-calculator.md` or a follow-up story derived from it.
- Test matrix affected: `docs/TEST_MATRIX.md`.

## User Goal

Students should be able to enter a target cumulative GPA and quickly understand:

- What average GPA they need in the current term.
- Whether the target is feasible with a 10-point grading scale.
- About how much each current-term course needs to score.

## Recommended Approach

Add a goal planning section to the existing GPA calculator rather than creating a separate route. The existing page already owns the course rows, semester credits, current GPA, and completed credits inputs. Keeping the goal planner on the same page avoids duplicate data entry and makes the calculator more useful without expanding product scope.

## Behavior

The page adds a third calculation mode labeled "Toi muon dat GPA X".

Inputs:

- Current cumulative GPA, 0 to 10.
- Completed credits, 0 or greater.
- Target cumulative GPA, 0 to 10.
- Current-term course rows already present in the calculator.

The current-term credits come from valid course rows. Invalid course rows are ignored consistently with the current GPA calculation behavior.

Formula:

```text
requiredTermGpa =
  (targetGpa * (completedCredits + termCredits) - currentGpa * completedCredits)
  / termCredits
```

Result states:

- Missing or invalid inputs: show guidance instead of a numeric target.
- `termCredits <= 0`: ask the student to enter valid current-term credits.
- `requiredTermGpa > 10`: mark the target as not feasible for this term.
- `requiredTermGpa < 0`: mark the target as already safely above target and show that no extra GPA pressure is required.
- `0 <= requiredTermGpa <= 10`: show the required current-term GPA.
- `requiredTermGpa >= 8.5`: mark the target as difficult but feasible.

Course-level estimate:

- For valid course rows, show a suggested grade target per course.
- Initial implementation uses the required term GPA as the per-course target for each course, capped to the 0 to 10 range.
- This is intentionally a simple equal-average planner. It does not solve constrained optimization or allow per-course weighting preferences yet.
- The UI copy must clarify that this is an approximate planning target, not an official requirement.

## UI Shape

The feature should fit into the existing GPA calculator page:

- Add a third segmented control option for "Muc tieu GPA".
- Reuse the existing dark DevOrbit visual system, 8px radii, compact inputs, and right-side summary panel style.
- Keep Vietnamese copy. Avoid adding 4-point GPA output.
- Show the main answer prominently: "Ky nay can trung binh X".
- Show feasibility status near the main answer.
- Show course-level targets in a compact list using the current course names and credits.

## Boundaries

In scope:

- Client-side calculation only.
- Existing `/gpa-calculator` route.
- 10-point GPA scale only.
- Manual rows and semester preset rows.
- Vitest coverage for the new domain behavior.
- Product docs, story, and test matrix updates.

Out of scope:

- Saving target GPA to backend or local storage.
- Backend API changes.
- Official UIT transcript integration.
- 4-point scale conversion.
- Per-course custom constraints such as "this course can only reach 8".
- Mobile implementation.

## Validation

Add Vitest coverage for:

- Required term GPA formula.
- Not feasible target when the required term GPA is above 10.
- Already above target when the required term GPA is below 0.
- Missing or invalid input guidance.
- Course-level target rows use current valid course rows.
- Existing semester and cumulative GPA behavior still works.

Run:

```text
cd devorbit-web
npm test -- GpaCalculatorPage
npm test -- router
npm run build
```

## Open Questions

No blocking open questions. The approved UX direction is to include both the required term GPA and approximate per-course targets.
