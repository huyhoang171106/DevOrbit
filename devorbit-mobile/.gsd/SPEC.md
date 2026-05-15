# SPEC.md — Project Specification (Mobile Rebuild)

> **Status**: `FINALIZED` (2026-05-12, updated 2026-05-16)

## Vision
DevOrbit Mobile is an "Academic OS" for UIT students, providing a centralized hub for course materials, progress tracking, academic planning, and AI-driven intelligence to optimize their university journey.

> **Implementation Progress**: Course Hub (search, list, detail), Knowledge Graph (search, filter, simulation, galaxy canvas), Dashboard (widget layout), Room offline caching, domain engines (9 engines), and authentication are built. PTB, Smart Todo, and AI UI tabs have their engines ready but lack dedicated tab UI. See `ROADMAP.md` for detailed status.

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
- [x] Students can find any UIT course repo in < 3 clicks. (Course Hub search + semester filter + list)
- [ ] PTB accurately calculates credits and prerequisite status. (Engines built: GpaEngine, RiskEngine, SimulationEngine)
- [ ] Smart Todo identifies "overload" weeks based on deadline clusters. (Engines built: WorkloadEngine, TaskBreakdownEngine)
- [ ] AI Engine provides a valid career roadmap based on course history. (Engines built: RecommendationEngine, AiRepositoryImpl)
