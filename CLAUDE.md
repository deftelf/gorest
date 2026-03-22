# Project Stack

- Kotlin only
- Jetpack Compose (no XML)
- MVVM architecture
- Koin for DI
- KTor for networking
- SQLDelight for database
- Coroutines + Flow only (no LiveData)

---

# Architecture Rules

- UI layer contains composables only
- No business logic in composables
- ViewModels handle state and logic
- Repositories manage data sources
- Domain layer contains use cases
- Strict separation between layers

---

# Compose Guidelines

- Prefer stateless composables
- Apply state hoisting
- UI state must be immutable
- Use collectAsStateWithLifecycle for Flow

---

# Performance Rules

- Use LazyColumn for large lists
- Avoid unnecessary recompositions
- Avoid blocking main thread
- Use Dispatchers.IO for data operations

---

# Naming Conventions

- Screens → Screen
- UI state → UiState
- Events → UiEvent
- Repository → Repository

---

# Forbidden

- No LiveData
- No direct database access in ViewModel
- No XML layouts
- No Java files