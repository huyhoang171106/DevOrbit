# Execplan

1. Add a failing Mốc 3 contract test for REST routes, STOMP message mapping, channel sync, message history, review upsert/delete, vote update/cancel, and aggregate summaries.
2. Add community DTOs.
3. Add repository query methods for channel listing and social aggregates.
4. Add `CommunityChatService` and `SocialService`.
5. Add student/public controllers.
6. Run focused and full backend tests.

## Impact Analysis

CodeGraph pi tools and documented `.agents/skills/codegraph-*` files were not exposed in this session. Local blast-radius inspection:

- New controllers: `StudentCommunityController`, `StudentSocialController`, `PublicSocialController`.
- New services: `CommunityChatService`, `SocialService`.
- New DTO package: `dto.community`.
- Repository method additions for existing Mốc 1 repositories.
- No existing public course/repo controller methods modified.

Risk remains HIGH because this adds authenticated student write APIs and public response contracts.
