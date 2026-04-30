<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# org

## Purpose
Local Maven repository cache containing downloaded dependencies and artifacts for the Java/Spring Boot backend. Automatically managed by Maven build system and should not be manually modified.

## Key Files
| File | Description |
|------|-------------|
| (Various .class, .jar files) | Compiled Java classes and JAR dependencies |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `springframework/` | Spring Framework dependencies and modules |
| `...` | Other Maven groupId directories |

## For AI Agents

### Working In This Directory
- This directory is auto-managed by Maven
- Do not manually add, remove, or modify files
- Safe to delete and rebuild (Maven will re-download)
- Not part of source control (should be .gitignored)

### Testing Requirements
- Verify Maven builds work correctly
- Check dependency resolution
- Validate artifact integrity

### Common Patterns
- Standard Maven repository structure (groupId/artifactId/version/)
- Cached JAR files and POMs
- Automatically populated during builds

## Dependencies

### Internal
- Referenced by `../devorbit-api/pom.xml` for dependency resolution
- Required for building the Java backend

### External
- Maven Central Repository artifacts
- Spring Framework ecosystem dependencies

<!-- MANUAL: -->