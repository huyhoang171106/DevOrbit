<!-- Parent: ../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# entity

## Purpose
JPA entity classes representing database tables and relationships in the DevOrbit application. Contains the core data model for courses, GitHub repositories, users, and related entities.

## Key Files
| File | Description |
|------|-------------|
| `Course.java` | Course entity with maMH, tenMH, credits, lecture, practice |
| `GithubRepo.java` | GitHub repository entity with metadata and relationships |
| `User.java` | User entity for authentication and authorization |
| `TechStack.java` | Technology stack classifications |

## For AI Agents

### Working In This Directory
- Use JPA annotations (@Entity, @Table, @Column)
- Define relationships with @OneToMany, @ManyToOne, etc.
- Include validation constraints
- Follow database naming conventions
- Use Lombok for boilerplate reduction

### Testing Requirements
- Test entity relationships
- Validate constraints
- Check database mapping
- Verify migration scripts

### Common Patterns
- @Entity annotation on classes
- @Id and @GeneratedValue for primary keys
- @Column for field mapping
- @OneToMany/@ManyToOne for relationships
- Lombok @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor

## Dependencies

### Internal
- Database schema in `../../../resources/data.sql`
- Repository interfaces in `../repository/`

### External
- JPA/Hibernate - ORM framework
- Lombok - Code generation
- Validation API - Bean validation

<!-- MANUAL: -->