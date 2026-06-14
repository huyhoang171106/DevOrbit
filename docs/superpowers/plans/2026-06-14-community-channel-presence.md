# Community Channel Presence Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add realtime online-member presence for the active community channel.

**Architecture:** Backend tracks ephemeral channel subscriptions from STOMP lifecycle events and broadcasts `ChannelPresenceResponse` to `/topic/channel/{channelId}/presence`. Frontend subscribes to that presence topic alongside chat messages and renders the member list in the existing right sidebar.

**Tech Stack:** Spring Boot 4 WebSocket/STOMP, Java 21, React 19, TypeScript, Vitest, TanStack Query.

---

## File Structure

- Create `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/community/OnlineMemberResponse.java`: one online member.
- Create `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/community/ChannelPresenceResponse.java`: channel presence payload.
- Create `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CommunityPresenceService.java`: in-memory session/channel tracker.
- Create `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/CommunityPresenceEventListener.java`: listen for STOMP subscribe/unsubscribe/disconnect events and broadcast presence.
- Create `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/service/CommunityPresenceServiceTest.java`: focused backend TDD proof.
- Modify `devorbit-web/src/types/api.ts`: add presence response types.
- Modify `devorbit-web/src/hooks/useCommunitySocket.ts`: subscribe to `/presence` topic and expose connection state.
- Modify `devorbit-web/src/pages/student/CommunityPage.tsx`: render online members.
- Add frontend tests near existing test patterns if the current setup supports this component/hook.
- Update `docs/stories/US-041-community-channel-presence.md` and `docs/TEST_MATRIX.md`.

## Task 1: Backend Presence Tracker

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/community/OnlineMemberResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/community/ChannelPresenceResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CommunityPresenceService.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/service/CommunityPresenceServiceTest.java`

- [ ] **Step 1: Write failing tests**

Test channel subscribe, duplicate session replacement, unsubscribe, and disconnect cleanup.

- [ ] **Step 2: Run focused test and verify RED**

Run: `cd devorbit-api && .\mvnw.cmd -Dtest=CommunityPresenceServiceTest test`

Expected: fail because `CommunityPresenceService` and DTOs do not exist.

- [ ] **Step 3: Implement minimal tracker**

Use synchronized maps keyed by channel id and WebSocket session id. Resolve `StudentUser` by `studentCode` through `StudentUserRepository`.

- [ ] **Step 4: Run focused test and verify GREEN**

Run: `cd devorbit-api && .\mvnw.cmd -Dtest=CommunityPresenceServiceTest test`

- [ ] **Step 5: Commit**

Commit message: `feat(api): track community channel presence`

## Task 2: Backend STOMP Lifecycle Broadcast

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/CommunityPresenceEventListener.java`
- Test: extend `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/config/WebSocketConfigContractTest.java` only if lifecycle behavior can be tested without brittle Spring internals; otherwise rely on service unit proof.

- [ ] **Step 1: Add listener**

Listen to `SessionSubscribeEvent`, `SessionUnsubscribeEvent`, and `SessionDisconnectEvent`. Parse channel ids from `/topic/channel/{id}` subscriptions and publish the current presence to `/topic/channel/{id}/presence`.

- [ ] **Step 2: Run community backend tests**

Run: `cd devorbit-api && .\mvnw.cmd -Dtest=CommunityPresenceServiceTest,WebSocketConfigContractTest,CommunityMilestone3ContractTest test`

- [ ] **Step 3: Commit**

Commit message: `feat(api): broadcast community channel presence`

## Task 3: Frontend Presence Subscription and UI

**Files:**
- Modify: `devorbit-web/src/types/api.ts`
- Modify: `devorbit-web/src/hooks/useCommunitySocket.ts`
- Modify: `devorbit-web/src/pages/student/CommunityPage.tsx`
- Test: add focused Vitest coverage if current project test utilities support the target unit cleanly.

- [ ] **Step 1: Add frontend types**

Add `OnlineMemberResponse` and `ChannelPresenceResponse`.

- [ ] **Step 2: Update socket hook**

Subscribe to `/topic/channel/{channelId}/presence`, parse `ChannelPresenceResponse`, call `onPresence`, and expose `connected` plus `error`.

- [ ] **Step 3: Update Community page**

Keep `onlineMembers` state for the active channel, clear it on channel switch, pass it to `OnlineMembers`, and render member avatars/names.

- [ ] **Step 4: Run web checks**

Run: `cd devorbit-web && npx tsc --noEmit && npm test -- --run`

- [ ] **Step 5: Commit**

Commit message: `feat(web): show online members in community channels`

## Task 4: Documentation and Final Validation

**Files:**
- Modify: `docs/stories/US-041-community-channel-presence.md`
- Modify: `docs/TEST_MATRIX.md`

- [ ] **Step 1: Update evidence**

Record backend and frontend validation commands that were actually run.

- [ ] **Step 2: Run final verification**

Run backend focused tests and web checks again.

- [ ] **Step 3: Commit**

Commit message: `docs: record community presence validation`

## Self-Review

- Spec coverage: channel-scoped online list, no DB persistence, STOMP presence topic, UI rendering, tests, and docs are covered.
- Placeholder scan: no TBD/TODO placeholders.
- Type consistency: backend and frontend use `OnlineMemberResponse` and `ChannelPresenceResponse`.
