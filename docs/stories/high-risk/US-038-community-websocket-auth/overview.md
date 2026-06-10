# Overview

## Current Behavior

The backend has community data tables and repositories, but no WebSocket/STOMP entrypoint for real-time community chat.

## Target Behavior

The backend exposes `/ws/community` as a STOMP endpoint with SockJS fallback. HTTP security permits the handshake path, while STOMP `CONNECT` frames must carry a valid student JWT in the native `Authorization` header.

## Affected Users

- Student.

## Affected Product Docs

- `specs/2026-06-07-community-space-chat/PRODUCT.md`
- `specs/2026-06-07-community-space-chat/TECH.md`

## Non-Goals

- No message persistence endpoint.
- No channel list REST API.
- No frontend WebSocket client.
