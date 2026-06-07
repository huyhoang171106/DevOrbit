# Validation

## Required

- `devorbit-api`: focused Maven test for community persistence contract.
- `devorbit-api`: Maven test/package smoke after code edits when feasible.

## Evidence

- RED: `CommunityPersistenceContractTest` failed at `testCompile` before implementation because `ChatChannel`, `CommunityMessage`, review/vote entities, and repositories were missing.
- GREEN: `devorbit-api`: cached Maven `mvn.cmd -Dtest=CommunityPersistenceContractTest test` passed on 2026-06-07 with 3 tests, 0 failures.
- SUITE: `devorbit-api`: cached Maven `mvn.cmd test` passed on 2026-06-07 with 29 tests, 0 failures.
- Wrapper note: `.\mvnw.cmd` currently fails before Maven starts with `Cannot index into a null array`; validation used the already-downloaded wrapper Maven distribution at `%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.5-bin\32db9c34\apache-maven-3.9.5\bin\mvn.cmd`.
