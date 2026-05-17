# Courses Page Overrides

> **PROJECT:** DevOrbit
> **Generated:** 2026-05-16
> **Page Type:** Academic Course Listings

> **IMPORTANT:** Rules in this file **override** the Master file (`design-system/MASTER.md`).
> Only deviations from the Master are documented here. For all other rules, refer to the Master.

---

## Page-Specific Rules

### Course Hub (Mobile)

- **Layout:** Full-screen with collapsible bottom nav
- **Navigation:** Course list → Course detail → Repo detail (3-level drill-down)
- **Course Cards:** Vertical list with semester section headers
- **Repo Cards:** Orbit-card style with tech stack badges, star count, short description
- **Tech Filter:** Bottom sheet with multi-select tech stack filter chips
- **Search:** Search bar at top with debounced query

### Course Cards (Web)

- **Layout:** Responsive grid (1 col mobile, 2 col tablet, 3 col desktop)
- **Card Component:** `orbit-card` with course code (bold), name, description, repo count
- **Semester Badge:** `badge-muted` showing semester number
- **Repo Count Indicator:** Accent-colored number with "repositories" label
- **Hover:** Standard orbit-card hover (emerald border glow + translateY)

### Course Detail Page

- **Header:** Course code + course name in `heading-2`, semester in `badge`
- **Description Block:** `body-lg` for lead, `body-md` for details
- **Repo List:** Grid of `orbit-card-interactive` repo cards
- **Each Repo Card:** 
  - Display name (heading-4)
  - Tech stack badges (comma-separated, inline)
  - Short description (body-sm, text-secondary)
  - Star count (if available)
- **Empty State:** "Chua co repo nao" (No repos yet) with muted styling

### Knowledge Graph Page

- **Layout:** Full-width canvas with `react-force-graph-2d`
- **Grid Overlay:** 8-semester columns with dashed vertical separators
- **Course Nodes:** Rectangular cards with course code (bold) + name
- **Color States:**
  - White/Gray: Default
  - Blue: Selected elective
  - Red: Simulated failed course
  - Pink: Blocked by failed prerequisite
- **Interactive:** Click for simulation mode (mark fail → cascade blocked courses)
- **Elective Placeholders:** "T? ch?n Co s? ngành", "T? ch?n Chuyên ngành" at semesters 4-8
- **Elective Panel:** Overlay list when clicking elective placeholder
- **Headers:** "H?C K? 1-8" labels rendered in-canvas, zoom-synced

### Course Repository (Web)

- **Layout:** Tabbed or page sections per course
- **Tech Stack Filter:** Tag-based multi-select filter bar
- **Sort:** Backend-driven `ORDER BY COUNT(r) DESC`
- **Pagination:** Page-based navigation at bottom

### Color Overrides

- **Semester Headers:** `text-orbit-text-muted` with `badge-muted` styling
- **Course Code:** `text-orbit-accent` for emphasis
- **Elective Highlight:** Blue tint (`#3B82F6`) for selected state

### Typography Overrides

- **Course Code:** `heading-4` (20px, semibold)
- **Course Name:** `body-lg` (17px, regular, text-secondary)
- **Repo Count:** `heading-3` (24px, bold, accent color)
- **Semester Label:** `badge` (10px, uppercase tracking-wide)

---

## Page-Specific Components

- **CourseCard** — Grid card with code, name, semester badge, repo count
- **RepoCard** — Interactive card with display name, tech badges, description, stars
- **TechFilterSheet** — Multi-select bottom sheet with tech stack chips
- **CourseHubScreen** — Full course listing with semester groupings (mobile)
- **KnowledgeGraphCanvas** — Force-directed graph with blueprint grid overlay
- **SimulationOverlay** — Context panel for fail/blocked course simulation
- **ElectiveSelectionPanel** — Overlay list for elective course selection

---

## Recommendations

- Effects: Emerald glow on interactive cards, glass backdrop on filter sheets
- Navigation: Course list → detail → repo detail (breadcrumb trail)
- Empty State: "Chua co du lieu" with muted styling and optional CTA
- Loading: Skeleton shimmer lines for card placeholders
- Responsive: Stack grid to single column on mobile; horizontal scroll on knowledge graph
