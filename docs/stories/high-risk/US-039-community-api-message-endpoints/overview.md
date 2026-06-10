# Overview

## Current Behavior

The backend has community persistence and STOMP connection authentication, but no API or message endpoint surface for channels, chat history, reviews, or votes.

## Target Behavior

Students can list community channels, fetch paginated message history, send STOMP chat messages that persist and broadcast, review courses/repos, and vote on repos. Public readers can fetch course review summaries and repository social info.

## Affected Users

- Student.
- Guest readers for public social summaries.

## Affected Product Docs

- `specs/2026-06-07-community-space-chat/PRODUCT.md`
- `specs/2026-06-07-community-space-chat/TECH.md`

## Non-Goals

- No frontend implementation.
- No admin moderation tools.
- No online-user tracking.
