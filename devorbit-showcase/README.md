# UIT 20th Anniversary Showcase

An immersive 3D interactive showcase celebrating 20 years of the University of Information Technology (UIT) — VNU-HCM. Built with Next.js, React Three Fiber (R3F), and GSAP, this project takes visitors on a scroll-driven journey through UIT's history, achievements, and the DevOrbit ecosystem.

> **Live site:** [uit.edu.vn](https://uit.edu.vn)

## Features

- **3D Window Metaphor** — Scroll to open the window and reveal the experience section
- **UIT History Timeline** — Navigate 20 milestone years (2006–2026) with animated 3D path and timeline points
- **DevOrbit Ecosystem** — Explore 8 smart learning tools via interactive feature panels
- **Dual Theme** — Light/dark mode toggle with persisted preference
- **Responsive** — Optimized for desktop and mobile with device-specific interactions
- **Smooth Animations** — GSAP-powered scroll animations, camera transitions, and portal effects

## Tech Stack

| Category     | Technology          | Purpose                         |
|-------------|---------------------|---------------------------------|
| Framework   | Next.js 16          | App Router, SSR, static export  |
| UI          | React 19            | Component model                 |
| 3D Engine   | Three.js            | WebGL rendering                 |
| R3F         | @react-three/fiber  | React bindings for Three.js     |
| R3D Utils   | @react-three/drei   | Helpers, controls, materials    |
| Animation   | GSAP                | Scroll-triggered animations     |
| State       | Zustand             | Theme, scroll, portal stores    |
| Styling     | Tailwind CSS 4      | Utility-first CSS               |
| Language    | TypeScript          | Type safety                     |
| Package Mgr | npm / Bun           | Dependency management           |

## Getting Started

```bash
# Install dependencies
npm install

# Run development server (with Webpack)
npm run dev

# Build for production
npm run build

# Start production server
npm start
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

## Project Structure

```
app/
  page.tsx              # Entry point (CanvasLoader > ScrollWrapper > Hero/Experience/Footer)
  layout.tsx            # Root layout, fonts, Google Analytics, metadata
  globals.css           # Tailwind, custom styles, scrollbar, theme variables
  constants/            # Data (work timeline, projects, footer links, DevOrbit features)
  stores/               # Zustand stores (portalStore, scrollStore, themeStore)
  types/                # TypeScript interfaces
  components/
    common/             # CanvasLoader, ScrollWrapper, ThemeSwitcher, ProgressLoader, etc.
    hero/               # Hero section (3D window, clouds, stars, text)
    experience/         # Portal grid (Work timeline + DevOrbit features)
      work/             # UIT history timeline (3D animated path)
      projects/         # DevOrbit feature panels with Wanderer model
    models/             # 3D models (Cloud, Stars, WindowModel, UITCore, Memory, Wanderer)
    footer/             # 3D footer with social links
```

## Deployment

The project is deployed to **GitHub Pages** via a GitHub Actions workflow (`.github/workflows/nextjs.yml`). The build generates a static export (`next build`) and uploads the `out/` directory.

## License

This project is developed for the UIT 20th Anniversary celebration.
