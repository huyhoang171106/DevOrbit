# DevOrbit API — Architecture Guide for Beginners

## What is this?

This is the **backend server** for DevOrbit. It provides REST APIs that the
frontend (web and mobile) calls to get data, create accounts, search courses,
manage GitHub repos, generate roadmaps, and more.

Think of it as the **middleman** between the database and the user interface:

```
Browser / Mobile App
       ↓  HTTP requests (JSON)
DevOrbit API (this project)
       ↓  SQL queries
PostgreSQL Database
```

---

## The 3-Layer Architecture

Every request follows this pattern:

```
┌──────────────────────────────────────────────────┐
│  LAYER 1: CONTROLLER                              │
│  Receives HTTP request, calls service             │
│  Example: PublicCourseController                  │
├──────────────────────────────────────────────────┤
│  LAYER 2: SERVICE                                 │
│  Contains business logic, coordinates data        │
│  Example: CourseService                           │
├──────────────────────────────────────────────────┤
│  LAYER 3: REPOSITORY                              │
│  Talks to the database                            │
│  Example: CourseRepository                        │
└──────────────────────────────────────────────────┘
```

### Example: tracing a request

When you open the course list page in the browser:

1. **Browser** calls `GET /api/courses`
2. **Controller** `PublicCourseController.getCourses()` receives it
3. **Service** `CourseService.getActiveCourseSummaries()` processes the request
4. **Repository** `CourseRepository.findAllWithRepoCountSortedByRepoCount()` runs a SQL query
5. **Database** returns the data
6. The **response** flows back as JSON

---

## Package Map

```
src/main/java/vn/edu/uit/devorbit_api/
│
├── DevorbitApiApplication.java     ← Entry point (main method)
│
├── config/                         ← Setup files (Spring config)
│   ├── SecurityConfig.java         ← Login/authentication rules
│   ├── JwtProperties.java          ← JWT token settings
│   ├── GithubProperties.java       ← GitHub API keys
│   └── ...
│
├── controller/                     ← Layer 1: HTTP endpoints
│   ├── Public*Controller.java      ← Public APIs (no login needed)
│   ├── Admin*Controller.java       ← Admin APIs (login required)
│   ├── Student*Controller.java     ← Student APIs (login required)
│   └── HealthController.java       ← Health check endpoint
│
├── service/                        ← Layer 2: Business logic
│   ├── CourseService.java          ← Course operations
│   ├── GithubRepoService.java      ← GitHub repo operations
│   ├── StudentAuthService.java     ← Student login/register
│   └── ai/                         ← AI-powered features
│       ├── RoadmapGenerator.java   ← Generate learning roadmaps
│       ├── GraphQueryEngine.java   ← Query knowledge graph with AI
│       └── ...
│
├── repository/                     ← Layer 3: Database access
│   ├── CourseRepository.java       ← Course queries
│   ├── GithubRepoRepository.java   ← GitHub repo queries
│   └── ...
│
├── entity/                         ← Database table representations
│   ├── Course.java                 ← "courses" table
│   ├── GithubRepo.java             ← "github_repos" table
│   └── ...
│
├── dto/                            ← Data sent to/from the frontend
│   ├── publicapi/                  ← Response DTOs for public endpoints
│   ├── admin/                      ← Request/response DTOs for admin
│   └── student/                    ← DTOs for student features
│
├── exception/                      ← Error handling
│   ├── NotFoundException.java      ← HTTP 404
│   ├── BadRequestException.java    ← HTTP 400
│   └── ApiExceptionHandler.java    ← Catches all exceptions
│
├── constant/                       ← Shared constants
│   └── CurriculumConstants.java    ← Course curriculum data
│
└── event/                          ← Event system
    └── RelationshipChangedEvent.java
```

---

## Key Concepts for Beginners

### What is a Controller?
A class that handles HTTP requests. Each method maps to a URL endpoint.

- `@RestController` — marks this class as a controller
- `@RequestMapping("/api/courses")` — all endpoints start with this path
- `@GetMapping` — handles GET requests
- `@PostMapping` — handles POST requests

### What is a Service?
A class that contains **business logic**. Controllers call services, services
call repositories.

- `@Service` — marks this class as a service
- `@Transactional` — database operations happen inside a transaction

### What is a Repository?
An interface that **automatically** gets database access methods.
Spring Data JPA generates the SQL at runtime.

- `extends JpaRepository<EntityType, IdType>` — gives you save(), findById(), findAll(), etc.
- Custom queries use `@Query` with JPQL (looks like SQL but uses Java field names)

### What is an Entity?
A Java class that **represents a database table**. Each instance = one row.

- `@Entity` — marks this as a database entity
- `@Table(name = "courses")` — maps to the "courses" table
- `@Column(name = "mamh")` — maps to the "mamh" column
- `@Id` — primary key

### What is a DTO? (Data Transfer Object)
A simple object that carries data between the API and the frontend.
We don't send raw entities to the browser — we send DTOs instead.

- Records (`public record X(...)`) are used for DTOs
- They only contain the fields the frontend needs
- They hide internal database details

---

## Common Request Lifecycle

### Read (GET request)
```
Browser → Controller → Service → Repository → Database → Response
```

### Create (POST request)
```
Browser (with JSON body) → Controller → Service → Repository.save() → Response
```

### Update (PUT request)
```
Browser (with JSON body) → Controller → Service → findById() → update fields → save() → Response
```

### Delete (DELETE request)
```
Browser → Controller → Service → findById() → delete() → HTTP 204 (No Content)
```

---

## Authentication Flow

1. User sends login request (`POST /api/admin/auth/login`)
2. Server validates credentials
3. Server creates a **JWT token** (a signed string that proves identity)
4. For subsequent requests, the browser sends this token in the header:
   `Authorization: Bearer <token>`
5. `JwtAuthenticationFilter` checks the token on every request
6. If invalid/missing → HTTP 401 Unauthorized

---

## Error Handling Flow

1. A Service method throws an exception (e.g., `NotFoundException`)
2. The exception travels up through Controller
3. `ApiExceptionHandler` catches it
4. Returns a JSON error response with the right HTTP status code

---

## Validation Flow

1. The Controller method parameter has `@Valid` annotation
2. The DTO has validation annotations (`@NotBlank`, `@NotNull`, etc.)
3. If validation fails, `MethodArgumentNotValidException` is thrown
4. `ApiExceptionHandler` catches it and returns HTTP 400 with error details

---

## Tips for Contributors

- **Start from the controller**: look at `PublicCourseController` to see what
  endpoints exist, then trace into the service, then into the repository.
- **Read the entity** first if you're confused by field names — many use
  Vietnamese abbreviations with English explanations in the JavaDoc.
- **Run tests**: `mvnw.cmd test` — all tests should pass before you commit.
- **Use `@WebMvcTest`** for controller tests (mocks services, only tests HTTP layer).
- **Use `@SpringBootTest`** for integration tests (loads the full application).
