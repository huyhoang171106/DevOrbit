# Pull Request Template

## Description

Provide a summary of the changes introduced by this pull request and the reasoning behind them.

## Type of Change

Select all that apply by putting an 'x' in the brackets:
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Code refactoring (internal improvements, no behavior changes)
- [ ] Documentation update (changes to Markdown files)
- [ ] Testing additions or updates

## Modules Affected

- [ ] devorbit-api (Backend)
- [ ] devorbit-web (Frontend)
- [ ] devorbit-mobile (Mobile)
- [ ] Database Schema / Migrations
- [ ] Documentation (docs)

## Database Schema Impact

Does this PR introduce database schema changes?
- [ ] No
- [ ] Yes (please describe the changes made to tables, indexes, constraints, functions, or RLS policies)

## Security Impact

Does this PR affect security mechanisms?
- [ ] No
- [ ] Yes (please describe details regarding JWT, authentication, authorization, CORS, or storage access policies)

## Verification and Testing

Describe how these changes were verified. Detail the tests that were run and their results.

### Automated Tests Run
Include commands and output references:
- Backend:
- Frontend:
- Mobile:

### Manual Testing / Staging Evidence
Provide details of manual verification steps, API response logs, or screenshots:

## Pull Request Checklist

Ensure all items are checked before submitting the PR:
- [ ] Code builds locally without compilation errors.
- [ ] All new and existing unit tests pass successfully.
- [ ] Linting and type-checking checks pass without errors.
- [ ] Documentation has been updated to reflect behavioral changes.
- [ ] No hardcoded passwords, tokens, API keys, or connection strings are included.
