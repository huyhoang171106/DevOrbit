# Test Matrix

This file maps product behavior to proof.

No product behavior has been defined or implemented yet. Do not mark a row
implemented until tests or validation evidence exist.

## Status Values

| Status | Meaning |
| --- | --- |
| planned | Accepted as intended behavior, not implemented |
| in_progress | Actively being built |
| implemented | Implemented and proof exists |
| changed | Contract changed after earlier implementation |
| retired | No longer part of the product contract |

## Matrix

| Story | Contract | Unit | Integration | E2E | Platform | Status | Evidence |
| --- | --- | --- | --- | --- | --- | --- | --- |
| US-015 | Simulation Engine | pass | pass | no | no | implemented | [Plan 1.3 Summary](file:///C:/Users/Hoang/.gemini/antigravity/brain/31790508-f06b-45f3-a162-ec4b3b74509b/artifacts/plan_1_3_summary.md) |
| US-016 | Photobooth Upload | - | - | - | Browser | planned | - |
| US-017 | AI Roadmap Generator | - | pass | pass | no | implemented | [US-017-PLAN-1.1](file:///d:/temp/devorbit/.gsd/plans/US-017-PLAN-1.1.md) |
| US-018 | Mobile Repo Tech Stack Filter | pass | no | no | Gradle unit test | implemented | `devorbit-mobile`: `.\gradlew.bat :app:testDebugUnitTest` |

## Evidence Rules

- Unit proof covers pure domain and application rules.
- Integration proof covers backend enforcement, data integrity, provider
  behavior, jobs, or service contracts.
- E2E proof covers user-visible browser flows.
- Platform proof covers only shell, deployment, mobile, desktop, or runtime
  behavior that cannot be proven in lower layers.
- A story can be implemented without every proof column if the story packet
  explains why.
