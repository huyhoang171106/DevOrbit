# Design System Master File

> **LOGIC:** When building a specific page, first check `design-system/pages/[page-name].md`.
> If that file exists, its rules **override** this Master file.
> If not, strictly follow the rules below.

---

**Project:** DevOrbit
**Generated:** 2026-05-16
**Category:** Academic Platform

---

## Global Rules

### Color Palette

| Role | Hex | CSS Variable |
|------|-----|--------------|
| Primary (Accent) | `#34D399` | `--orbit-accent` |
| Background | `#09090B` | `--orbit-bg` |
| Surface | `#18181B` | `--orbit-surface` |
| Elevated | `#27272A` | `--orbit-elevated` |
| Border | `rgba(39,39,42,0.8)` | `--orbit-border` |
| Accent Subtle | `rgba(52,211,153,0.1)` | `--orbit-accent-subtle` |
| Text | `#FAFAFA` | `--orbit-text` |
| Text Secondary | `#A1A1AA` | `--orbit-text-secondary` |
| Text Muted | `#52525B` | `--orbit-text-muted` |
| Ink (alias) | `#FAFAFA` | `--ink` |
| Ink Secondary (alias) | `#A1A1AA` | `--ink-secondary` |
| Ink Muted (alias) | `#52525B` | `--ink-muted` |

**Color Notes:** Dark mode only — emerald accent on zinc/charcoal background. WCAG AA compliant (text contrast: 13.9:1 primary, 7.3:1 secondary, 4.7:1 muted).

### Mobile Cosmic Palette

| Role | Hex | Usage |
|------|-----|-------|
| Primary (Plasma) | `#7B61FF` | Headers, key actions, bottom nav active |
| Secondary | `#00D2FF` | Links, secondary accents |
| Tertiary | `#00F5A0` | Success, completion states |
| Background (Void) | `#05050A` | App background |
| Surface (Nebula) | `#10101A` | Cards, sheets, elevated surfaces |

### Typography

| Role | Font | Fallback |
|------|------|----------|
| Headings | **Baloo 2** (400–800) | system-ui, sans-serif |
| Body | **Inter** (400–700) | system-ui, sans-serif |
| Mono | **Geist Mono** (400–500) | monospace |

**Scale:**

| Token | Size | Weight | Usage |
|-------|------|--------|-------|
| `hero-display` | 56–96px | Black (900) | Landing hero |
| `display-lg` | 40–56px | Black (900) | Section heroes |
| `display-md` | 32–40px | Bold (700) | Major sections |
| `display-sm` | 24–28px | Bold (700) | Sub-sections |
| `heading-1` | 40px | Black (900) | Page title |
| `heading-2` | 32px | Bold (700) | Section title |
| `heading-3` | 24px | Bold (700) | Card title |
| `heading-4` | 20px | Semibold (600) | Component title |
| `heading-5` | 16px | Semibold (600) | Small heading |
| `subtitle` | 20px | Medium (500) | Hero subtitle |
| `body-lg` | 17px | Regular (400) | Lead text |
| `body-md` | 15px | Regular (400) | Body text |
| `body-sm` | 13px | Regular (400) | Captions |
| `badge` | 10px | Black (900) | Labels, tags |

**Mood:** Tech-forward, dark, confident, cosmic — premium developer tool aesthetic.

### Spacing (Tailwind-based)

| Token | Value | Usage |
|-------|-------|-------|
| `--space-xs` | `4px` | Tight gaps |
| `--space-sm` | `8px` | Icon gaps, inline spacing |
| `--space-md` | `16px` | Standard padding |
| `--space-lg` | `24px` | Section padding |
| `--space-xl` | `32px` | Large gaps |
| `--space-2xl` | `48px` | Section margins |
| `--space-3xl` | `64px` | Hero padding |
| `--space-4xl` | `84px` | Major sections |

### Border Radii

| Token | Value | Usage |
|-------|-------|-------|
| `rounded-2xl` | 16px (1rem) | Buttons, inputs |
| `rounded-3xl` | 24px (1.5rem) | Medium cards |
| `rounded-4xl` | 32px (2rem) | Default cards |
| `rounded-5xl` | 40px (2.5rem) | Hero sections |

### Shadow Depths

| Level | Value | Usage |
|-------|-------|-------|
| `shadow-diffusion` | `0 20px 40px -15px rgba(0,0,0,0.5)` | Cards, modals |
| `shadow-diffusion-lg` | `0 30px 60px -20px rgba(0,0,0,0.6)` | Featured cards |
| `shadow-glow` | `0 0 20px rgba(52,211,153,0.15)` | Interactive hover |
| `shadow-glow-lg` | `0 0 40px rgba(52,211,153,0.1)` | CTA hover |
| `inner-glow` | `inset 0 1px 0 rgba(255,255,255,0.05)` | Surface edges |

### Animations

| Name | Duration | Usage |
|------|----------|-------|
| `shimmer` | 3s ease-in-out infinite | Loading skeletons |
| `float` | 4s ease-in-out infinite | Hero graphics |
| `pulse-soft` | 3s ease-in-out infinite | Status dots |
| `breathing` | 3s ease-in-out infinite | Live indicators |

---

## Component Specs

### Cards (orbit-card)

```css
.orbit-card {
  background: #18181B;
  border: 1px solid rgba(39, 39, 42, 0.8);
  border-radius: 2rem;
  padding: 2rem;
  box-shadow: 0 20px 40px -15px rgba(0,0,0,0.5);
  transition: all 500ms;
}
.orbit-card:hover {
  border-color: rgba(52, 211, 153, 0.2);
  box-shadow: 0 0 20px rgba(52, 211, 153, 0.15);
}
.orbit-card-interactive {
  cursor: pointer;
}
.orbit-card-interactive:hover {
  transform: translateY(-4px);
}
.orbit-card-glow {
  border-color: rgba(52, 211, 153, 0.1);
}
.orbit-card-glow:hover {
  border-color: rgba(52, 211, 153, 0.3);
}
```

### Glass Surface (orbit-glass)

```css
.orbit-glass {
  background: rgba(39, 39, 42, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(39, 39, 42, 0.8);
  border-radius: 2rem;
}
.orbit-glass-strong {
  background: rgba(39, 39, 42, 0.8);
  backdrop-filter: blur(40px);
}
```

### Buttons

```css
/* Primary */
.btn-primary {
  background: #34D399;
  color: #09090B;
  padding: 16px 32px;
  border-radius: 16px;
  font-weight: 700;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  transition: all 300ms;
}
.btn-primary:hover {
  background: #86EFAC;
  box-shadow: 0 0 40px rgba(52, 211, 153, 0.1);
  transform: translateY(-1px);
}
.btn-primary:active {
  transform: scale(0.98);
}

/* Secondary */
.btn-secondary {
  background: transparent;
  color: #FAFAFA;
  border: 1px solid rgba(39, 39, 42, 0.8);
  padding: 16px 32px;
  border-radius: 16px;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 600;
  transition: all 300ms;
}
.btn-secondary:hover {
  border-color: rgba(52, 211, 153, 0.4);
  background: rgba(52, 211, 153, 0.1);
  color: #34D399;
}

/* Ghost */
.btn-ghost {
  background: transparent;
  color: #A1A1AA;
  padding: 12px 20px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
  transition: all 200ms;
}
.btn-ghost:hover {
  background: #18181B;
  color: #FAFAFA;
}
```

### Badges

```css
.badge {
  background: rgba(52, 211, 153, 0.1);
  border: 1px solid rgba(52, 211, 153, 0.2);
  color: #34D399;
  padding: 8px 16px;
  border-radius: 9999px;
  font-size: 10px;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 0.2em;
}
.badge-muted {
  background: #18181B;
  border: 1px solid rgba(39, 39, 42, 0.8);
  color: #52525B;
  padding: 8px 16px;
  border-radius: 9999px;
  font-size: 10px;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 0.2em;
}
```

### Inputs

```css
.input-field {
  background: #18181B;
  border: 1px solid rgba(39, 39, 42, 0.8);
  border-radius: 16px;
  padding: 16px 24px;
  color: #FAFAFA;
  font-size: 15px;
  transition: all 300ms;
  width: 100%;
}
.input-field:focus {
  border-color: rgba(52, 211, 153, 0.4);
  box-shadow: 0 0 0 4px rgba(52, 211, 153, 0.05);
  outline: none;
}
.input-field::placeholder {
  color: rgba(82, 82, 91, 0.6);
}
```

### Dividers

```css
.divider-gradient {                     
  @apply h-px w-full bg-gradient-to-r from-transparent via-orbit-border to-transparent;
}
.divider-accent {
  @apply h-px w-full bg-gradient-to-r from-transparent via-orbit-accent/20 to-transparent;
}
```

### Typography Helpers

```css
.section-label {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 9999px;
  background: rgba(52, 211, 153, 0.1);
  border: 1px solid rgba(52, 211, 153, 0.2);
  color: #34D399;
  font-size: 10px;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 0.25em;
}
```

---

## Style Guidelines

**Style:** Dark Tech — Claymorphism + Glassmorphism (consolidated)

**Keywords:** Dark mode, cosmic, emerald glow, rounded cards, glass blur, diffusion shadows, floating elements, tech-forward, developer tool aesthetic

**Best For:** Academic platforms, developer tools, portfolio, data dashboards, educational apps

**Key Effects:** Glass backdrop blur (20-40px), emerald glow on hover, inner-glow edge highlight, diffusion shadows, rounded cards (2rem default)

### Page Pattern

**Pattern Name:** Dark Cosmic Z-Pattern

- **Conversion Strategy:** Trust-first, value-driven. Dark mode immerses user. Emerald CTA draws attention.
- **CTA Placement:** Bottom of hero section + sticky top nav on scroll
- **Section Order:** 1. Dark Hero with Cosmic Background, 2. Feature/Metrics Grid, 3. Content Sections, 4. CTA, 5. Minimal Footer

### Sub-patterns

- **Repository Bento:** 2-3 column grid of repo cards with tech stack badges, star counts, description
- **Knowledge Graph:** Full-width canvas with semester grid, simulation mode, elective selection
- **Course Hub:** Horizontal scroll + detail panels, semester filters
- **Data Dashboard:** Metrics cards (2x2), glass surfaces, animated gauges

---

## Anti-Patterns (Do NOT Use)

- ❌ Generic light/dark toggle — dark mode only
- ❌ No personality / generic AI design
- ❌ Low contrast text on dark backgrounds (maint ain 4.5:1 minimum)
- ❌ Using competing design systems (claymorphism vs glass) — they are consolidated

### Additional Forbidden Patterns

- ❌ **Emojis as icons** — Use SVG icons (Heroicons, Lucide, Simple Icons)
- ❌ **Missing cursor:pointer** — All clickable elements must have cursor:pointer
- ❌ **Layout-shifting hovers** — Avoid scale transforms that shift layout
- ❌ **Instant state changes** — Always use transitions (200-500ms)
- ❌ **Invisible focus states** — Focus states must be visible for a11y
- ❌ **Rose/Pink palette** — Legacy palette, replaced by emerald + zinc
- ❌ **Light mode / white backgrounds** — Only dark mode supported

---

## Pre-Delivery Checklist

Before delivering any UI code, verify:

- [ ] No emojis used as icons (use SVG instead)
- [ ] All icons from consistent icon set (Heroicons/Lucide)
- [ ] `cursor-pointer` on all clickable elements
- [ ] Hover states with smooth transitions (200-500ms)
- [ ] Dark background text contrast 4.5:1 minimum
- [ ] Focus states visible for keyboard navigation
- [ ] `prefers-reduced-motion` respected
- [ ] Responsive: 375px, 768px, 1024px, 1440px
- [ ] No content hidden behind fixed navbars
- [ ] No horizontal scroll on mobile
- [ ] Uses orbit-* CSS variables, not legacy clay/glass/ink mix
- [ ] Dark mode only — no light mode infrastructure
