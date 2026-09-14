# AGENTS.md

This file provides guidance to AI coding agents working in this repository.
All paths in this document are relative to the repository root.

## Required reading

Before planning, reviewing, or modifying this project, read
[docs/GENERIC_RULES.md](docs/GENERIC_RULES.md) in full and apply it alongside
this file. The link is an explicit reading requirement; do not assume your
tool automatically imports linked Markdown files.

GENERIC_RULES.md contains reusable working rules. This file adds the project's
Android conventions and spec-driven workflow. For repository conventions,
project-specific rules refine the generic defaults. Neither file overrides
the user's explicit instructions or the agent's higher-priority instructions.
If documents conflict in a way that affects behavior or scope, clarify the
conflict before implementing the affected part.

## Project

Native Android app built with Kotlin and Jetpack Compose as a course project
("Curso SDD Mobile", spec-driven development).

- Single module: `app`.
- Package: `com.aristidevs.cursopremiumandroid`.
- Baseline functionality: fetch a dog catalog and dog details from a static
  JSON API and render them with Compose.
- The existing Dog feature is the reference for new features. Inspect its
  implementation before extending the app.

## Commands

Run commands from the repository root using the Gradle wrapper.

```bash
./gradlew :app:assembleDebug          # Build the debug APK
./gradlew :app:test                  # Run local JVM tests across variants
./gradlew :app:testDebugUnitTest      # Run local JVM tests for debug
./gradlew :app:connectedDebugAndroidTest  # Requires a device/emulator
./gradlew :app:lintDebug              # Run Android lint for debug

# Run one local test class; replace SomeTest with an existing class
./gradlew :app:testDebugUnitTest --tests "com.aristidevs.cursopremiumandroid.SomeTest"
```

Local tests live in `app/src/test`; instrumented tests live in
`app/src/androidTest`. If build variants change, inspect the available Gradle
tasks and use the appropriate variant-specific task.

There is no separate ktlint/detekt configuration in the baseline repository.
Use the existing configuration; do not introduce a new quality tool as an
unrelated change.

For code changes, run `:app:assembleDebug`, `:app:testDebugUnitTest`, and
`:app:lintDebug` before reporting completion. Run relevant instrumented tests
and manual checks when acceptance criteria require device behavior. For
documentation-only changes, verify content and references without requiring
an Android build. Report any checks that could not run and the reason.

## Spec-driven workflow

### Reference documents

Each template carries its own instructions for the agent, its section
structure, and its identifier scheme. Read the template in full and follow it;
this file does not restate its content and must not contradict it.

- `docs/SPEC_TEMPLATE.md`: copy to the feature's `SPEC.md` when creating or
  updating a specification. It owns the spec's sections, its `RF-` requirement
  and `CA-` acceptance-criteria identifiers, and its approval states.
- `docs/PLAN_TEMPLATE.md`: copy to the feature's `PLAN.md` once the
  specification is approved. It owns the plan's sections and approval states.
  Reference requirements and criteria by their spec identifiers.
- `docs/MOBILE_GUIDELINES.md`: consult when writing or reviewing requirements,
  acceptance criteria, the technical plan, and mobile validation. Consider
  lifecycle, state retention, connectivity, persistence, UI states, navigation
  and interruptions, forms, screen adaptation, accessibility, permissions,
  performance, background work, privacy, and internationalization. Apply only
  relevant items; clarify undefined product behavior instead of inventing it.

Preserve each template's structure and its embedded comments in the copy. Do not
rewrite the shared templates for an individual feature.

### Feature documents

Keep each feature's documents together:

- `docs/features/<feature-name>/SPEC.md`
- `docs/features/<feature-name>/PLAN.md`
- `docs/features/<feature-name>/TASKS.md`

Use an existing feature directory when continuing its work.

### Sequence

Each stage is gated by the previous document's state. A document is only
`Aprobada`/`Aprobado` when the user says so: a complete document is not an
approved one, and the agent never changes that state on its own.

1. **Specification:** complete `SPEC.md` from `docs/SPEC_TEMPLATE.md`,
   collaboratively and section by section, following the template's own
   instructions. Do not start the plan until the user approves the spec.
2. **Plan:** write `PLAN.md` from `docs/PLAN_TEMPLATE.md` for the approved
   specification, following the template's own instructions. Additionally,
   record whether subagents are needed and their bounded responsibilities; do
   not assume delegation is required or available. Do not start tasks until
   the user approves the plan.
3. **Tasks:** derive `TASKS.md` from the approved plan. There is no shared
   template for it, so this file defines it: small, ordered, verifiable
   checkboxes, each with an identifier, objective, scope, dependencies, the
   spec criteria it resolves, and its validation method. Keep tasks concise
   enough to execute and detailed enough to determine when they are done.
4. **Implementation:** execute the tasks within the agreed scope, preserving
   the architecture below. Authorization to implement must be explicit; it is
   not implied by the documents' state. Update task status as work progresses.
5. **Validation:** verify acceptance criteria with appropriate evidence and
   record the result in `TASKS.md`, including any outstanding checks.

Do not treat a filled template as resolution of unanswered questions. Ask
about missing product decisions that affect behavior; resolve routine technical
details from the code and established conventions. Do not ask for renewed
permission for steps the user has already authorized.

If implementation reveals a requirement gap or contradiction, clarify the
affected behavior and update the relevant documents before continuing that
part. Keep the specification, plan, tasks, and resulting behavior consistent.

## Architecture

Clean Architecture with unidirectional UI data flow. The existing Dog feature
is wired end to end with this structure, under the app's package directory:

| Path | Responsibility |
| --- | --- |
| `data/api/DogApiServices.kt` | Retrofit interface with suspend functions and kotlinx.serialization |
| `data/api/response/*Response.kt` | Wire DTOs annotated with `@Serializable` |
| `data/mapper/DogMapper.kt` | Response-to-domain extension functions such as `toDomain()` |
| `data/DogRepositoryImpl.kt` | Repository implementation, API access, and mapping to domain |
| `domain/model/*.kt` | Plain domain models |
| `domain/DogRepository.kt` | Repository contract owned by domain |
| `domain/usecase/*.kt` | Use cases with `suspend operator fun invoke(...)` where appropriate |
| `presentation/<feature>/*ViewModel.kt` | Hilt ViewModel exposing `StateFlow<UiState>` |
| `presentation/<feature>/*Screen.kt` | Stateful screen entry point and stateless content |
| `core/navigation/Routes.kt` | Type-safe `@Serializable` route objects/data classes implementing `NavKey` |
| `core/navigation/AppNavigation.kt` | Navigation3 `NavDisplay` and `entryProvider` wiring |
| `core/di/DataModule.kt` | Hilt module for JSON, Retrofit, API, and repository dependencies |
| `core/di/DogApiConfig.kt` | API base URL, also used by image URL mapping |

### Layer dependencies

- `presentation -> domain` and `data -> domain`.
- Domain owns repository interfaces and must not depend on presentation,
  data implementations, DTOs, or Android framework classes.
- Presentation accesses domain use cases and models, never data implementations
  or DTOs directly.
- Data implements domain contracts and maps external representations to domain
  models. Dependency injection wires the implementations to their contracts.

### Dependency injection

- Use Hilt and prefer `@Inject constructor` for project-owned classes.
- Follow the existing modules in `core/di` for interface bindings and factory
  construction of dependencies such as Retrofit.
- Use `@Binds` or `@Provides` as appropriate to the existing setup. Interfaces
  cannot receive constructor injection.
- Match dependency scopes to their intended lifetime and existing conventions.

### Navigation

- Use `androidx.navigation3`, following the existing implementation.
- Define type-safe `@Serializable` route keys in `Routes.kt` and centralize
  screen wiring in `AppNavigation.kt` through `entryProvider`.
- Do not introduce the older Navigation Compose stack alongside Navigation3.

### UI state and Compose

- Each screen exposes one `StateFlow<UiState>` from its ViewModel.
- Preserve the existing screen's state representation: `DogsUiState` uses a
  data class; `DogDetailUiState` uses a sealed Loading/Success/Error model.
  For new screens, choose a representation that expresses valid states clearly.
- Collect state using `collectAsStateWithLifecycle()`.
- Keep a stateful screen entry point that wires `hiltViewModel()` and state
  collection, plus a stateless `*Content` composable accepting state and callbacks.
- Keep business logic and direct data access out of composables.
- Define loading, content, empty, and error behavior where relevant to the spec.

### Coroutines and errors

- Use structured concurrency and `viewModelScope` for ViewModel-owned work.
- Handle expected failures and reflect them in UI state as required by the spec.
- Never swallow `CancellationException`. If catching `Exception` or another
  broad type, rethrow cancellation before handling other errors.
- Keep blocking work off the main thread and respect dependency threading
  contracts. Do not wrap every suspend call in `Dispatchers.IO` by default.

### Theming

- Colors and typography live in `ui/theme/` (`Color.kt`, `Theme.kt`, `Type.kt`).
- Use the existing named theme colors, such as `BackgroundApp`,
  `BackgroundComponent`, `SecondaryText`, `ControlColor`, and `PrimaryButton`.
- Avoid hardcoded hex values in screens. Add reusable values to the theme
  when the feature needs them.

### Networking

- Use Retrofit with the kotlinx.serialization converter, following the current
  configuration. Do not introduce Gson or Moshi.
- API image paths are relative. Build image URLs in the mapper using
  `DogApiConfig.BASE_URL`, matching the existing `DogMapper` pattern.
- Keep API DTOs in the data layer and expose domain models to callers.

## Completion report

Summarize the implemented behavior, the affected feature documents, and the
validation results. Link acceptance criteria to evidence in `TASKS.md`.
Clearly identify anything incomplete or unverified; do not present a test name,
an unexecuted command, or "should work" as proof of success.
