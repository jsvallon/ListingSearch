# Module Dependency Diagram

Arrows point from a module to the modules it depends on. Dependencies only go one way:
no module depends on a module above it.

```text
┌─────────────────────────────────┐
│              :app               │
│                                 │
│ MainActivity                    │
│ ListingSearchApplication (Hilt) │
│ ListingSearchTheme              │
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│         :feature:search         │
│                                 │
│ SearchRoute                     │
│ SearchScreen · SearchIcons      │
│ SearchViewModel                 │
│ SearchUiState · SearchAction    │
│ SearchMessages                  │
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│          :common:data           │
│                                 │
│ SearchListingsUseCase           │
│ ListingRepository               │
│ ListingRepositoryImpl           │
│   └─ ListingDto.toModel()       │
│ DataModule (Hilt)               │
└───────┬────────────────────┬────┘
        │                    │
        ▼                    ▼
┌────────────────┐ ┌───────────────────┐
│ :common:model  │ │  :common:network  │
│                │ │                   │
│ Listing        │ │ ListingApiService │
│ SearchCriteria │ │ DTOs              │
│ SearchResult   │ │ NetworkModule     │
│                │ │ NetworkConfig     │
└────────────────┘ └─────────┬─────────┘
                             │ HTTP
                             ▼
                   ┌───────────────────┐
                   │   Provided API    │
                   │                   │
                   │ server.py         │
                   │ :8000             │
                   └───────────────────┘
```

## Exact Gradle dependencies

The diagram shows the main path. These are all the project dependencies declared in the
`build.gradle.kts` files:

| Module | Depends on |
| --- | --- |
| `:app` | `:feature:search`, `:common:data`, `:common:network` |
| `:feature:search` | `:common:data`, `:common:model` |
| `:common:data` | `:common:model` (as `api`), `:common:network` |
| `:common:network` | none |
| `:common:model` | none |

`:app` depends on `:common:data` and `:common:network` only so Hilt can find `DataModule` and
`NetworkModule` when it builds the dependency graph. The UI never uses Retrofit or the DTOs directly.
