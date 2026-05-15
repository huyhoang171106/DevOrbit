# Performance Optimization Plan — DevOrbit Web

## Scope

Optimize `devorbit-web` (Vite 6 + React 19 + TypeScript) for load speed, runtime responsiveness, and bundle size.

## Status

| Section | Change | Status |
|---------|--------|--------|
| A | ParticleNetwork — Runtime CPU Optimization | Pending |
| B | Dead 3D Scene Code Removal | Pending |
| C | Vite Config — Bundle Splitting + Optimization | Pending |
| D | Font Optimization | Pending |
| E | CSS Cleanup — Backward-Compatible Aliases | Pending |
| F | Unused Dependency Removal | Pending |
| G | Framer Motion Optimization | Pending |

All items remain unimplemented. This plan tracks work still to be done.

## Changes

### A. ParticleNetwork — Runtime CPU Optimization
**File:** `src/components/ParticleNetwork.tsx`
- Reduce particles: 80 → 25
- Remove O(n³) triangle mesh computation (3 nested loops, ~240K checks/frame)
- Keep line connections (O(n²) with limit to top 2 neighbors)
- Keep mouse interaction (simple per-particle spring physics)
- Add canvas size debounce on resize
- Add `will-change: opacity` to canvas element
- Impact: ~80% reduction in per-frame computation, preserves visual aesthetic

### B. Dead 3D Scene Code Removal
**Files to delete (13 files, ~1569 lines):**
- `src/pages/student/knowledge-graph/GalaxyCanvas.tsx`
- `src/pages/student/knowledge-graph/effects/Starfield.tsx`
- `src/pages/student/knowledge-graph/effects/PlanetTrail.tsx`
- `src/pages/student/knowledge-graph/effects/OrbitRings.tsx`
- `src/pages/student/knowledge-graph/entities/Planet.tsx`
- `src/pages/student/knowledge-graph/entities/Wormhole.tsx`
- `src/pages/student/knowledge-graph/systems/ConstellationSystem.tsx`
- `src/pages/student/knowledge-graph/systems/OrbitalGroup.tsx`
- `src/pages/student/knowledge-graph/systems/WormholeSystem.tsx`
- `src/pages/student/knowledge-graph/camera/GalaxyCamera.tsx`
- `src/pages/student/knowledge-graph/context/PlanetPositionContext.tsx`
- `src/pages/student/knowledge-graph/ui/GalaxyOverlay.tsx`
- `src/pages/student/KnowledgeGraphPage.tsx` (old 2D force-graph page)

Also clean empty parent directories after deletion.

**Also delete unused subdirectories:**
- `src/pages/student/knowledge-graph/camera/`
- `src/pages/student/knowledge-graph/context/`
- `src/pages/student/knowledge-graph/effects/`
- `src/pages/student/knowledge-graph/entities/`
- `src/pages/student/knowledge-graph/layout/` (check if used)
- `src/pages/student/knowledge-graph/systems/`
- `src/pages/student/knowledge-graph/ui/`

**Keep file:** `src/pages/student/knowledge-graph/GalaxyPage.tsx` (active route with KanbanBoard)

### C. Vite Config — Bundle Splitting + Optimization
**File:** `vite.config.ts`
- Add `rollupOptions.output.manualChunks`:
  - `vendor-anim` → framer-motion
  - `vendor-dnd` → @dnd-kit
  - `vendor-markdown` → react-markdown
- Add `cssCodeSplit: true`
- Restore `modulePreload: { polyfill: true }` (safer browser support)
- Add `rollup-plugin-visualizer` as optional dev plugin (not installed, just documented for CI)

### D. Font Optimization
**File:** `src/index.css`
- Remove all `.woff` font imports (keep only `.woff2`)
- Reduce Baloo-2 from 5 weights (400,500,600,700,800) to 3 (400,600,700)
- Impact: ~580KB font downloads → ~200KB

### E. CSS Cleanup — Remove Backward-Compatible Aliases
**File:** `src/index.css`
- Remove the entire `@layer utilities` block with `bg-clay-*`, `bg-glass-*`, `text-ink-*`, `text-clay-*`, `border-clay-*`, `.glass-card`, `.glass-card-glow`, `.clay-card-hover` aliases
- Impact: ~5-10KB CSS reduction

### F. Unused Dependency Removal
**File:** `package.json`
- Remove: `@fontsource/geist-sans`, `lucide-react`, `react-force-graph-2d`
- Keep: `@supabase/supabase-js`, `@ai-sdk/openai-compatible`, `ai` (may be used in future)
- After removal: run `npm install` to clean `node_modules`

### G. Framer Motion Optimization
**Files:** `src/components/Layout.tsx`, `src/components/student/DiscoveryFeed.tsx`
- Replace `type: 'spring'` with `{ duration: 0.3 }` tween in nav indicator
- Replace spring transitions in DiscoveryFeed card animations
- Remove redundant `whileInView` viewport margins and delays where excessive
- Impact: Less CPU from spring physics solver, fewer scroll observers

## Implementation Order

1. **A (ParticleNetwork)** — Immediate CPU relief on every page
2. **B (Dead Code)** — Safe deletions, no functional impact
3. **C (Vite)** — Only build-time config change
4. **D + E (CSS/Fonts)** — Same file, one combined edit
5. **F (Dependencies)** — package.json cleanup
6. **G (Framer)** — Requires visual verification after

## Validation

- `npm run build` must succeed after all changes
- `npm run dev` must serve without errors
- Bundle sizes should be measurably smaller
- Pages should load without visual breakage
