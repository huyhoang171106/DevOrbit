# US-021 SE 2025 Curriculum Integration

## Status

completed

## Evidence

- Generated `data_updated.sql` using `scratch/parse_se2025.py` from official UIT markdown sources.
- Updated `devorbit-api/src/main/resources/data.sql` with 100% of the SE 2025 roadmap.
- Verified 548 relationships (Prerequisites and Corequisites) mapped into `course_relationships` table.
- Verified backend build success with `mvn clean compile`.
- Updated `docs/KNOWLEDGE_GRAPH.md` to reflect the 2.0 blueprint state.

## Lane

normal

## Product Contract

The system must support the Software Engineering 2025 curriculum (Class 2025). This includes:
- Updating the course database with new course codes and credits.
- Updating the Knowledge Graph prerequisite network to match the 2025 spec.
- Ensuring the "Simulation Mode" correctly models the new prerequisite structure.

## Relevant Product Docs

- `docs/product/curriculum-se-2025.md`

## Acceptance Criteria

- All compulsory courses from the 2025 spec are present in the Knowledge Graph.
- Prerequisites (e.g., IT001 -> IT002 -> SE104) are correctly mapped as per the 2025 roadmap.
- The Knowledge Graph visualizes the correct semester layout (S1-S8).
- Database migrations/updates for `course` and `course_prerequisite` tables are verified.

## Design Notes

- **Data Source**: Parse the UIT 2025 curriculum spec into `data.sql`.
- **Logic**: The `computeBlocked` logic in `GalaxyPage.tsx` relies on the `PREREQUISITE` link type. We must ensure these links are correctly populated from the new backend data.
- **UI**: Update the Knowledge Graph level assignment (semester-based) to match the 2025 roadmap.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | Verify prerequisite graph traversal logic with new data. |
| Integration | API `/api/v1/knowledge-graph` returns the 2025 curriculum nodes/links. |
| E2E | Knowledge Graph renders correctly with 130 credits worth of courses. |

## Evidence
(None yet)
