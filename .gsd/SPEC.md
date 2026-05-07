# SPEC.md — Project Specification

> **Status**: `FINALIZED`

## Vision
DevOrbit is a full-stack academic platform for UIT students to explore courses and curated repositories. The current goal is to stabilize the existing codebase, achieve feature parity between Web and Mobile, and establish a solid testing foundation.

## Goals
1. **Mobile Parity**: Implement registration and bookmarking features in the Android app to match the Web experience.
2. **Data Consistency**: Fix tech stack deserialization issues in the Mobile app.
3. **Quality Assurance**: Establish a testing suite for the Web frontend to ensure reliability.
4. **Architecture Quality**: Refactor Mobile app to use standard Android patterns (ViewModel, DI).

## Non-Goals (Out of Scope)
- Real-time collaboration features.
- Advanced social features (comments, ratings) - deferred to Phase 2+.
- Cloud deployment automation (CI/CD) - deferred.

## Users
- **Students**: Primary users who browse courses, repositories, and manage bookmarks.
- **Admins**: Faculty/Staff who manage courses and approve repository candidates.

## Constraints
- **Tech Stack**: Must maintain Spring Boot (API), React (Web), and Kotlin/Compose (Mobile).
- **Environment**: Must remain compatible with Docker Compose local setup.

## Success Criteria
- [ ] Mobile app correctly displays tech stacks for all repositories.
- [ ] Students can register and bookmark repositories on Mobile.
- [ ] Web frontend has at least 50% test coverage for core components.
- [ ] Mobile app uses Hilt for Dependency Injection and ViewModels for state.
