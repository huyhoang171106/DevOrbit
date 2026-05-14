# DECISIONS.md

# ADR-001: Mobile Rebuild Approach
**Date**: 2026-05-12
**Status**: Accepted
**Context**: The existing mobile app needed a structural overhaul to support the new "Academic OS" vision.

## Phase 1 Decisions: Academic OS Architecture

**Date:** 2026-05-12

### 1. Navigation & Navigation (OS Foundation)
- **Pattern**: Bottom Navigation Bar with 5 pillars.
  1. **Dashboard** (Mission Control)
  2. **Courses** (Repo Hub)
  3. **Progress** (PTB Tracker)
  4. **Execution** (Smart Todo)
  5. **AI Copilot** (Intelligence Engine)
- **Dashboard**: acts as "Mission Control" containing Daily Briefing, Deadlines, Semester Risk, Credit Progress, AI Recs, and Workload Heatmap.
- **Constraints**: No sidebar; mobile-first focus.

### 2. User Experience (Discovery vs. Productivity)
- **Primary View**: High-density List View with Search-first UX (Linear/GitHub style).
- **Secondary View**: "Explore Galaxy" toggle for dependency and career path visualization.
- **Philosophy**: List for daily productivity; Galaxy for occasional exploration.

### 3. Engineering (Data Flow)
- **Strategy**: Offline-first using Room DB.
- **Pattern**: API → Room → UI (Stale-while-revalidate).
- **Implementation**: Room setup is required immediately in Phase 1.

### 4. Aesthetics (Visual Identity)
- **Style**: "Professional Intelligence System" (Linear + Notion + Raycast).
- **Theme**: Dark-mode-first, subtle galaxy gradients, minimal constellation effects.
- **Avoid**: Neon overload, cyberpunk, or heavy galaxy backgrounds that hinder readability.

**Decision**: Rebuild the mobile app focusing on 4 core modules: Course Hub, PTB, Smart Todo, and AI Engine.
**Consequences**: Existing screens will be refactored or replaced to align with the new vision.
