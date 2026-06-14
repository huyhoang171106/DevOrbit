# US-041 Community Channel Presence

## Status

planned

## Lane

normal

## Product Contract

Students who are logged in and viewing a community channel can see other students currently online in that same channel.

Presence is realtime and ephemeral. It is derived from WebSocket channel subscriptions and is not stored in the database.

## Relevant Product Docs

- `docs/superpowers/specs/2026-06-14-community-channel-presence-design.md`
- `docs/stories/high-risk/US-038-community-websocket-auth/overview.md`
- `docs/stories/high-risk/US-039-community-api-message-endpoints/overview.md`

## Acceptance Criteria

- The right community sidebar lists online members for the selected channel.
- Changing channels updates the list to that channel's online members.
- Disconnecting, unsubscribing, or closing the tab removes that session from the channel presence list.
- Presence updates do not require database writes.
- Existing community chat send/receive behavior remains unchanged.

## Design Notes

- Commands: STOMP subscribe/unsubscribe/disconnect lifecycle events.
- Queries: student lookup by student code for display metadata.
- API: no new REST route; broadcast uses `/topic/channel/{channelId}/presence`.
- Tables: none.
- Domain rules: online state is per channel and per active WebSocket session.
- UI surfaces: `devorbit-web/src/pages/student/CommunityPage.tsx` right sidebar.

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | Backend presence service tests; frontend render/socket parsing tests where practical |
| Integration | Existing community WebSocket/message contract tests remain green |
| E2E | Not planned for this slice |
| Platform | Web TypeScript and Vitest |
| Release | Not planned |

## Harness Delta

No structural harness changes expected.

## Evidence

Pending implementation.
