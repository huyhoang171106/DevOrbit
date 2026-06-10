# Design

## Scope

Implement Mốc 2 from `specs/2026-06-07-community-space-chat/MILESTONES.md`.

## WebSocket Shape

- Add `spring-boot-starter-websocket`.
- Register `/ws/community` with SockJS fallback.
- Enable simple broker destinations under `/topic`.
- Use `/app` for application message destinations.

## Auth Shape

HTTP security permits `/ws/community/**` so the browser can perform the WebSocket/SockJS handshake.

Authentication happens at the STOMP layer. A `ChannelInterceptor` inspects `CONNECT`, extracts `Authorization: Bearer <jwt>` from native headers, validates via `JwtService`, requires token type `STUDENT`, and attaches a `UsernamePasswordAuthenticationToken` principal with `ROLE_STUDENT`.
