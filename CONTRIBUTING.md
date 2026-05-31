# Contributing to DevOrbit

Thank you for your interest in contributing to DevOrbit! This document outlines
the guidelines for contributing to the project.

## Prerequisites

Before you begin, make sure you have:

- **Java 21** (JDK) installed
- **Node.js 20+** and **bun** installed
- **Git** installed
- **Docker** (optional, for running dependencies via docker-compose.yml)

## Local Setup

1. **Fork and clone** the repository:

   git clone https://github.com/<your-username>/devorbit.git
   cd devorbit

2. **Install dependencies**:

   Backend (Spring Boot):
   cd devorbit-api
   ./mvnw clean install   (or .\mvnw.cmd clean install on Windows)

   Frontend:
   cd ../devorbit-web
   bun install

3. **Set up environment variables**:

   cp .env.example .env
   Edit .env with your local configuration

4. **Run the application**:

   Backend:
   cd devorbit-api
   ./mvnw spring-boot:run

   Frontend:
   cd devorbit-web
   bun run dev

## Development Workflow

1. **Create a branch** from master:

   git checkout -b feature/your-feature-name master

   Use a descriptive branch name:
   - feature/description for new features
   - fix/description for bug fixes
   - docs/description for documentation changes

2. **Make your changes** following the code style guidelines below.

3. **Write or update tests** for your changes.

4. **Commit** with a clear, descriptive message:

   git commit -m "feat: add user profile endpoint"

   Follow Conventional Commits (https://www.conventionalcommits.org/) format:
   - feat: New feature
   - fix: Bug fix
   - docs: Documentation
   - refactor: Code refactoring
   - test: Adding or updating tests
   - chore: Maintenance tasks

5. **Push** your branch and **open a Pull Request**.

## Code Style Guidelines

### Java / Kotlin

- Follow standard Java/Kotlin conventions
- Use meaningful variable and method names
- Keep methods focused and concise
- Add Javadoc/KDoc for public APIs

### TypeScript / JavaScript (React)

- Use TypeScript for all new code
- Follow the project's ESLint configuration
- Use functional components with hooks
- Keep components small and focused

### General

- No trailing whitespace
- Use consistent indentation (match the existing file)
- Comment complex logic, not obvious code

## Testing Requirements

- All new features **must** include tests
- Bug fixes **must** include a regression test
- Run the full test suite before submitting:

   Backend:  cd devorbit-api && ./mvnw test
   Frontend: cd devorbit-web && bun run test

## Pull Request Process

1. Ensure your PR targets the master branch
2. Fill out the PR template completely
3. Link any related issues
4. Ensure all CI checks pass
5. Request a review from a maintainer
6. Address review feedback promptly
7. Once approved, a maintainer will merge your PR

## Code of Conduct

Be respectful, inclusive, and constructive in all interactions.
We are committed to providing a welcoming and inclusive experience for everyone.