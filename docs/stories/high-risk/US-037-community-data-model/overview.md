# Overview

## Current Behavior

DevOrbit has student authentication, courses, repositories, and tech stacks, but no persistence contract for community chat channels, chat messages, repository reviews, repository votes, or course reviews.

## Target Behavior

The backend defines the Mốc 1 persistence foundation for community features:

- Flyway migration `V006__create_community_features.sql` creates the five community tables.
- JPA entities map the tables and relationships to `StudentUser`, `Course`, and `GithubRepo`.
- Spring Data repositories exist for future service/API work.

## Affected Users

- Student.
- Guest readers indirectly, once later milestones expose public review endpoints.

## Affected Product Docs

- `specs/2026-06-07-community-space-chat/PRODUCT.md`
- `specs/2026-06-07-community-space-chat/TECH.md`

## Non-Goals

- No WebSocket configuration.
- No REST API or message endpoints.
- No frontend chat or review UI.
