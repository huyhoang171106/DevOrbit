# Design

## Scope

Implement Mốc 3 from `specs/2026-06-07-community-space-chat/MILESTONES.md`.

## Chat API

- `GET /api/student/community` returns synchronized channels.
- `GET /api/student/community/channels/{channelId}/messages` returns paged message history.
- `/app/chat.send/{channelId}` accepts STOMP messages, persists them, and broadcasts to `/topic/channel/{channelId}`.

Channel sync creates fixed general channels plus course and tech-stack channels from existing data.

## Social API

- Student repo reviews: `POST` upserts and `DELETE` removes `/api/student/repos/{repoId}/review`.
- Student course reviews: `POST` upserts and `DELETE` removes `/api/student/courses/{courseId}/review`.
- Student repo votes: `POST /api/student/repos/{repoId}/vote`; `voteValue` is `1`, `-1`, or `0` to cancel.
- Public repo social info: `GET /api/repos/{repoId}/social-info`.
- Public course reviews: `GET /api/courses/{courseId}/reviews`.

Repository uniqueness constraints from Mốc 1 enforce one review/vote per student target.
