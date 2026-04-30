<!-- Parent: ../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# config

## Purpose
Spring Boot configuration classes for security, authentication, external API integrations, and application settings. Centralizes all configuration logic separate from business code.

## Key Files
| File | Description |
|------|-------------|
| `SecurityConfig.java` | Spring Security configuration with JWT |
| `JwtAuthenticationFilter.java` | JWT token processing filter |
| `JwtProperties.java` | JWT configuration properties |
| `GithubProperties.java` | GitHub API configuration |
| `OpenApiConfig.java` | Swagger/OpenAPI documentation setup |

## For AI Agents

### Working In This Directory
- Use @Configuration annotations
- Define beans with @Bean methods
- Handle cross-cutting concerns
- Configure external service integrations
- Set up security policies

### Testing Requirements
- Test configuration loading
- Verify security configurations
- Test external API connectivity
- Validate property binding

### Common Patterns
- @Configuration classes
- @Bean method definitions
- @EnableWebSecurity for security
- Property binding with @ConfigurationProperties
- Filter registration with FilterRegistrationBean

## Dependencies

### Internal
- Property files in `../../../resources/application.yaml`
- Controllers that use security

### External
- Spring Security - Authentication framework
- JWT libraries - Token handling
- GitHub API - External integration
- OpenAPI/Swagger - API documentation

<!-- MANUAL: -->