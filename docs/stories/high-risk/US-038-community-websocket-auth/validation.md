# Validation

## Required

- `devorbit-api`: focused Maven test for WebSocket configuration and STOMP auth.
- `devorbit-api`: full Maven test suite.

## Evidence

- RED: `WebSocketConfigContractTest` failed at `testCompile` before implementation because Spring messaging/WebSocket classes and `WebSocketConfig` were missing.
- GREEN: `devorbit-api`: cached Maven `mvn.cmd -Dtest=WebSocketConfigContractTest test` passed on 2026-06-07 with 4 tests, 0 failures.
- SUITE: `devorbit-api`: cached Maven `mvn.cmd test` passed on 2026-06-07 with 33 tests, 0 failures.
