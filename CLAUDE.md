# DevOrbit

## Mo ta project

DevOrbit la he thong full-stack quan ly va kham pha ma nguon hoc thuat cho sinh vien UIT. Giai doan phat trien tich cuc (production). Tich hop Spring Boot, React, Mobile Kotlin.

## Tech stack

- **Language**: Java 21, TypeScript ~5.7, Kotlin 2.0.21
- **Backend Framework**: Spring Boot 4.0.6, Spring Data JPA, Spring Security, WebClient
- **Frontend Framework**: React 19, Vite 6, Tailwind CSS 3.4, React Router 7
- **Data (Frontend)**: TanStack React Query 5, Zustand 5
- **Visualization**: D3 ForceGraph 2D, Three.js 0.184 / React Three Fiber 9.6
- **Mobile**: Jetpack Compose BOM 2024.11, Navigation Compose 2.8.4, Retrofit 2.11
- **Database**: PostgreSQL 16
- **Auth**: JWT (jjwt 0.12.6)
- **Build**: Maven (mvnw), Gradle 8.7.3
- **Infrastructure**: Docker Compose, Nginx, Supabase Storage

## Lenh quan trong

```bash
# Backend
cd devorbit-api && .\mvnw.cmd spring-boot:run

# Web frontend
cd devorbit-web && npm install && npm run dev

# Mobile
cd devorbit-mobile && .\gradlew.bat :app:assembleDebug

# Build all
docker compose up -d --build
```

## Cau truc thu muc

```text
devorbit-api/            # Spring Boot backend
  src/main/java/vn/edu/uit/devorbit_api/
    config/              # Security, JWT, OpenAPI, GitHub client, Jackson
    controller/          # 20 controllers
    dto/                 # 43 DTOs
    entity/              # 17 entities + 5 enums
    repository/          # 18 repositories
    service/             # 21 services
devorbit-web/            # React SPA
  src/pages/admin/       # 11 admin pages
  src/pages/student/     # 11 student pages
devorbit-mobile/         # Kotlin Android
devorbit-showcase/       # Next.js showcase (WIP)
docs/                    # Architecture, decisions, stories, templates
```

## Convention RIENG project

- API endpoints: `/api` (public), `/api/admin` (admin), `/api/student` (student)
- JWT tokens: `devorbit-admin-token`, `devorbit-student-token` (localStorage)
- Entity fields: `@CreationTimestamp`, `@UpdateTimestamp` for audit
- Soft-delete: `active` boolean field on entities
- React pages: PascalCase.tsx in `pages/admin/` or `pages/student/`
- API client: TanStack React Query for public pages, custom `useApiFetch` for admin
- Galaxy 3D state: Zustand store (`useGalaxyStore`)

## Module owner & noi can can than

- `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java` — security sensitive
- `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/JwtAuthenticationFilter.java` — auth logic
- `devorbit-web/src/pages/student/knowledge-graph/` — complex Three.js 3D visualization
- `devorbit-web/src/lib/photoCompositor.ts` — Photobooth canvas compositing

## Vung cam / dieu can biet

- KHONG thay doi schema DB khong co migration file kem (data.sql)
- KHONG `git push --force` len master
- JWT secret mac dinh chi cho local, BAT BUOC override cho production
- Breaking change API phai bump version + cap nhat CHANGELOG.md
- Galaxy 3D visualizations (Three.js) co the khong hoat dong tren thiet bi yeu
