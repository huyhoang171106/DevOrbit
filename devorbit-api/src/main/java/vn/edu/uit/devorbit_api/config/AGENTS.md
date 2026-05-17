<!-- Parent: ../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# config

## Purpose
Spring Boot configuration classes for security, authentication, external API integrations (GitHub), application settings, and data initialization. Centralizes all configuration logic separate from business code.

## Key Files
| File | Description |
|------|-------------|
| `SecurityConfig.java` | Spring Security configuration with JWT filter chain and role-based access (ADMIN, STUDENT) |
| `JwtAuthenticationFilter.java` | Once-per-request JWT token processing filter for `/api/admin/**` and `/api/student/**` routes |
| `JwtProperties.java` | JWT configuration properties binding (`jwt.secret`, `jwt.expiration`) |
| `GithubProperties.java` | GitHub API configuration properties (`github.token`, `github.base-url`) |
| `GithubClientConfig.java` | WebClient-based GitHub API client bean configuration |
| `OpenApiConfig.java` | Swagger/OpenAPI documentation setup with security scheme |
| `JacksonConfig.java` | Jackson ObjectMapper customizations (date formats, naming strategies) |
| `ElectiveGroupConfig.java` | Elective course group configuration — defines elective group mappings based on curriculum constants |
| `RepoCandidateDataInitializer.java` | `CommandLineRunner` that seeds initial repo candidate data on application startup |

## For AI Agents

### Working In This Directory
- Use `@Configuration` annotations
- Define beans with `@Bean` methods
- Handle cross-cutting concerns (security, serialization)
- Configure external service integrations (GitHub, Supabase)
- Set up security policies and filter chains

### Testing Requirements
- Test configuration loading under different profiles
- Verify security filter chain ordering
- Test external API connectivity (GitHub WebClient)
- Validate `@ConfigurationProperties` binding

### Common Patterns
- `@Configuration` classes
- `@Bean` method definitions
- `@EnableWebSecurity` for security
- `@ConfigurationProperties` with `@EnableConfigurationProperties` for property binding
- `SecurityFilterChain` bean for HTTP security rules
- `OncePerRequestFilter` for JWT authentication filter
- `CommandLineRunner` for startup data initialization

## Dependencies

### Internal
- Property files in `../../../resources/application.yaml`
- Controllers that consume security configuration
- Services that use GitHub WebClient (`GithubRepoService`, `GithubScanService`)

### External
- Spring Security — Authentication framework
- JWT (jjwt 0.12.6) — Token handling
- GitHub API — External integration via WebClient
- OpenAPI/Swagger (springdoc 2.8.6) — API documentation
- Jackson — JSON serialization customization

<!-- MANUAL: -->
