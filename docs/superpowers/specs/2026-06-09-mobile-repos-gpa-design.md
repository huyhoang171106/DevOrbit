# Mobile Repos And GPA Design

## Goal

Replace the mobile bottom-nav Course and Knowledge experiences with two focused
student tools:

- `Repos`: browse repositories through the course catalogue, with a compact
  course list similar in density to the web course table.
- `GPA`: calculate semester GPA, projected cumulative GPA, and required current
  term GPA locally on the device.

## Scope

In scope:

- Mobile navigation labels and destinations.
- A repo-by-course screen using existing course and repo APIs.
- A local GPA calculator screen and calculation model.
- Unit tests for new screen state/calculation logic.
- Mobile docs and test matrix updates.

Out of scope:

- Backend changes.
- A GPA API endpoint.
- Removing old course/knowledge source files from the repo if unused.
- Web changes.

## Navigation

The mobile bottom navigation will remove:

- `Courses`
- `Knowledge`

It will add:

- `Repos`, using a list icon.
- `GPA`, using a calculator-style icon if available in Material icons, or a
  nearby stable icon from the existing dependency.

The rest of the nav remains unchanged: Dashboard, Explore, Plan, Profile.

## Repos Screen

The Repos screen uses the existing `CourseViewModel` and `AcademicRepository`
where practical:

1. Load courses from `/api/courses`.
2. Display a dense, scan-friendly list of course rows.
3. Each row shows course code, course name, and secondary metadata available on
   mobile such as credits, subject type, or semester.
4. Search and simple subject-type filtering remain available.
5. Tapping a course loads `/api/courses/{courseId}/repos`.
6. The course detail focuses on repositories first and reuses existing
   `RepoDetailScreen` for repository details.

The screen should feel like a mobile version of the web course table rather than
the old learning graph/course-hub experience.

## GPA Screen

The GPA screen calculates locally because there is no GPA endpoint.

Core behavior:

- Maintain editable course rows with name, credits, and grade on the 10-point
  scale.
- Allow adding and removing rows.
- Ignore invalid rows in totals.
- Show semester GPA and total valid credits.
- Accept current cumulative GPA and completed credits.
- Show projected cumulative GPA after the current term.
- Accept target cumulative GPA.
- Show the required current-term GPA needed to reach that target.

The domain logic should be separated from Compose UI so it can be tested without
Android UI instrumentation.

## Data And Error Handling

Repos:

- Course loading continues to use the existing offline cache behavior.
- If remote repo loading fails, use existing cached repo fallback from
  `AcademicRepository.loadCourseDetail`.
- Empty course/repo states should show short, actionable text.

GPA:

- Validation is local.
- Credits must be positive.
- Grades and GPA inputs must be within `0..10`.
- If inputs are incomplete, show neutral empty-state values such as `--`
  rather than crashing.

## Testing

Repos:

- Unit test navigation/state logic that opens course and repo detail.
- Unit test course search/filter logic if the new screen introduces new state.

GPA:

- Unit test weighted semester GPA.
- Unit test invalid rows are ignored.
- Unit test projected cumulative GPA.
- Unit test required current-term GPA for a target.
- Unit test infeasible targets above GPA 10.

Validation commands:

```text
devorbit-mobile: .\gradlew.bat :app:testDebugUnitTest
devorbit-mobile: .\gradlew.bat :app:assembleDebug
```

## Commit Plan

Use small commits:

1. Design spec.
2. Navigation replacement.
3. Repos screen.
4. GPA calculator domain tests and implementation.
5. GPA screen UI/viewmodel.
6. Docs and test matrix updates.
