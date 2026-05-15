# DevOrbit

**Nen tang quan ly va kham pha ma nguon hoc thuat danh cho sinh vien UIT**

DevOrbit la mot he thong full-stack duoc xay dung nhan dip ky niem 20 nam thanh lap **Truong Dai hoc Cong nghe Thong tin (UIT)**. He thong giup sinh vien de dang tra cuu cac khoa hoc, kho ma nguon (Legacy Repos) da duoc tuyen chon, truy cap so do kien thuc chuong trinh dao tao, tao anh ky niem AI (Photobooth), va nhan lo trinh hoc AI ca nhan hoa.

---

## Muc tieu

- Cung cap mot **catalogue truc tuyen** cac mon hoc va repository lien quan, phuc vu nhu cau hoc tap va nghien cuu cua sinh vien UIT.
- Xay dung **quy trinh kiem duyet** (scan -> candidate -> approve) giup giang vien de dang nhap khau ma nguon tu GitHub vao he thong mot cach co kiem soat.
- Cung cap **So do kien thuc (Knowledge Graph)** truc quan hoa chuong trinh dao tao SE2025 dang luoi (Blueprint Grid) voi che do gia lap truot mon.
- Tich hop **AI** cho tao anh ky niem (Photobooth), goi y AI, va sinh lo trinh hoc (Roadmap Generator).
- Ho tro da nen tang: **Web (React)** va **Mobile (Kotlin Android)**.

---

## Tinh nang da hoan thanh

### Cong khai (Public - khong can dang nhap)

| Tinh nang | Mo ta |
|-----------|-------|
| Danh sach mon hoc | Hien thi tat ca mon hoc dang hoat dong, kem ma mon, ten, loai mon, hoc ky |
| Chi tiet mon hoc | Xem danh sach repository, tai lieu (articles, tutorials, YouTube playlists), loc theo tech stack |
| Kho ma nguon | Xem thong tin repo: ten, mo ta, ngon ngu, so sao, tech stack, AI summary, AI advice |
| Lien ket GitHub | Mo repository truc tiep tren GitHub |
| Danh sach tech stack | Endpoint toan cuc de lay tech stack va loc repo theo mon |
| Knowledge Graph (2D) | So do kien thuc dang luoi (Blueprint Grid) voi 8 hoc ky, che do gia lap truot mon, he thong mon tu chon |
| Galaxy 3D | Truc quan hoa moi quan he mon hoc dang 3D galaxy (Three.js / R3F) voi xoay, phong to/thu nho |
| Photobooth | Tao anh ky niem voi khung anh AI (frame compositing), su dung the chung minh thu/Diem danh |
| AI Roadmap Generator | Sinh lo trinh hoc ca nhan hoa dua tren mon hoc da hoan thanh va muc tieu ca nhan |
| AI Summary & Advice | Tom tat va loi khuyen cho moi repo bang AI |

### Quan tri (Admin - yeu cau JWT)

| Tinh nang | Mo ta |
|-----------|-------|
| Dang nhap | Xac thuc bang username/password, nhan JWT |
| Quan ly mon hoc | Them, sua, xoa (soft-delete) mon hoc |
| Quan ly tai lieu mon | Them/sua/xoa articles, tutorials, YouTube playlists |
| Quan ly quan he mon | Thiet lap PREREQUISITE, COMPLEMENTARY, COREQUISITE |
| Quet GitHub | Nhap course ID + query, backend goi GitHub Search API va luu candidate kem metadata |
| Duyet candidate | Xem danh sach pending, duyet hoac tu choi candidate, phan cong nguoi review |
| Quan ly repo da duyet | Xem danh sach, sua va xoa (soft-delete) repo |
| Quan ly Roadmap | Tao/sua/xoa learning roadmap, phase, item |
| Quan ly Notes | Ghi chu ca nhan (admin), code snippets |
| Quan ly Photobooth Frames | Them/sua/xoa frame definitions, kich thuoc, overlay |
| Dashboard | Thong ke tong quan (so mon, so repo, so candidate) |

### Sinh vien (Student - yeu cau dang ky va JWT)

| Tinh nang | Mo ta |
|-----------|-------|
| Dang ky | Tao tai khoan voi ma so sinh vien, email, mat khau, OTP xac thuc |
| Dang nhap | Xac thuc bang ma so SV + mat khau, nhan JWT |
| Bookmark | Luu mon hoc hoac repository vao danh sach ca nhan |
| Xem bookmark | Danh sach cac muc da luu, co link dieu huong |

### Mobile (Android - Kotlin Compose)

- Course hub: danh sach mon hoc (offline-first voi cache), chi tiet mon + repo + tech stack filter
- Repo detail: mo tren GitHub, AI summary
- Bookmarks: luu va xem bookmark
- Explore screen: tech stack chips, recent repos, search
- Subject screen: danh sach mon hoc theo hoc ky, bo loc

---

## Tinh nang chua hoan thanh / con thieu

### Backend (API)

| Tinh nang | Ghi chu |
|-----------|---------|
| Lich quet dinh ky | Scheduled scanning (non-goal MVP) |
| Role hierarchy | Chi co ADMIN va STUDENT, chua co phan cap chi tiet |
| Social vote | Vote / danh gia repository |

### Web Frontend

| Tinh nang | Ghi chu |
|-----------|---------|
| Test coverage | Da co Vitest, nhung chua co test case thuc te cho UI |

### Mobile App

| Tinh nang | Ghi chu |
|-----------|---------|
| Hilt DI | Da co DataModule voi Retrofit, OkHttp, Repository injection |
| ViewModel | Da co CourseViewModel, AiTutorViewModel, ExploreViewModel |
| AI Insights | Man hinh RepoDetailScreen co AI summary va tutor advice |
| Auth + Bookmarks | Da xoa khoi codebase (restructure), can them lai |
| Gradle wrapper (Linux) | Chi co `gradlew.bat`, thieu `gradlew` cho moi truong Unix |

### Infrastructure

| Van de | Ghi chu |
|--------|---------|
| Test integration | Can DB that de chay Spring Boot test (PostgreSQL qua Docker Compose) |

---

## Cong nghe su dung

### Backend
| Cong nghe | Phien ban |
|-----------|-----------|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | — |
| Spring Security | — |
| Spring WebMVC | — |
| Spring WebFlux (WebClient) | — |
| PostgreSQL | 16 |
| JWT (jjwt) | 0.12.6 |
| Lombok | — |
| OpenAPI / Swagger | springdoc-openapi |
| Maven | Wrapper (`mvnw`) |

### Web Frontend
| Cong nghe | Phien ban |
|-----------|-----------|
| React | 19 |
| TypeScript | ~5.7 |
| Vite | 6 |
| Tailwind CSS | 3.4 |
| React Router | 7 |
| TanStack React Query | 5 |
| Zustand | 5 |
| Three.js / React Three Fiber | 0.184 / 9.6 |
| D3 ForceGraph | react-force-graph-2d 1.29 |
| Phosphor Icons / Lucide | — |
| Vitest | — |

### Mobile
| Cong nghe | Phien ban |
|-----------|-----------|
| Kotlin | 2.0.21 |
| Jetpack Compose | BOM 2024.11.00 |
| Navigation Compose | 2.8.4 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| Kotlinx Coroutines | 1.9.0 |
| Gradle | 8.7.3 |

### Infrastructure
| Cong nghe | Ghi chu |
|-----------|---------|
| Docker Compose | 3 service: db, api, web |
| Nginx | Reverse proxy cho web -> api |
| PostgreSQL 16 Alpine | Database |
| Supabase Storage | File upload cho Photobooth |

---

## Huong dan chay local

### Yeu cau
- Docker & Docker Compose
- Java 21+ (neu chay backend khong qua Docker)
- Node.js 20+ (neu chay web khong qua Docker)
- GitHub Personal Access Token (de dung tinh nang scan)

### Chay toan bo he thong bang Docker Compose

```bash
git clone https://github.com/huyhoang171106/DevOrbit.git
cd DevOrbit

# Tao file .env
# Bat buoc: GITHUB_TOKEN=<your-token>
# Khuyen nghi doi: JWT_SECRET=<secret-dai-hon-32-ky-tu>

# Khoi dong
docker compose up -d --build
```

Sau khi chay:
- **Web:** http://localhost:3000
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Database:** localhost:5432 (user: `huyhoang`, password: `huyhoang`, db: `devorbit_db`)

### Chay backend rieng (khong Docker)

```bash
cd devorbit-api
.\mvnw.cmd spring-boot:run
```

Yeu cau PostgreSQL dang chay va bien moi truong:
- `DATABASE_URL=jdbc:postgresql://localhost:5432/devorbit_db`
- `DATABASE_USERNAME=huyhoang`
- `DATABASE_PASSWORD=huyhoang`
- `GITHUB_TOKEN=<your-token>`
- `JWT_SECRET=<your-secret>`

### Chay web rieng (khong Docker)

```bash
cd devorbit-web
npm install
npm run dev
```

Dev server chay tai `http://localhost:5173`, proxy API den `http://localhost:8080`.

### Chay mobile (Android)

Mo thu muc `devorbit-mobile/` bang Android Studio, dam bao co thiet bi gia lap chay API 26+.

> **Luu y:** API base URL mac dinh la `http://10.0.2.2:8080` (Android emulator -> host machine). Neu backend chay tren Docker, can doi URL hoac expose port.

---

## Cau truc thu muc

```
devorbit/
├── devorbit-api/                    # Spring Boot backend
│   └── src/main/java/vn/edu/uit/devorbit_api/
│       ├── config/                  # Security, JWT, GitHub client, OpenAPI, Jackson
│       ├── controller/              # 21 REST controllers (Admin, Public, Student)
│       ├── dto/                     # 43 Request/Response DTOs (admin, public, student)
│       ├── entity/                  # 18 JPA entities + 4 enums
│       ├── exception/               # Global exception handler + custom exceptions
│       ├── repository/              # 18 Spring Data repositories
│       └── service/                 # 25 business services (20 main + 5 AI sub-services)
├── devorbit-web/                    # React + Vite web frontend
│   └── src/
│       ├── components/              # Reusable UI (admin, student, photobooth)
│       ├── lib/                     # API client, auth, colors, photoCompositor, frames
│       ├── pages/                   # 11 admin + 11 student pages
│       └── types/                   # TypeScript type definitions
├── devorbit-mobile/                 # Kotlin Android app
├── devorbit-showcase/               # Next.js showcase site (WIP)
├── docs/                            # Architecture, glossary, test matrix, decisions
├── docker-compose.yml               # Multi-service orchestration
├── AGENTS.md                        # AI agent guidance
└── README.md                        # This file
```

---

## Tong quan trang thai du an

| Phan he | Trang thai | Ghi chu |
|---------|-----------|---------|
| Backend API | San xuat | 21 controllers, 25 services (20 main + 5 AI), 18 repositories, 18 entities, 4 enums, 43 DTOs, 7 tests |
| Admin pipeline | Hoan thanh | Login, CRUD course, GitHub scan, candidate review, repo management, resources, roadmaps, notes, photobooth frames |
| Public browsing | Hoan thanh | Course list, course detail, repo detail, tech stack filter, Knowledge Graph, Galaxy 3D, Photobooth, AI Roadmap |
| Student auth + bookmark | Hoan thanh | Register (OTP), login, JWT, bookmark course/repo, list/delete |
| Knowledge Graph | Hoan thanh | Blueprint Grid 8 semesters, simulation mode, elective panels, 3D Galaxy |
| AI Features | Hoan thanh | AI summary/advice, Photobooth frame compositing, AI Roadmap Generator |
| Web frontend | San xuat | 20+ routes, 22 pages, end-to-end voi API |
| Mobile app | Co ban | Course hub, repo detail, bookmarks, explore screen, subject screen |
| Infrastructure | Hoan thanh | Docker Compose 3 services, health checks, nginx proxy, Supabase storage |
| Tests | Can bo sung | 7 backend tests (controller + service), web 0, mobile unit tests co |

---

## Ke hoach phat trien

### Tuong lai goi y
1. Them lai man hinh dang ky + bookmark cho mobile
2. Bo sung test cho web (Vitest / Playwright)
3. Vote / danh gia repository
4. Dashboard thong ke nang cao cho admin
5. Lich quet GitHub dinh ky
6. CI/CD pipeline
7. Social features (binh luan, chia se)

---

## Ghi chu

- Du an dang o giai doan phat trien tich cuc, mot so thanh phan co the thay doi.
- JWT secret mac dinh trong `docker-compose.yml` chi dung cho local dev. Khi trien khai thuc te, **bat buoc** override bang bien moi truong `JWT_SECRET`.
- DB password trong `docker-compose.yml` cung chi dung cho local. Su dung Docker secrets hoac bien moi truong cho production.
- Chi tiet ky thuat va conventions cho tung phan he xem trong file `AGENTS.md` tuong ung.

---

## Dong gop

Du an duoc phat trien boi sinh vien UIT. Moi dong gop deu duoc hoan nghenh. Vui long tao issue hoac pull request tren GitHub.

---

## Giay phep

Du an hoc thuat noi bo - Truong Dai hoc Cong nghe Thong tin (UIT).
