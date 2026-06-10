# Execplan

1. Add failing backend test covering the community persistence contract.
2. Add migration `V006__create_community_features.sql`.
3. Add community entity classes and repositories.
4. Run focused backend verification.
5. Update milestone and test matrix evidence.

## Impact Analysis

CodeGraph pi tools and documented `.agents/skills/codegraph-*` files were not exposed in this session. Local blast-radius inspection found an additive data-model change only:

- New migration file.
- New entity classes.
- New repository interfaces.
- No existing Java symbols modified.

Risk remains HIGH because the story adds database schema.
