# SPEC.md — Project Specification (Mobile Rebuild)

> **Status**: `FINALIZED` (2026-05-12)

## Vision
DevOrbit Mobile is an "Academic OS" for UIT students, providing a centralized hub for course materials, progress tracking, academic planning, and AI-driven intelligence to optimize their university journey.

## Goals
1. **Course Repo Hub**: Seamless discovery and classification of UIT course repositories.
2. **Academic Progress Tracker (PTB)**: Comprehensive tracking of course credits, prerequisites, and graduation roadmap.
3. **Smart Todo System**: Academic-focused task management with workload analysis and AI planning.
4. **Academic Intelligence Engine**: AI-powered career roadmaps, semester risk analysis, and smart recommendations.

## Non-Goals
- Complex social features (chat, forums) - stay focused on academic utility.
- Non-UIT curriculum support (v1 focus is UIT only).
- Desktop-first design - this is a mobile-native experience.

## Users
- **UIT Students**: Primary users seeking to manage their academic life efficiently.
- **Academic Mentors**: Potential users (future) who might use the data to help students.

## Constraints
- **Data Source**: Must integrate with `devorbit-api` and UIT curriculum data.
- **Performance**: Must handle large course/repo graphs (Galaxy Graph) smoothly.
- **Tech Stack**: Kotlin, Jetpack Compose, Material 3 (Premium Aesthetics).

## Success Criteria
- [ ] Students can find any UIT course repo in < 3 clicks.
- [ ] PTB accurately calculates credits and prerequisite status.
- [ ] Smart Todo identifies "overload" weeks based on deadline clusters.
- [ ] AI Engine provides a valid career roadmap based on course history.
