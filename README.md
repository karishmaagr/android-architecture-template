# Android Architecture Template

A production-ready Android project template built with **MVI + Clean Architecture + Multi-Module** design. Use this as a starting point for new Android projects — the patterns, wiring, and module graph are already in place.

## Architecture

```
app/
├── core/
│   ├── common/        # Result wrapper, coroutine dispatchers, extensions
│   ├── data/          # BaseRepository, AppPreferences, LogoutService, Syncable
│   ├── database/      # Room database, DAOs, entities
│   ├── domain/        # UseCase, FlowUseCase, NoParamUseCase base classes
│   ├── network/       # Retrofit, OkHttp interceptors, ApiResponse model
│   └── ui/            # MviViewModel base, shared Compose components, theme
├── feature/
│   ├── auth/          # Login, Register (MVI screens + data + domain)
│   ├── home/          # Posts list (offline-first, Room + Retrofit)
│   ├── profile/       # User profile view and edit
│   └── settings/      # Theme, notifications, logout
└── services/
    └── sync/          # WorkManager background sync
```

### Layer rules

- `feature` modules depend on `core` modules only — never on each other
- `domain` layer has no dependency on `data`, `network`, or `ui`
- `services` modules depend on `core` abstractions (`Syncable`) — not on feature repositories
- Cross-cutting concerns (logout, sync) are exposed as interfaces in `core:data` and implemented in the feature that owns them

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

## MVI Pattern

Every feature follows the same three-part contract:

```kotlin
// State — what the screen renders
data class HomeState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) : UiState

// Intent — what the user can do
sealed interface HomeIntent : UiIntent {
    data object LoadPosts : HomeIntent
    data object RefreshPosts : HomeIntent
}

// Effect — one-shot events (navigation, snackbars)
sealed interface HomeEffect : UiEffect {
    data object NavigateToProfile : HomeEffect
    data class ShowSnackbar(val message: String) : HomeEffect
}
```

ViewModels extend `MviViewModel<State, Intent, Effect>` and only mutate state via `setState { }`.

## Use Case Base Classes

| Class | Use when |
|-------|----------|
| `UseCase<P, R>` | Single suspend call with a parameter |
| `NoParamUseCase<R>` | Single suspend call with no parameter |
| `FlowUseCase<P, R>` | Ongoing stream; emits `Flow<Result<R>>` |

## Getting Started

1. Clone the repo
2. Open in Android Studio Hedgehog or newer
3. Replace `com.example.arch` with your package name in all files and `build.gradle.kts`
4. Update `AppConstants` with your API base URL
5. Run on device or emulator

## Module Dependency Graph

```
app
 ├── core:common
 ├── core:data ──── core:common
 ├── core:database ─ core:common
 ├── core:domain ─── core:common
 ├── core:network ── core:common
 ├── core:ui ─────── core:common
 ├── feature:auth ── core:{common,data,domain,network,ui}
 ├── feature:home ── core:{common,data,database,domain,network,ui}
 ├── feature:profile── core:{common,data,domain,network,ui}
 ├── feature:settings─ core:{common,data,domain,ui}
 └── services:sync ─ core:{common,data,domain,network}
```

## License

```
Copyright 2024 karishmaagr

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
