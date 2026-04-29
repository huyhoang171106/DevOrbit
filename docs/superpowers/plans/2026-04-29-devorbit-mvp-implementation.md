# DevOrbit MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the DevOrbit MVP around a curated Legacy Repo Finder flow with a Spring Boot backend, React web app for student and admin use, and a Kotlin Android student app.

**Architecture:** Extend the existing `devorbit-api` backend instead of replacing it. Establish a stable public and admin REST API first, then build the web student and admin experiences on top of it, then build the Kotlin student app against the same public API.

**Tech Stack:** Java 21, Spring Boot 4, Spring Data JPA, Spring Security, PostgreSQL, Maven, React, Vite, Tailwind CSS, shadcn/ui, Kotlin Android.

---

## File Structure Map

### Existing backend files to modify

- Modify: `devorbit-api/pom.xml`
- Modify: `devorbit-api/src/main/resources/application.yaml`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/Course.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/GithubRepo.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/CourseRepository.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/GithubRepoRepository.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CourseService.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubRepoService.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/CourseController.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/GithubRepoController.java`
- Modify: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/DevorbitApiApplicationTests.java`

### New backend files to create

- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/GithubProperties.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/JwtProperties.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/AdminUser.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/RepoCandidate.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/TechStack.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/RepoCandidateStatus.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/AdminUserRepository.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/RepoCandidateRepository.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/TechStackRepository.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/publicapi/CourseSummaryResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/publicapi/CourseDetailResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/publicapi/RepoSummaryResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/publicapi/TechStackResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/LoginRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/LoginResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/AdminCourseUpsertRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/GithubScanRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/CandidateReviewRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/RepoCandidateResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/ApprovedRepoUpdateRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/AdminAuthService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubScanService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/RepoCandidateService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/TechStackService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/JwtService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicCourseController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicRepoController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicTechStackController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminAuthController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminCourseController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminGithubController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminRepoCandidateController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminRepoController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/exception/ApiExceptionHandler.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/exception/NotFoundException.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/exception/UnauthorizedException.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/exception/BadRequestException.java`
- Create: `devorbit-api/src/main/resources/data.sql`
- Create: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/PublicCourseControllerTest.java`
- Create: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/PublicRepoControllerTest.java`
- Create: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/AdminAuthControllerTest.java`
- Create: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/AdminCourseControllerTest.java`
- Create: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/AdminRepoCandidateControllerTest.java`
- Create: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/service/RepoCandidateServiceTest.java`

### New web app files to create

- Create: `devorbit-web/package.json`
- Create: `devorbit-web/vite.config.ts`
- Create: `devorbit-web/tsconfig.json`
- Create: `devorbit-web/src/main.tsx`
- Create: `devorbit-web/src/App.tsx`
- Create: `devorbit-web/src/lib/api.ts`
- Create: `devorbit-web/src/lib/auth.ts`
- Create: `devorbit-web/src/types/api.ts`
- Create: `devorbit-web/src/pages/student/HomePage.tsx`
- Create: `devorbit-web/src/pages/student/CourseListPage.tsx`
- Create: `devorbit-web/src/pages/student/CourseDetailPage.tsx`
- Create: `devorbit-web/src/pages/admin/LoginPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminDashboardPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminCoursesPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminScanPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminCandidatesPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminReposPage.tsx`
- Create: `devorbit-web/src/components/student/CourseCard.tsx`
- Create: `devorbit-web/src/components/student/RepoFilterBar.tsx`
- Create: `devorbit-web/src/components/student/RepoCard.tsx`
- Create: `devorbit-web/src/components/admin/CourseFormDialog.tsx`
- Create: `devorbit-web/src/components/admin/ScanForm.tsx`
- Create: `devorbit-web/src/components/admin/CandidateTable.tsx`
- Create: `devorbit-web/src/components/admin/ApprovedRepoTable.tsx`
- Create: `devorbit-web/src/router.tsx`

### New mobile app files to create

- Create: `devorbit-mobile/settings.gradle.kts`
- Create: `devorbit-mobile/build.gradle.kts`
- Create: `devorbit-mobile/app/build.gradle.kts`
- Create: `devorbit-mobile/app/src/main/AndroidManifest.xml`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/MainActivity.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/network/ApiService.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/network/NetworkModule.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/model/CourseSummary.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/model/RepoSummary.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/model/TechStack.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/repository/DevOrbitRepository.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/CourseListScreen.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/CourseDetailScreen.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/RepoListSection.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/RepoFilterSheet.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/RepoDetailScreen.kt`

### Plan and docs files

- Create: `docs/superpowers/plans/2026-04-29-devorbit-mvp-implementation.md`

## Task 1: Prepare Backend Dependencies And Environment Config

**Files:**
- Modify: `devorbit-api/pom.xml`
- Modify: `devorbit-api/src/main/resources/application.yaml`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/DevorbitApiApplicationTests.java`

- [ ] **Step 1: Write the failing configuration smoke test**

```java
@SpringBootTest(properties = {
    "app.jwt.secret=test-secret-key-test-secret-key",
    "app.github.token=test-token"
})
class DevorbitApiApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }
}
```

- [ ] **Step 2: Run test to verify current setup behavior**

Run: `./mvnw test -Dtest=DevorbitApiApplicationTests`
Expected: Either PASS on current context test or FAIL after adding stricter config requirements.

- [ ] **Step 3: Add required backend dependencies**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 4: Replace hardcoded config with env-based placeholders**

```yaml
spring:
  application:
    name: devorbit-api
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/devorbit_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

app:
  jwt:
    secret: ${JWT_SECRET:devorbit-dev-secret-key-devorbit-dev-secret-key}
    expiration-minutes: ${JWT_EXPIRATION_MINUTES:120}
  github:
    token: ${GITHUB_TOKEN:}

server:
  port: ${SERVER_PORT:8080}
```

- [ ] **Step 5: Run test suite and confirm context loads**

Run: `./mvnw test -Dtest=DevorbitApiApplicationTests`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add pom.xml src/main/resources/application.yaml src/test/java/vn/edu/uit/devorbit_api/DevorbitApiApplicationTests.java
git commit -m "chore: prepare backend env config"
```

## Task 2: Expand Core Entities And Repositories

**Files:**
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/Course.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/GithubRepo.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/CourseRepository.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/GithubRepoRepository.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/AdminUser.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/RepoCandidate.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/RepoCandidateStatus.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/TechStack.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/AdminUserRepository.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/RepoCandidateRepository.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/TechStackRepository.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/service/RepoCandidateServiceTest.java`

- [ ] **Step 1: Write the failing repository and entity test shell**

```java
@SpringBootTest(properties = {
    "app.jwt.secret=test-secret-key-test-secret-key",
    "app.github.token=test-token"
})
class RepoCandidateServiceTest {

    @Autowired
    private RepoCandidateRepository repoCandidateRepository;

    @Test
    void shouldStoreNewCandidateStatus() {
        RepoCandidate candidate = RepoCandidate.builder()
            .githubUrl("https://github.com/example/repo")
            .githubOwner("example")
            .githubName("repo")
            .status(RepoCandidateStatus.NEW)
            .build();

        RepoCandidate saved = repoCandidateRepository.save(candidate);

        assertThat(saved.getStatus()).isEqualTo(RepoCandidateStatus.NEW);
    }
}
```

- [ ] **Step 2: Run test to verify missing classes fail compilation**

Run: `./mvnw test -Dtest=RepoCandidateServiceTest`
Expected: FAIL with missing entity or repository classes.

- [ ] **Step 3: Create new entities and repositories**

```java
public enum RepoCandidateStatus {
    NEW,
    APPROVED,
    REJECTED
}
```

```java
@Entity
@Table(name = "admin_users")
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active;
}
```

```java
@Entity
@Table(name = "repo_candidates")
public class RepoCandidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scanQuery;
    private String githubOwner;
    private String githubName;

    @Column(nullable = false)
    private String githubUrl;

    @Enumerated(EnumType.STRING)
    private RepoCandidateStatus status;
}
```

```java
@Repository
public interface RepoCandidateRepository extends JpaRepository<RepoCandidate, Long> {
    List<RepoCandidate> findByStatus(RepoCandidateStatus status);
}
```

- [ ] **Step 4: Extend existing `Course` and `GithubRepo` entities minimally for MVP**

```java
@Column(name = "is_active", nullable = false)
@Builder.Default
private boolean active = true;

@Column(name = "description", columnDefinition = "TEXT")
private String description;
```

```java
@Column(name = "display_name", length = 255)
private String displayName;

@Column(name = "primary_language", length = 100)
private String primaryLanguage;

@Column(name = "is_active", nullable = false)
@Builder.Default
private boolean active = true;
```

- [ ] **Step 5: Run entity test again**

Run: `./mvnw test -Dtest=RepoCandidateServiceTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/vn/edu/uit/devorbit_api/entity src/main/java/vn/edu/uit/devorbit_api/repository src/test/java/vn/edu/uit/devorbit_api/service/RepoCandidateServiceTest.java
git commit -m "feat: add core mvp entities"
```

## Task 3: Add DTO Layer And Public Read API Contract

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/publicapi/CourseSummaryResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/publicapi/CourseDetailResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/publicapi/RepoSummaryResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/publicapi/TechStackResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicCourseController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicRepoController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicTechStackController.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CourseService.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubRepoService.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/GithubRepoRepository.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/PublicCourseControllerTest.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/PublicRepoControllerTest.java`

- [ ] **Step 1: Write the failing public course controller test**

```java
@WebMvcTest(PublicCourseController.class)
class PublicCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Test
    void shouldReturnCourseSummaries() throws Exception {
        when(courseService.getActiveCourseSummaries()).thenReturn(List.of(
            new CourseSummaryResponse(1L, "SE101", "Nhap mon CNPM")
        ));

        mockMvc.perform(get("/api/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("SE101"));
    }
}
```

- [ ] **Step 2: Run public controller test to verify endpoint is missing**

Run: `./mvnw test -Dtest=PublicCourseControllerTest`
Expected: FAIL with missing controller or service method.

- [ ] **Step 3: Add DTOs and public controllers**

```java
public record CourseSummaryResponse(Long id, String code, String name) {
}
```

```java
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class PublicCourseController {
    private final CourseService courseService;

    @GetMapping
    public List<CourseSummaryResponse> getCourses() {
        return courseService.getActiveCourseSummaries();
    }
}
```

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicRepoController {
    private final GithubRepoService githubRepoService;

    @GetMapping("/courses/{courseId}/repos")
    public List<RepoSummaryResponse> getReposByCourse(@PathVariable Long courseId) {
        return githubRepoService.getApprovedReposByCourse(courseId);
    }
}
```

- [ ] **Step 4: Implement service mapping methods and repository queries**

```java
public List<CourseSummaryResponse> getActiveCourseSummaries() {
    return courseRepository.findByActiveTrue().stream()
        .map(course -> new CourseSummaryResponse((long) course.getId(), course.getMaMH(), course.getTenMH()))
        .toList();
}
```

```java
List<GithubRepo> findByCourseIdAndActiveTrue(Long courseId);
```

- [ ] **Step 5: Run public API tests**

Run: `./mvnw test -Dtest=PublicCourseControllerTest,PublicRepoControllerTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/vn/edu/uit/devorbit_api/dto src/main/java/vn/edu/uit/devorbit_api/controller/PublicCourseController.java src/main/java/vn/edu/uit/devorbit_api/controller/PublicRepoController.java src/main/java/vn/edu/uit/devorbit_api/controller/PublicTechStackController.java src/main/java/vn/edu/uit/devorbit_api/service src/main/java/vn/edu/uit/devorbit_api/repository src/test/java/vn/edu/uit/devorbit_api/controller/PublicCourseControllerTest.java src/test/java/vn/edu/uit/devorbit_api/controller/PublicRepoControllerTest.java
git commit -m "feat: add public mvp api contract"
```

## Task 4: Seed MVP Admin User And Subject Data

**Files:**
- Create: `devorbit-api/src/main/resources/data.sql`
- Modify: `devorbit-api/src/main/resources/application.yaml`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/DevorbitApiApplicationTests.java`

- [ ] **Step 1: Write the failing seed-data verification test**

```java
@SpringBootTest(properties = {
    "app.jwt.secret=test-secret-key-test-secret-key",
    "app.github.token=test-token"
})
class DevorbitApiApplicationTests {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldSeedAtLeastOneCourse() {
        assertThat(courseRepository.count()).isGreaterThan(0);
    }
}
```

- [ ] **Step 2: Run the test to confirm seed data is currently absent**

Run: `./mvnw test -Dtest=DevorbitApiApplicationTests`
Expected: FAIL with zero records unless existing local DB happens to contain data.

- [ ] **Step 3: Enable SQL initialization and add starter seed data**

```yaml
spring:
  sql:
    init:
      mode: always
```
```

```sql
insert into courses (mamh, tenmh, sotc, lt, th, loaimonhoc, is_active)
values
('SE101', 'Nhap mon Cong nghe phan mem', 3, 3, 0, 'Bat buoc', true),
('IT007', 'He dieu hanh', 4, 3, 1, 'Bat buoc', true);
```

- [ ] **Step 4: Seed one admin account with a known bcrypt hash**

```sql
insert into admin_users (username, password_hash, active)
values ('admin', '$2a$10$wH4lS0i2a5R1N3P0mK2hjuj0KxG9H6dE3qX9xF8k3VJ7pWmQf6y2m', true);
```

- [ ] **Step 5: Re-run seed test**

Run: `./mvnw test -Dtest=DevorbitApiApplicationTests`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/resources/application.yaml src/main/resources/data.sql src/test/java/vn/edu/uit/devorbit_api/DevorbitApiApplicationTests.java
git commit -m "feat: seed mvp bootstrap data"
```

## Task 5: Add Admin Authentication With JWT

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/JwtProperties.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/LoginRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/LoginResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/JwtService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/AdminAuthService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminAuthController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/exception/UnauthorizedException.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/exception/ApiExceptionHandler.java`
- Create: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/AdminAuthControllerTest.java`

- [ ] **Step 1: Write the failing admin login test**

```java
@WebMvcTest(AdminAuthController.class)
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAuthService adminAuthService;

    @Test
    void shouldReturnJwtForValidAdmin() throws Exception {
        when(adminAuthService.login(any())).thenReturn(new LoginResponse("token-value"));

        mockMvc.perform(post("/api/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"admin","password":"admin123"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("token-value"));
    }
}
```

- [ ] **Step 2: Run the test to verify the auth stack does not exist yet**

Run: `./mvnw test -Dtest=AdminAuthControllerTest`
Expected: FAIL

- [ ] **Step 3: Implement auth DTOs, service, and controller**

```java
public record LoginRequest(String username, String password) {
}
```

```java
public record LoginResponse(String token) {
}
```

```java
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return adminAuthService.login(request);
    }
}
```

- [ ] **Step 4: Implement Spring Security for public and admin routes**

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/admin/auth/login").permitAll()
            .requestMatchers("/api/admin/**").authenticated()
            .requestMatchers("/api/**").permitAll()
            .anyRequest().permitAll()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
}
```

- [ ] **Step 5: Run admin auth tests**

Run: `./mvnw test -Dtest=AdminAuthControllerTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/vn/edu/uit/devorbit_api/config src/main/java/vn/edu/uit/devorbit_api/dto/admin src/main/java/vn/edu/uit/devorbit_api/service/JwtService.java src/main/java/vn/edu/uit/devorbit_api/service/AdminAuthService.java src/main/java/vn/edu/uit/devorbit_api/controller/AdminAuthController.java src/main/java/vn/edu/uit/devorbit_api/exception src/test/java/vn/edu/uit/devorbit_api/controller/AdminAuthControllerTest.java
git commit -m "feat: add admin jwt auth"
```

## Task 6: Implement Admin Course Management API

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/AdminCourseUpsertRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminCourseController.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CourseService.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/CourseRepository.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/exception/NotFoundException.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/AdminCourseControllerTest.java`

- [ ] **Step 1: Write the failing admin course create test**

```java
@WebMvcTest(AdminCourseController.class)
class AdminCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Test
    void shouldCreateCourse() throws Exception {
        mockMvc.perform(post("/api/admin/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code":"SE104","name":"Nhap mon Test","credits":3}
                """))
            .andExpect(status().isOk());
    }
}
```

- [ ] **Step 2: Run the test to confirm missing controller**

Run: `./mvnw test -Dtest=AdminCourseControllerTest`
Expected: FAIL

- [ ] **Step 3: Implement request DTO and admin controller methods**

```java
public record AdminCourseUpsertRequest(
    String code,
    String name,
    Integer credits,
    Integer lectureHours,
    Integer practiceHours,
    String subjectType,
    String description
) {
}
```

```java
@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {
    private final CourseService courseService;

    @PostMapping
    public CourseDetailResponse create(@RequestBody AdminCourseUpsertRequest request) {
        return courseService.createCourse(request);
    }
}
```

- [ ] **Step 4: Add service methods for create, update, deactivate**

```java
public CourseDetailResponse createCourse(AdminCourseUpsertRequest request) {
    Course course = Course.builder()
        .maMH(request.code())
        .tenMH(request.name())
        .soTC(request.credits())
        .lt(request.lectureHours())
        .th(request.practiceHours())
        .loaiMonHoc(request.subjectType())
        .description(request.description())
        .active(true)
        .build();

    return mapToDetail(courseRepository.save(course));
}
```

- [ ] **Step 5: Run admin course test**

Run: `./mvnw test -Dtest=AdminCourseControllerTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/vn/edu/uit/devorbit_api/dto/admin/AdminCourseUpsertRequest.java src/main/java/vn/edu/uit/devorbit_api/controller/AdminCourseController.java src/main/java/vn/edu/uit/devorbit_api/service/CourseService.java src/main/java/vn/edu/uit/devorbit_api/repository/CourseRepository.java src/main/java/vn/edu/uit/devorbit_api/exception/NotFoundException.java src/test/java/vn/edu/uit/devorbit_api/controller/AdminCourseControllerTest.java
git commit -m "feat: add admin course management"
```

## Task 7: Implement GitHub Scan Service And Candidate Persistence

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/GithubProperties.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/GithubScanRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubScanService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminGithubController.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/RepoCandidateRepository.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/exception/BadRequestException.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/service/RepoCandidateServiceTest.java`

- [ ] **Step 1: Write the failing scan persistence test**

```java
@Test
void shouldPersistNewCandidatesFromScan() {
    GithubScanRequest request = new GithubScanRequest(1L, "SE101 UIT");

    when(githubScanService.scan(any())).thenReturn(List.of(
        new RepoCandidateResponse(
            1L,
            "example",
            "repo",
            "https://github.com/example/repo",
            "NEW"
        )
    ));

    List<RepoCandidateResponse> results = githubScanService.scan(request);

    assertThat(results).hasSize(1);
}
```

- [ ] **Step 2: Run scan-related test to verify missing service contracts**

Run: `./mvnw test -Dtest=RepoCandidateServiceTest`
Expected: FAIL or compile error depending on test layout.

- [ ] **Step 3: Add request DTO and GitHub scan service skeleton**

```java
public record GithubScanRequest(Long courseId, String query) {
}
```

```java
@Service
@RequiredArgsConstructor
public class GithubScanService {
    private final RepoCandidateRepository repoCandidateRepository;

    public List<RepoCandidateResponse> scan(GithubScanRequest request) {
        return List.of();
    }
}
```

- [ ] **Step 4: Implement WebClient-based GitHub search and candidate upsert**

```java
WebClient webClient = WebClient.builder()
    .baseUrl("https://api.github.com")
    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubProperties.token())
    .build();
```

```java
RepoCandidate candidate = RepoCandidate.builder()
    .course(course)
    .scanQuery(request.query())
    .githubOwner(owner)
    .githubName(name)
    .githubUrl(url)
    .status(RepoCandidateStatus.NEW)
    .build();
```

- [ ] **Step 5: Run scan service tests**

Run: `./mvnw test -Dtest=RepoCandidateServiceTest`
Expected: PASS for persistence-focused checks

- [ ] **Step 6: Commit**

```bash
git add src/main/java/vn/edu/uit/devorbit_api/config/GithubProperties.java src/main/java/vn/edu/uit/devorbit_api/dto/admin/GithubScanRequest.java src/main/java/vn/edu/uit/devorbit_api/service/GithubScanService.java src/main/java/vn/edu/uit/devorbit_api/controller/AdminGithubController.java src/main/java/vn/edu/uit/devorbit_api/repository/RepoCandidateRepository.java src/main/java/vn/edu/uit/devorbit_api/exception/BadRequestException.java src/test/java/vn/edu/uit/devorbit_api/service/RepoCandidateServiceTest.java
git commit -m "feat: add github candidate scanning"
```

## Task 8: Implement Candidate Review And Approval Flow

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/CandidateReviewRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/RepoCandidateResponse.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/RepoCandidateService.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminRepoCandidateController.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubRepoService.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/AdminRepoCandidateControllerTest.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/service/RepoCandidateServiceTest.java`

- [ ] **Step 1: Write the failing approve-candidate controller test**

```java
@WebMvcTest(AdminRepoCandidateController.class)
class AdminRepoCandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepoCandidateService repoCandidateService;

    @Test
    void shouldApproveCandidate() throws Exception {
        when(repoCandidateService.approveCandidate(eq(10L), any()))
            .thenReturn(new RepoCandidateResponse(10L, "repo", "APPROVED"));

        mockMvc.perform(post("/api/admin/repo-candidates/10/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"description":"Cleaned up","techStacks":["java","spring-boot"]}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
```

- [ ] **Step 2: Run the test to verify missing controller and service methods**

Run: `./mvnw test -Dtest=AdminRepoCandidateControllerTest`
Expected: FAIL

- [ ] **Step 3: Implement candidate DTOs, service, and controller**

```java
public record CandidateReviewRequest(
    String description,
    List<String> techStacks,
    String reviewNote
) {
}
```

```java
@RestController
@RequestMapping("/api/admin/repo-candidates")
@RequiredArgsConstructor
public class AdminRepoCandidateController {
    private final RepoCandidateService repoCandidateService;

    @PostMapping("/{candidateId}/approve")
    public RepoCandidateResponse approve(
        @PathVariable Long candidateId,
        @RequestBody CandidateReviewRequest request
    ) {
        return repoCandidateService.approveCandidate(candidateId, request);
    }
}
```

- [ ] **Step 4: Implement approval logic that upserts into `github_repos`**

```java
public RepoCandidateResponse approveCandidate(Long candidateId, CandidateReviewRequest request) {
    RepoCandidate candidate = repoCandidateRepository.findById(candidateId)
        .orElseThrow(() -> new NotFoundException("Candidate not found"));

    GithubRepo repo = githubRepoRepository.findByGithubUrlAndCourseId(candidate.getGithubUrl(), candidate.getCourse().getId())
        .orElseGet(GithubRepo::new);

    repo.setGithubUrl(candidate.getGithubUrl());
    repo.setRepoName(candidate.getGithubName());
    repo.setDisplayName(candidate.getGithubName());
    repo.setDescription(request.description());
    repo.setActive(true);

    githubRepoRepository.save(repo);
    candidate.setStatus(RepoCandidateStatus.APPROVED);
    repoCandidateRepository.save(candidate);

    return RepoCandidateResponse.from(candidate);
}
```

- [ ] **Step 5: Run candidate approval tests**

Run: `./mvnw test -Dtest=AdminRepoCandidateControllerTest,RepoCandidateServiceTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/vn/edu/uit/devorbit_api/dto/admin/CandidateReviewRequest.java src/main/java/vn/edu/uit/devorbit_api/dto/admin/RepoCandidateResponse.java src/main/java/vn/edu/uit/devorbit_api/service/RepoCandidateService.java src/main/java/vn/edu/uit/devorbit_api/controller/AdminRepoCandidateController.java src/main/java/vn/edu/uit/devorbit_api/service/GithubRepoService.java src/test/java/vn/edu/uit/devorbit_api/controller/AdminRepoCandidateControllerTest.java src/test/java/vn/edu/uit/devorbit_api/service/RepoCandidateServiceTest.java
git commit -m "feat: add candidate approval workflow"
```

## Task 9: Implement Approved Repo Admin Management And Public Filters

**Files:**
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/admin/ApprovedRepoUpdateRequest.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminRepoController.java`
- Create: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/TechStackService.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubRepoService.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicRepoController.java`
- Modify: `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicTechStackController.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/PublicRepoControllerTest.java`

- [ ] **Step 1: Write the failing repo filter test**

```java
@Test
void shouldFilterReposByTechStackQueryParam() throws Exception {
    when(githubRepoService.getApprovedReposByCourseAndTechStack(1L, "java"))
        .thenReturn(List.of(new RepoSummaryResponse(1L, "repo", "java")));

    mockMvc.perform(get("/api/courses/1/repos").param("techStack", "java"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].primaryLanguage").value("java"));
}
```

- [ ] **Step 2: Run the test to verify filter API is incomplete**

Run: `./mvnw test -Dtest=PublicRepoControllerTest`
Expected: FAIL

- [ ] **Step 3: Add admin approved-repo update endpoint**

```java
public record ApprovedRepoUpdateRequest(
    String displayName,
    String description,
    List<String> techStacks,
    Boolean active
) {
}
```

```java
@PutMapping("/{repoId}")
public RepoSummaryResponse updateRepo(@PathVariable Long repoId, @RequestBody ApprovedRepoUpdateRequest request) {
    return githubRepoService.updateApprovedRepo(repoId, request);
}
```

- [ ] **Step 4: Add public filter and tech-stack listing support**

```java
@GetMapping("/courses/{courseId}/repos")
public List<RepoSummaryResponse> getReposByCourse(
    @PathVariable Long courseId,
    @RequestParam(required = false) String techStack
) {
    if (techStack != null && !techStack.isBlank()) {
        return githubRepoService.getApprovedReposByCourseAndTechStack(courseId, techStack);
    }
    return githubRepoService.getApprovedReposByCourse(courseId);
}
```

- [ ] **Step 5: Run repo public API tests**

Run: `./mvnw test -Dtest=PublicRepoControllerTest`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/vn/edu/uit/devorbit_api/dto/admin/ApprovedRepoUpdateRequest.java src/main/java/vn/edu/uit/devorbit_api/controller/AdminRepoController.java src/main/java/vn/edu/uit/devorbit_api/service/TechStackService.java src/main/java/vn/edu/uit/devorbit_api/service/GithubRepoService.java src/main/java/vn/edu/uit/devorbit_api/controller/PublicRepoController.java src/main/java/vn/edu/uit/devorbit_api/controller/PublicTechStackController.java src/test/java/vn/edu/uit/devorbit_api/controller/PublicRepoControllerTest.java
git commit -m "feat: add repo admin management and filters"
```

## Task 10: Scaffold The React Web App

**Files:**
- Create: `devorbit-web/package.json`
- Create: `devorbit-web/vite.config.ts`
- Create: `devorbit-web/tsconfig.json`
- Create: `devorbit-web/src/main.tsx`
- Create: `devorbit-web/src/App.tsx`
- Create: `devorbit-web/src/router.tsx`
- Create: `devorbit-web/src/lib/api.ts`
- Create: `devorbit-web/src/types/api.ts`

- [ ] **Step 1: Create the Vite React TypeScript app shell**

```json
{
  "name": "devorbit-web",
  "private": true,
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build"
  },
  "dependencies": {
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "react-router-dom": "^7.0.0"
  }
}
```

- [ ] **Step 2: Run install to verify the app shell is valid**

Run: `npm install`
Expected: dependencies installed successfully in `devorbit-web`

- [ ] **Step 3: Add shared API client and route skeleton**

```ts
export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export async function apiGet<T>(path: string): Promise<T> {
  const response = await fetch(`${apiBaseUrl}${path}`);
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }
  return response.json() as Promise<T>;
}
```

```tsx
export function App() {
  return <RouterProvider router={router} />;
}
```

- [ ] **Step 4: Run the web build**

Run: `npm run build`
Expected: PASS with empty shell app

- [ ] **Step 5: Commit**

```bash
git add package.json vite.config.ts tsconfig.json src
git commit -m "feat: scaffold web app"
```

## Task 11: Build Student Web Pages

**Files:**
- Create: `devorbit-web/src/pages/student/HomePage.tsx`
- Create: `devorbit-web/src/pages/student/CourseListPage.tsx`
- Create: `devorbit-web/src/pages/student/CourseDetailPage.tsx`
- Create: `devorbit-web/src/components/student/CourseCard.tsx`
- Create: `devorbit-web/src/components/student/RepoFilterBar.tsx`
- Create: `devorbit-web/src/components/student/RepoCard.tsx`
- Modify: `devorbit-web/src/router.tsx`
- Modify: `devorbit-web/src/types/api.ts`

- [ ] **Step 1: Implement API types for student flows**

```ts
export type CourseSummary = {
  id: number;
  code: string;
  name: string;
};

export type RepoSummary = {
  id: number;
  displayName: string;
  description: string;
  githubUrl: string;
  primaryLanguage: string;
  techStacks: string[];
  stars: number;
};
```

- [ ] **Step 2: Implement the course list page**

```tsx
export function CourseListPage() {
  const [courses, setCourses] = useState<CourseSummary[]>([]);

  useEffect(() => {
    void apiGet<CourseSummary[]>("/api/courses").then(setCourses);
  }, []);

  return <div>{courses.map(course => <CourseCard key={course.id} course={course} />)}</div>;
}
```

- [ ] **Step 3: Implement the course detail and repo filtering page**

```tsx
export function CourseDetailPage() {
  const { courseId } = useParams();
  const [techStack, setTechStack] = useState("");
  const [repos, setRepos] = useState<RepoSummary[]>([]);

  useEffect(() => {
    const query = techStack ? `?techStack=${encodeURIComponent(techStack)}` : "";
    void apiGet<RepoSummary[]>(`/api/courses/${courseId}/repos${query}`).then(setRepos);
  }, [courseId, techStack]);

  return <div>{repos.map(repo => <RepoCard key={repo.id} repo={repo} />)}</div>;
}
```

- [ ] **Step 4: Wire routes and verify build**

Run: `npm run build`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/pages/student src/components/student src/router.tsx src/types/api.ts
git commit -m "feat: build student web flow"
```

## Task 12: Build Admin Web Auth And Course Management Pages

**Files:**
- Create: `devorbit-web/src/lib/auth.ts`
- Create: `devorbit-web/src/pages/admin/LoginPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminDashboardPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminCoursesPage.tsx`
- Create: `devorbit-web/src/components/admin/CourseFormDialog.tsx`
- Modify: `devorbit-web/src/router.tsx`

- [ ] **Step 1: Implement a minimal admin auth helper**

```ts
const ADMIN_TOKEN_KEY = "devorbit-admin-token";

export function saveAdminToken(token: string) {
  localStorage.setItem(ADMIN_TOKEN_KEY, token);
}

export function getAdminToken() {
  return localStorage.getItem(ADMIN_TOKEN_KEY);
}
```

- [ ] **Step 2: Implement the admin login page**

```tsx
export function LoginPage() {
  const navigate = useNavigate();

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const response = await fetch(`${apiBaseUrl}/api/admin/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: "admin", password: "admin123" })
    });
    const data = await response.json();
    saveAdminToken(data.token);
    navigate("/admin");
  }

  return <form onSubmit={handleSubmit}><button type="submit">Login</button></form>;
}
```

- [ ] **Step 3: Implement admin course table and create dialog**

```tsx
export function AdminCoursesPage() {
  const [courses, setCourses] = useState<CourseSummary[]>([]);

  useEffect(() => {
    void fetch(`${apiBaseUrl}/api/admin/courses`, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    }).then(response => response.json()).then(setCourses);
  }, []);

  return <div>{courses.map(course => <div key={course.id}>{course.code}</div>)}</div>;
}
```

- [ ] **Step 4: Run the web build**

Run: `npm run build`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/lib/auth.ts src/pages/admin/LoginPage.tsx src/pages/admin/AdminDashboardPage.tsx src/pages/admin/AdminCoursesPage.tsx src/components/admin/CourseFormDialog.tsx src/router.tsx
git commit -m "feat: add admin web auth and course pages"
```

## Task 13: Build Admin GitHub Scan And Candidate Review Pages

**Files:**
- Create: `devorbit-web/src/pages/admin/AdminScanPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminCandidatesPage.tsx`
- Create: `devorbit-web/src/pages/admin/AdminReposPage.tsx`
- Create: `devorbit-web/src/components/admin/ScanForm.tsx`
- Create: `devorbit-web/src/components/admin/CandidateTable.tsx`
- Create: `devorbit-web/src/components/admin/ApprovedRepoTable.tsx`
- Modify: `devorbit-web/src/types/api.ts`
- Modify: `devorbit-web/src/router.tsx`

- [ ] **Step 1: Add admin API types for candidates**

```ts
export type RepoCandidate = {
  id: number;
  githubName: string;
  githubOwner: string;
  githubUrl: string;
  status: "NEW" | "APPROVED" | "REJECTED";
  description: string;
};
```

- [ ] **Step 2: Implement scan form page**

```tsx
export function AdminScanPage() {
  async function handleScan(courseId: number, query: string) {
    await fetch(`${apiBaseUrl}/api/admin/github/scan`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${getAdminToken()}`
      },
      body: JSON.stringify({ courseId, query })
    });
  }

  return <ScanForm onSubmit={handleScan} />;
}
```

- [ ] **Step 3: Implement candidate review page with approve and reject actions**

```tsx
export function AdminCandidatesPage() {
  const [candidates, setCandidates] = useState<RepoCandidate[]>([]);

  useEffect(() => {
    void fetch(`${apiBaseUrl}/api/admin/repo-candidates`, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    }).then(response => response.json()).then(setCandidates);
  }, []);

  return <CandidateTable candidates={candidates} />;
}
```

- [ ] **Step 4: Run the web build**

Run: `npm run build`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/pages/admin/AdminScanPage.tsx src/pages/admin/AdminCandidatesPage.tsx src/pages/admin/AdminReposPage.tsx src/components/admin/ScanForm.tsx src/components/admin/CandidateTable.tsx src/components/admin/ApprovedRepoTable.tsx src/types/api.ts src/router.tsx
git commit -m "feat: add admin scan and review pages"
```

## Task 14: Scaffold The Kotlin Android App

**Files:**
- Create: `devorbit-mobile/settings.gradle.kts`
- Create: `devorbit-mobile/build.gradle.kts`
- Create: `devorbit-mobile/app/build.gradle.kts`
- Create: `devorbit-mobile/app/src/main/AndroidManifest.xml`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/MainActivity.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/network/ApiService.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/network/NetworkModule.kt`

- [ ] **Step 1: Create the Android app shell with Retrofit and Compose**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "vn.edu.uit.devorbit.mobile"
    compileSdk = 35
}
```

- [ ] **Step 2: Add Retrofit API interface**

```kotlin
interface ApiService {
    @GET("/api/courses")
    suspend fun getCourses(): List<CourseSummary>

    @GET("/api/courses/{courseId}/repos")
    suspend fun getRepos(@Path("courseId") courseId: Long): List<RepoSummary>
}
```

- [ ] **Step 3: Create a simple `MainActivity` Compose host**

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("DevOrbit")
        }
    }
}
```

- [ ] **Step 4: Run the Android build**

Run: `./gradlew assembleDebug`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add settings.gradle.kts build.gradle.kts app
git commit -m "feat: scaffold android app"
```

## Task 15: Build Student Mobile Screens

**Files:**
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/model/CourseSummary.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/model/RepoSummary.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/model/TechStack.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/repository/DevOrbitRepository.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/CourseListScreen.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/CourseDetailScreen.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/RepoListSection.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/RepoFilterSheet.kt`
- Create: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/ui/RepoDetailScreen.kt`
- Modify: `devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/MainActivity.kt`

- [ ] **Step 1: Add Kotlin models and repository wrapper**

```kotlin
data class CourseSummary(
    val id: Long,
    val code: String,
    val name: String
)

data class RepoSummary(
    val id: Long,
    val displayName: String,
    val description: String,
    val githubUrl: String,
    val primaryLanguage: String,
    val techStacks: List<String>
)
```

- [ ] **Step 2: Implement course list screen**

```kotlin
@Composable
fun CourseListScreen(courses: List<CourseSummary>, onCourseClick: (CourseSummary) -> Unit) {
    LazyColumn {
        items(courses) { course ->
            Text(text = "${course.code} - ${course.name}")
        }
    }
}
```

- [ ] **Step 3: Implement repo list and detail screens with external browser open**

```kotlin
@Composable
fun RepoDetailScreen(repo: RepoSummary, onOpenGithub: (String) -> Unit) {
    Column {
        Text(repo.displayName)
        Text(repo.description)
        Button(onClick = { onOpenGithub(repo.githubUrl) }) {
            Text("Open GitHub")
        }
    }
}
```

- [ ] **Step 4: Run Android build**

Run: `./gradlew assembleDebug`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/vn/edu/uit/devorbit/mobile/model app/src/main/java/vn/edu/uit/devorbit/mobile/repository app/src/main/java/vn/edu/uit/devorbit/mobile/ui app/src/main/java/vn/edu/uit/devorbit/mobile/MainActivity.kt
git commit -m "feat: build student mobile flow"
```

## Task 16: End-To-End Verification And Deployment Readiness

**Files:**
- Modify: `devorbit-api/src/main/resources/application.yaml`
- Modify: `devorbit-web/package.json`
- Modify: `devorbit-mobile/app/build.gradle.kts`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/PublicCourseControllerTest.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/PublicRepoControllerTest.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/AdminAuthControllerTest.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/controller/AdminRepoCandidateControllerTest.java`
- Test: `devorbit-api/src/test/java/vn/edu/uit/devorbit_api/service/RepoCandidateServiceTest.java`

- [ ] **Step 1: Run the complete backend test suite**

Run: `./mvnw test`
Expected: PASS

- [ ] **Step 2: Run the production web build**

Run: `npm run build`
Expected: PASS

- [ ] **Step 3: Run the Android debug build**

Run: `./gradlew assembleDebug`
Expected: PASS

- [ ] **Step 4: Smoke-test the local stack manually**

Run: `./mvnw spring-boot:run`
Expected: backend starts on `http://localhost:8080`

Run: `npm run dev`
Expected: web starts on Vite local URL and can load courses from backend

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "chore: verify devorbit mvp readiness"
```

## Self-Review Checklist

- Spec coverage: backend public browsing, admin auth, admin course management, GitHub scan, candidate review, approved repo management, web student flow, web admin flow, and mobile student flow are all covered by dedicated tasks.
- Placeholder scan: no `TBD`, no "implement later", and each task includes files, commands, and concrete code snippets.
- Type consistency: plan uses `CourseSummaryResponse`, `RepoSummaryResponse`, `GithubScanRequest`, `CandidateReviewRequest`, and `ApprovedRepoUpdateRequest` consistently across controllers, services, and clients.
- Fixes applied during review: admin course test file path was corrected, Spring Security matcher order was fixed so `/api/admin/**` stays protected, and the scan test example now uses a concrete `RepoCandidateResponse` constructor instead of an undefined builder.
