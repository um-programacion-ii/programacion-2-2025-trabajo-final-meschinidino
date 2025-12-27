# Repository Guidelines

## Project Structure & Module Organization
- `backend/`: Spring Boot service (Java 24). Core source in `backend/src/main/java`, tests in `backend/src/test/java`.
- `proxy/`: Spring Boot proxy service (Java 24). Source in `proxy/src/main/java`, tests in `proxy/src/test/java`.
- `mobile/`: Kotlin Multiplatform app. Shared code in `mobile/composeApp/src/commonMain`; platform-specific in `androidMain`, `iosMain`, `jsMain`, `wasmJsMain`.
- `docs/`: Setup and reference material. Root `README.md` and `docs/SETUP.md` cover environment details.

## Build, Test, and Development Commands
- `docker compose up -d --build`: Build and start all services (root).
- `docker compose down`: Stop services.
- `./gradlew build`: Compile and run tests (run inside `backend/` or `proxy/`).
- `./gradlew bootRun`: Run backend or proxy locally (inside each module).
- `./gradlew :composeApp:assembleDebug`: Build Android debug APK (inside `mobile/`).

## Coding Style & Naming Conventions
- Follow standard Java/Kotlin style: 4-space indentation, PascalCase for classes, camelCase for methods/fields, UPPER_SNAKE for constants.
- Keep DTO and controller naming aligned with existing packages (e.g., `presentation/dto`, `presentation/controller`).
- Prefer small, focused files and reuse existing patterns in the module you’re touching.

## Testing Guidelines
- Backend/proxy use JUnit 5 via Spring Boot (`useJUnitPlatform()` in Gradle).
- Test locations: `backend/src/test/java` and `proxy/src/test/java`.
- Run tests with `./gradlew test` in the relevant module.
- If adding KMP tests, place them in `mobile/composeApp/src/commonTest` or platform-specific test sources.

## Commit & Pull Request Guidelines
- Git history shows no strict convention; keep commits short, descriptive, and in Spanish or English (consistent within a PR).
- Prefer imperative summaries like “Agregar DTO de eventos” or “Fix proxy auth”.
- PRs should include: concise description, testing notes (commands run), and UI screenshots for mobile changes when applicable.

## Configuration & Security Notes
- Environment variables live in `.env` (see `.env.example` in repo root).
- Backend/proxy service-to-service auth uses shared tokens (`SERVICE_JWT_SECRET`, `SYNC_WEBHOOK_TOKEN`).
