# Architecture Notes

## Class Diagram

![Class Diagram](docs/class-diagram.png)

## Search Sequence Diagram

![Search Sequence Diagram](docs/sequence-diagram.png)

## Module Dependency Diagram

See [docs/module-diagram.md](docs/module-diagram.md).

## State flow
`SearchScreen -> SearchAction -> SearchViewModel -> SearchListingsUseCase -> ListingRepository -> ListingApiService`

The result flows back as domain models and is reduced into a new immutable `SearchUiState`, collected with `collectAsStateWithLifecycle()`.

## State vs events
This assessment currently has no required one-shot navigation or snackbar effects, so a separate effect Channel/SharedFlow would add complexity without value. If a future requirement adds navigation or transient effects, those would be modeled separately rather than stored as replayable persistent UI state.

## Failure behavior
- Invalid min/max price: fail fast locally and avoid an unnecessary network request.
- HTTP/API failure: surface a retryable error state.
- Connectivity failure: show the same retryable error state with a user-friendly message ("Check your connection and try again."). The ViewModel's technical message, which mentions the local Python server, is not shown to the user.
- HTTP 200 + zero results: valid empty state, not an error.

## Scalability
For this single-feature assessment, five Gradle modules demonstrate boundaries without reproducing the full Now in Android production build. If the product grew, `common` could be split further into design system, database, analytics, and other stable capabilities, while features remain isolated from one another.
