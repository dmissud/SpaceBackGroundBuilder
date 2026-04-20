# Copilot instructions for SpaceBackGroundBuilder

Purpose: concise, repo-specific guidance for Copilot-based assistants and future Copilot CLI sessions.

## Build, test, and run (commands)

Backend (Maven, multi-module)
- Full build: ./mvnw clean install (or mvn clean install)
- Build without tests: ./mvnw clean install -DskipTests
- Run backend app (dev): cd sbgb-configuration && ./mvnw spring-boot:run
- Run all tests: ./mvnw test
- Run tests for a single module: ./mvnw -pl sbgb-application test
- Run a single test class:
  - Fully-qualified class: ./mvnw -Dtest=com.example.MyServiceTest test
  - Single method: ./mvnw -Dtest=com.example.MyServiceTest#shouldDoX test
- Run integration/Cucumber tests: ./mvnw -Dtest=RunCucumberTest test

Frontend (Angular, Jest)
- Install deps: cd sbgb-gui && npm ci
- Dev server: cd sbgb-gui && npm start
- Build prod: cd sbgb-gui && npm run build
- Run all unit tests: cd sbgb-gui && npm test
- Run a single Jest test by path: cd sbgb-gui && npx jest path/to/spec.or.test.ts
- Run a single Jest test by name: cd sbgb-gui && npm test -- -t "test name pattern"

Docker / Compose
- Quick start (local): docker-compose up -d
- Stop: docker-compose down
- Use published images: docker-compose -f docker-compose.prod.yml up -d
- Rebuild no-cache: docker-compose build --no-cache

Linting
- No repo-wide lint script detected in root. Frontend uses Angular tooling; check sbgb-gui/package.json for lint scripts (npm run lint).

## Quick single-test examples (copy-paste)
- Backend single class: ./mvnw -Dtest=org.dbs.sbgb.service.GalaxyServiceTest test
- Backend single method: ./mvnw -Dtest=org.dbs.sbgb.service.GalaxyServiceTest#shouldCreateGalaxy test
- Frontend single jest: cd sbgb-gui && npm test -- -t "renders galaxy"

## High-level architecture
- Multi-module Maven project (see root pom.xml modules):
  - sbgb-application: domain models, services, business rules
  - sbgb-infrastructure: JPA adapters, Liquibase changelogs
  - sbgb-exposition: REST controllers, DTOs, API layer
  - sbgb-configuration: Spring Boot app entry, configuration
  - sbgb-cmd: CLI utilities (noise/image generation)
  - sbgb-gui: Angular frontend (built -> Nginx)
  - sbgb-coverage: coverage aggregation
- Docker Compose orchestrates frontend (Nginx), backend (Spring Boot) and PostgreSQL.
- Hexagonal architecture: domain in sbgb-application; adapters implement ports in infrastructure/exposition.
- CI/CD: GitHub Actions under .github/workflows; versioning driven by PR labels (see docs/versioning-workflow.md).

## Key conventions (repo-specific)
- Development workflow: Strict TDD (Red-Green-Refactor). See CLAUDE.md for full rules.
- Commits: Angular-style messages (feat:, fix:, chore:, test:, refactor:).
- Branching: GitFlow-style (feature/*, develop, master).
- Commit author: many docs mandate committing as Daniel. To match policy use:
  git commit --author="Daniel Missud <daniel@missud.eu>" -m "..."
- Tests & tools:
  - Backend: JUnit 5, Mockito, AssertJ, Cucumber for BDD
  - Frontend: Angular 17, Jest
- Code style:
  - Prefer records for DTOs; Lombok used across modules
  - Domain services under sbgb-application/domain/service
  - Keep clear port/adapters separation

## Important files & docs to consult
- README.md, CLAUDE.md (development rules), docs/ (PRESENTATION_TECHNIQUE.md, versioning-workflow.md)
- docker-compose.yml, docker-compose.prod.yml
- k8s/ for Kubernetes manifests
- root pom.xml for module and plugin configuration

## AI/assistant config files discovered
- CLAUDE.md exists (detailed dev rules and TDD policy)
- No .cursorrules, AGENTS.md, .windsurfrules, CONVENTIONS.md detected (checked during analysis)

## MCP Servers — Playwright (configured guidance)
This project uses Angular frontend; Playwright is a recommended MCP server for E2E testing.
- Avoid Playwright downloading browsers in CI: set PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 and provide a system/browser in the image or runner.
  Example (local CI install): PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 npm ci
- Use playwright-core and point to system Chrome/Chromium if needed.
- Recommended approach for CI: use a custom Docker image that pre-installs Chromium or use a self-hosted runner with browsers.
- If desired, a GitHub Actions workflow can be added to run Playwright tests against the built frontend container. Ask to add it and the CI job will be created.

## When interacting as Copilot
- Prefer small, focused edits and verify with existing Maven/Angular tests.
- Prefer using ./mvnw and docker-compose to reproduce user environments.
- Keep generated code aligned with hexagonal boundaries (domain vs adapters).
- Respect CLAUDE.md rules: do not add production code without tests (TDD).

---

(Generated from README.md, pom.xml, CLAUDE.md, and docs/.)
