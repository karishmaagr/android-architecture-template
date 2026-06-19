# Android Architecture Template

A production-ready Android project template built with **MVI + Clean Architecture + Fine-Grained Multi-Module** design. Every feature is split into three independent Gradle modules (`domain`, `data`, `ui`), enforcing strict layer boundaries at the build level — not just by convention.

---

## Module Structure

```
app/
├── core/
│   ├── common/        # Result<T>, ApiException, coroutine dispatchers, extensions
│   ├── data/          # BaseRepository, NetworkResult<T>, AppPreferences, LogoutService, Syncable
│   ├── database/      # Room database, DAOs, entities
│   ├── domain/        # UseCase / FlowUseCase / NoParamUseCase base classes
│   ├── network/       # Retrofit, OkHttp interceptors, ApiResponse
│   └── ui/            # MviViewModel base, Compose components, theme
│
├── feature/
│   ├── auth/
│   │   ├── domain/    # Models (User, AuthCredentials), AuthRepository interface, use cases
│   │   ├── data/      # AuthRepositoryImpl, AuthApi, data sources, AuthDataModule
│   │   └── ui/        # LoginScreen, RegisterScreen, ViewModels, Contracts
│   │
│   ├── home/
│   │   ├── domain/    # Post model, HomeRepository interface, GetPostsUseCase, RefreshPostsUseCase
│   │   ├── data/      # HomeRepositoryImpl, HomeApi, PostMapper, HomeDataModule
│   │   └── ui/        # HomeScreen, HomeViewModel, HomeContract, PostItem
│   │
│   ├── profile/
│   │   ├── domain/    # Profile model, ProfileRepository interface, use cases
│   │   ├── data/      # ProfileRepositoryImpl, ProfileApi, ProfileDataModule
│   │   └── ui/        # ProfileScreen (view + edit mode), ProfileViewModel, ProfileContract
│   │
│   └── settings/
│       ├── domain/    # AppSettings model, ThemeMode, SettingsRepository interface
│       ├── data/      # SettingsRepositoryImpl, SettingsDataModule
│       └── ui/        # SettingsScreen, SettingsViewModel, SettingsContract
│
└── services/
    └── sync/          # WorkManager background sync (depends on core:data:Syncable only)
```

---

## Module Dependency Graph

**Colour key:** 🔴 app &nbsp;|&nbsp; 🟣 feature:ui &nbsp;|&nbsp; 🟠 feature:data &nbsp;|&nbsp; 🟢 feature:domain &nbsp;|&nbsp; 🔵 core &nbsp;|&nbsp; 🟡 services

---

## Dependency Rules (enforced at build level)

```
feature:X:domain  ←  core:common, core:domain
feature:X:data    ←  feature:X:domain, core:common, core:data, core:network
feature:X:ui      ←  feature:X:domain, core:common, core:ui          ← NO data dependency
app               ←  feature:X:ui + feature:X:data  (the only place they meet)
```

The `ui` module **cannot** import from `data` — the Gradle dependency graph makes this a **compile error**, not a lint warning.

> **Note on `ApiException`:** Although exceptions originate in the network layer, `ApiException` lives in `core:common` so that ViewModels in `feature:X:ui` can match on specific subtypes (e.g. `Unauthorized`, `NetworkError`) without violating the no-data-in-ui rule.

---

## Why Fine-Grained Feature Modules?

### 1. Strict layer isolation — enforced by the build system
In a single-module feature, nothing stops a ViewModel from accidentally importing a DAO or a Retrofit service. With three separate modules, the compiler enforces the rule: `ui` has no path to `data` in its dependency graph. Violations fail the build.

### 2. Selective dependency — consume only what you need
Another module or app that wants to reuse business logic can depend on `:feature:home:domain` alone — it gets the use cases and interfaces without pulling in Room, Retrofit, Moshi, or any implementation detail. Gradle only compiles what is actually needed.

### 3. Faster incremental builds
Gradle's incremental build only recompiles modules whose inputs changed. Changing a screen layout touches only `:feature:home:ui` — domain and data are untouched and their cached outputs are reused. In a monolithic feature module every UI change would trigger a full recompile of data and domain too.

### 4. Smaller binary footprint per consumer
An SDK or library consumer that embeds only the domain layer avoids the transitive weight of Retrofit, Room, and Moshi. Each layer adds its own dependencies only where they are actually needed.

### 5. Parallel compilation
Gradle compiles independent modules in parallel. `domain`, `data`, and `ui` of different features have no cross-feature dependencies, so the entire feature graph can be compiled concurrently.

### 6. Clearer ownership and testability
Unit tests for use cases live in `domain` with zero Android framework dependencies — just pure Kotlin. Integration tests for the repository live in `data`. UI tests live in `ui`. Each test suite has a minimal compile scope and runs faster.

### 7. Future-proof reuse
If `feature:home` later needs to be published as a standalone SDK, the split is already done. Consumers can take `:feature:home:domain` (interface contract), `:feature:home:data` (network + cache implementation), or both — independently versioned if needed.

---

## How the App Wires It Together

`app` is the only module that depends on **both** `ui` and `data` for each feature. This is intentional: it is the composition root where Hilt's dependency graph is assembled.

```
app ──► feature:home:ui   (screens, ViewModels)
    └──► feature:home:data  (Hilt bindings: HomeRepositoryImpl → HomeRepository)
```

`feature:home:ui` injects `HomeRepository` (the interface from `domain`). `feature:home:data`'s `HomeDataModule` provides the concrete binding. Neither module knows about the other — `app` is the glue.

---

## Clean Architecture Layers

| Layer | Module | Allowed dependencies |
|-------|--------|----------------------|
| Presentation | `feature:X:ui` | Domain layer + `core:ui` + `core:common` |
| Domain | `feature:X:domain` | `core:common`, `core:domain` only |
| Data | `feature:X:data` | Domain layer + `core:data/network/database` |
| Core | `core:*` | `core:common` (no feature knowledge) |

The **Dependency Inversion Principle** is the key mechanism: `RepositoryImpl` (data) implements `Repository` (domain). The domain layer defines the contract; the data layer fulfils it. The domain never imports anything from data.

---

## MVI Pattern

| Concept | Type | Description |
|---------|------|-------------|
| **State** | `StateFlow` | Always present — the screen never guards against null |
| **Intent** | sealed interface | The only entry point into the ViewModel |
| **Effect** | `Channel` | Fire-and-forget — navigation and snackbars fire exactly once |

Every feature follows the same three-part contract:

```kotlin
data class HomeState(
    val posts: List<Post>  = emptyList(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val error: String?     = null,
) : UiState

sealed interface HomeIntent : UiIntent {
    data object LoadPosts    : HomeIntent
    data object RefreshPosts : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data object NavigateToProfile              : HomeEffect
    data object SessionExpired                 : HomeEffect
    data class ShowSnackbar(val message: String) : HomeEffect
}
```

ViewModels extend `MviViewModel<State, Intent, Effect>` and only mutate state via `setState { }`.

---

## API Error Handling

All HTTP errors flow through a consistent three-layer pipeline:

```
BaseRepository.safeApiCall()
        │
        ▼  produces
NetworkResult<T>                          [core:data — data layer only]
  ├── Success(data)      — 2xx with body
  ├── NotModified        — 204 No Content (cache is still fresh)
  └── Failure(ApiException)
        │
        ▼  converted by toResult() / toUnitResult()
Result<T>                                 [core:common — all layers]
  ├── Success(data)
  └── Error(ApiException, message)
        │
        ▼  matched in ViewModel
when (result.exception) {
    is ApiException.Unauthorized   → SessionExpired effect → navigate to login
    is ApiException.ValidationError → surface fieldErrors per field in state
    is ApiException.NetworkError   → isOffline = true, show offline banner
    is ApiException.TooManyRequests → rate-limit message in state
    is ApiException.ServerError    → generic server-error message
    else                           → fallback message
}
```

### HTTP Status Code → `ApiException` mapping

| HTTP | `ApiException` subclass | Typical ViewModel response |
|------|------------------------|---------------------------|
| 400 | `BadRequest` | General error message |
| 401 | `Unauthorized` | `SessionExpired` effect → navigate to login |
| 403 | `Forbidden` | General error message |
| 404 | `NotFound` | "Not found" error message |
| 409 | `Conflict` | Field-level error (e.g. "email already taken") |
| 422 | `ValidationError` | Per-field errors from `fieldErrors` map |
| 429 | `TooManyRequests` | Rate-limit snackbar / state message |
| 5xx | `ServerError` | "Server error, try again" message |
| IOException | `NetworkError` | Offline banner + cached data |

### 204 No Content

`204` is handled as `NetworkResult.NotModified`. Repositories decide what it means per endpoint:

- **data refresh** (`HomeRepositoryImpl.refreshPosts`) — skip the cache clear-and-insert; the existing DB rows are served as-is via the live `Flow`
- **profile update** (`ProfileRepositoryImpl.updateProfile`) — return the input profile unchanged (server confirmed nothing changed)
- **logout** (`AuthRemoteDataSourceImpl.logout`) — treated as success (`toUnitResult()`)

### `ApiException` lives in `core:common`

`ApiException` is a pure-Kotlin sealed class with no network or Android dependencies. Placing it in `core:common` (not `core:network`) means `feature:X:ui` modules can import and `when`-match on it without violating the no-data-in-ui dependency rule.

---

## Tech Stack

| Area | Library |
|------|---------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVI + Clean Architecture |
| DI | Hilt |
| Navigation | Navigation Compose |
| Networking | Retrofit + OkHttp + Moshi |
| Local storage | Room + DataStore |
| Async | Kotlin Coroutines + Flow |
| Image loading | Coil |
| Background work | WorkManager |
| Testing | JUnit4, MockK, Turbine, Truth, Robolectric |

---

## Use Case Base Classes

| Class | Use when |
|-------|----------|
| `UseCase<P, R>` | Single suspend call with a parameter |
| `NoParamUseCase<R>` | Single suspend call with no parameter |
| `FlowUseCase<P, R>` | Ongoing stream; emits `Flow<Result<R>>` |

All three catch exceptions thrown by `execute()` and wrap them in `Result.Error(exception)`, preserving the typed `ApiException` for the ViewModel to match.

---

## Session Expiry

When any screen receives `ApiException.Unauthorized`, the ViewModel emits a `SessionExpired` effect. `AppNavGraph` handles this by navigating to the auth graph and clearing the main graph from the back stack:

```kotlin
onSessionExpired = {
    navController.navigate(NavRoutes.AuthGraph.route) {
        popUpTo(NavRoutes.MainGraph.route) { inclusive = true }
    }
}
```

This pattern is wired in `HomeScreen` and `ProfileScreen`.

---

## Getting Started

1. Clone the repo
2. Open in Android Studio Hedgehog or newer
3. Replace `com.example.arch` with your package name across all files and `build.gradle.kts` namespaces
4. Update `AppConstants` with your API base URL
5. Run on device or emulator

---

## License

```
Copyright 2024 karishmaagr

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
