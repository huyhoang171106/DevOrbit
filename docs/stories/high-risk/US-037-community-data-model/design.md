# Design

## Scope

Implement only Mốc 1 from `specs/2026-06-07-community-space-chat/MILESTONES.md`.

## Data Model

The migration adds:

- `chat_channels` for general, course, and tech stack channels.
- `community_messages` for stored channel messages.
- `repo_reviews` for one student review per repository.
- `repo_votes` for one student vote per repository.
- `course_reviews` for one student review per course.

The JPA model keeps ownership simple: community records own their rows and use `@ManyToOne(fetch = FetchType.LAZY)` toward existing aggregate roots.

## Risk Controls

The change is additive. Existing entities, services, controllers, and route contracts are not modified in this milestone.

Uniqueness constraints enforce the product invariant that one student can create only one review per course/repository and one vote per repository.
