<!-- Parent: ../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# entity

## Purpose
JPA entity classes representing database tables and relationships in the DevOrbit application. Contains the core data model for courses, GitHub repositories, users, learning roadmaps, photobooth frames, notes, tech stacks, and related enumeration types. ~22 entity classes mapping to PostgreSQL tables.

## Key Files

### Course & Curriculum
| File | Description |
|------|-------------|
| `Course.java` | Course entity: maMH, tenMH, credits, lecture, practice, semester, elective group |
| `CourseRelationship.java` | Course relationship mapping (prerequisites, corequisites, alternatives) |
| `CourseRelationType.java` | Enum: PREREQUISITE, COREQUISITE, ALTERNATIVE, etc. |

### GitHub & Repositories
| File | Description |
|------|-------------|
| `GithubRepo.java` | GitHub repository entity with metadata: name, description, stars, language, topics, and course relationship |
| `RepoCandidate.java` | Candidate repository pending review: GitHub URL, status, submitter info |
| `RepoCandidateStatus.java` | Enum: PENDING, APPROVED, REJECTED |

### Learning & Roadmaps
| File | Description |
|------|-------------|
| `LearningRoadmap.java` | Learning roadmap entity with title, description, difficulty, course association |
| `RoadmapPhase.java` | Phase within a roadmap: order, title, description |
| `RoadmapItem.java` | Item within a roadmap phase: order, title, type (course/repo/article/video), target ID |
| `RoadmapItemTargetType.java` | Enum: COURSE, REPOSITORY, ARTICLE, VIDEO |

### Content Resources
| File | Description |
|------|-------------|
| `CourseArticle.java` | Course-related article resource: title, URL, description |
| `CourseTutorial.java` | Course-related tutorial resource: title, URL, platform, duration |
| `CourseYoutubePlaylist.java` | Course-related YouTube playlist: title, playlist ID, channel name |

### Notes
| File | Description |
|------|-------------|
| `Note.java` | User note with title, content, tag, course/repo association |
| `NoteCodeSnippet.java` | Code snippet embedded in a note: language, code, note reference |
| `NoteTargetType.java` | Enum: COURSE, REPOSITORY |

### Users & Authentication
| File | Description |
|------|-------------|
| `AdminUser.java` | Admin user entity: email, password (BCrypt hashed), name, role |
| `StudentUser.java` | Student user entity: email, password, name, student ID, avatar |
| `StudentBookmark.java` | Student bookmark: user reference, target type (course/repo), target ID |
| `Otp.java` | One-time password entity for email verification / password reset |

### Tech & Utilities
| File | Description |
|------|-------------|
| `TechStack.java` | Technology stack classification: name, slug, category, icon URL |
| `PhotoboothFrame.java` | Photobooth frame entity: name, image URL (Supabase Storage), active status |

## For AI Agents

### Working In This Directory
- Use JPA annotations (`@Entity`, `@Table`, `@Column`)
- Define relationships with `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- Use `@Enumerated(EnumType.STRING)` for enum fields
- Include Bean Validation constraints (`@NotBlank`, `@NotNull`, `@Size`)
- Follow PostgreSQL naming conventions (snake_case)
- Use Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) for boilerplate reduction
- Temporal fields use `java.time.LocalDateTime`

### Testing Requirements
- Test entity validation constraints
- Verify relationship mappings (cascade, fetch types)
- Check database column mapping matches Flyway migration
- Ensure enum ordinals vs string storage is consistent

### Common Patterns
- `@Entity` + `@Table(name = "table_name")` annotations
- `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)` for auto-increment PKs
- `@Column(name = "column_name", nullable = false)` for field mapping
- `@CreationTimestamp` / `@UpdateTimestamp` for audit timestamps
- Lombok `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- `@Enumerated(EnumType.STRING)` for enum persistence

## Dependencies

### Internal
- Database schemas in `../../../resources/schema.sql`, `../../../resources/schema-complete.sql`
- Flyway migration in `../../../resources/db/migration/V002__create_photobooth_frames.sql`
- Repository interfaces in `../repository/`

### External
- JPA/Hibernate — ORM framework
- Lombok — Code generation
- Jakarta Validation API — Bean validation

<!-- MANUAL: -->
