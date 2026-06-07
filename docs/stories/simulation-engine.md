# US-015 GOD-tier Simulation Engine

## Status

planned

## Lane

normal

## Product Contract

The Knowledge Graph must provide a "Simulation Mode" where students can simulate the failure of a course. The system must propagate this failure to all downstream courses (blocked courses) and visually represent the impact. Nodes must be weighted by an "Impact Score" that reflects their criticality in the academic path.

## Relevant Product Docs

- `docs/product/knowledge-graph.md` (to be created/updated)

## Acceptance Criteria

- [ ] Each course node has an `impactScore` (0.0 - 10.0) based on unlocked courses, depth, and bottleneck factors.
- [ ] Users can enter "Simulation Mode" and click "Fail" on any node.
- [ ] Failed and blocked nodes are visually distinct (e.g., shaking, red, faded).
- [ ] Graduation delay (semesters) is estimated based on the longest blocked path.
- [ ] Entire clusters turn red if failure triggers a high-risk academic scenario (Heatmap).

## Design Notes

- **Backend**: Update `KnowledgeGraphResponse` DTO and `KnowledgeGraphService`.
- **API**: `GET /api/courses/graph` includes `impactScore`.
- **UI**: Add simulation controls and state to `KnowledgeGraphPage.tsx`.
- **Domain rules**: `impact = unlocked_courses * 0.4 + downstream_depth * 0.3 + bottleneck_factor * 0.3`.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `KnowledgeGraphServiceTest` validates score formula. |
| Integration | API returns scores correctly. |
| E2E | Simulation mode correctly identifies all blocked nodes. |

## Harness Delta

- Added US-015 for Simulation Engine.

## Evidence

- (Pending implementation)
