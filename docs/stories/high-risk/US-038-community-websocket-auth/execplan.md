# Execplan

1. Add a failing contract test for WebSocket/STOMP configuration.
2. Add the WebSocket starter dependency.
3. Implement `WebSocketConfig`.
4. Permit `/ws/community/**` in `SecurityConfig`.
5. Run focused and full backend tests.

## Impact Analysis

CodeGraph pi tools and documented `.agents/skills/codegraph-*` files were not exposed in this session. Local blast-radius inspection:

- `SecurityConfig` changed to permit only `/ws/community/**`.
- New `WebSocketConfig` handles STOMP broker and student JWT `CONNECT` auth.
- No existing API controllers or services modified.

Risk remains HIGH because this changes authentication behavior for a new real-time entrypoint.
