# Contributing to DevOrbit

Thank you for contributing to DevOrbit. To maintain a clean and reliable codebase, please follow the guidelines below.

## Branch Naming Conventions

Create descriptive branches off the master branch using the following prefixes:
- feature/feature-name : for new features
- bugfix/bug-description : for bug fixes
- chore/task-name : for repository updates, dependency bumps, or tooling adjustments
- docs/document-name : for documentation additions or corrections

Example: feature/gpa-goal-planner

## Commit Message Conventions

Commit messages must follow the conventional style. Use lowercase, keep the subject line short (under 50 characters), and optionally include a body for explaining the rationale if not obvious.

Format:
type(scope): description

Types:
- feat: A new feature
- fix: A bug fix
- chore: Maintenance, config changes, or dependency updates
- docs: Documentation changes
- test: Adding or updating tests
- refactor: Code alterations that do not fix bugs or add features

Examples:
- fix(admin): delete community message
- fix: lifecycle cleanup for course and repo deletion
- feat(student): add roadmap recommendation query

## Development Workflow

1. Fork or branch from master.
2. Follow the editorconfig rules:
   - UTF-8 encoding.
   - LF line endings.
   - 2-space indentation for general configurations and frontend files.
   - 4-space indentation for Java and Kotlin files.
3. Keep class and component names consistent:
   - Java package: vn.edu.uit.devorbit_api
   - Java classes: CourseService, StudentAuthController, CommunityPresenceServiceTest
   - React components: PascalCase (e.g. SemesterSelector)
   - React hooks and helpers: camelCase (e.g. useDebounce)
4. Do not modify the database schema (supabase_complete_schema.sql) directly without a review. If you need to make changes, document them in your pull request description for database posture checks.
5. Do not commit secrets, passwords, or connection tokens. Keep credentials in local configuration files (like .env or .env.properties) which are ignored by Git.

## Testing Guidelines

You must verify that your changes pass tests locally before pushing or opening a pull request.

- Backend: Run `./mvnw test` (or `./mvnw.cmd test` on Windows) to verify all JUnit tests pass.
- Frontend: Run `npm test` inside devorbit-web to verify Vitest tests. Run `npm run build` to ensure TypeScript compilation and asset bundling succeed.
- Mobile: Run `./gradlew test` (or `.\gradlew.bat test` on Windows) inside devorbit-mobile.

Targeted tests should be run first, followed by the complete module build.

## Pull Request Guidelines

- Scope: Keep pull requests small, targeted, and focused on one specific issue or feature.
- Description: Document what the change accomplishes, why it was made, and how it was tested.
- Evidence: Include test execution results, screenshots (for UI changes), or REST API response logs. Never claim success without testing the code locally.
- Documentation: If your changes alter existing behavior, APIs, or settings, you must update the relevant Markdown documentation files in the docs/ folder accordingly.
