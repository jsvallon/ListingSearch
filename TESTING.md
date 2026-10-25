# Testing & Verification

This document describes how to verify the Listing Search Android assessment. It separates **automated coverage already implemented in the project** from **manual scenarios to verify in Android Studio**.

## Testing process

The app is tested in four layers, from fastest and most isolated to slowest and closest to a real user. Run them in this order: when a fast layer fails, fix it before moving on, because the slower layers will fail for the same reason.

| Step | Layer | What it proves | Needs | Run | Expected |
| ---: | --- | --- | --- | --- | --- |
| 1 | Unit tests (`SearchViewModelTest`) | ViewModel logic: validation and search state | Nothing | Open `SearchViewModelTest` and click the green arrow, or `./gradlew :feature:search:testDebugUnitTest` | 2 passed |
| 2 | Compose UI tests (`SearchScreenTest`) | The search screen shows each state correctly and sends the right actions | Emulator | **Compose UI tests - Search screen**, or `./gradlew :feature:search:connectedDebugAndroidTest` | 17 passed |
| 3 | Cucumber scenarios (`property_search.feature`) | Whole-app user journeys: the real app, ViewModel and screen with a fake listing service | Emulator | **Cucumber - All scenarios**, or `./gradlew :app:connectedDebugAndroidTest` | 8 passed (7 scenarios; bedrooms runs twice) |
| 4 | Manual verification | The app against the real `server.py` and `data.json` | Emulator + `server.py` | Follow the table in [Manual verification scenarios](#manual-verification-scenarios) | All 18 ticked |

The names in bold are run configurations saved in the `.run/` folder. Pick them from the run dropdown in Android Studio's toolbar.

### Before you start

1. Open the project in Android Studio and run **File › Sync Project with Gradle Files**.
2. Start an emulator (any phone image with API 26 or later). Steps 2 and 3 install and drive the app on it automatically, so don't touch the emulator while they run.
3. For step 4 only, start the API from inside its folder: `cd provided-api && python3 server.py`.

### Reading the results

- **Android Studio:** the Run panel lists every test with a green tick or a red cross. Check the total at the top matches the "Expected" column above. A run that ends without listing tests did not actually pass.
- **Reports:** after each run, open the HTML report in a browser.
  - Unit tests: `feature/search/build/reports/tests/testDebugUnitTest/index.html`
  - Compose UI tests: `feature/search/build/reports/androidTests/connected/debug/index.html`
  - Cucumber: `app/build/reports/androidTests/connected/debug/index.html`
- **Crashes:** if the app crashed during an emulator run, the crash log is saved next to the results in `<module>/build/outputs/androidTest-results/connected/debug/`.
- **Watching Cucumber run:** use **Cucumber - All scenarios (slow, to watch)**, which pauses one second after every step.

### Troubleshooting

| Symptom | Cause | Fix |
| --- | --- | --- |
| `NoSuchFieldError ... R$string` in a preview or test | Android Studio is using an old build from before `strings.xml` changed | **File › Sync Project with Gradle Files**, then **Build › Rebuild Project** |
| `./gradlew: no such file or directory` | The Gradle wrapper is missing | Regenerate it from the project folder with `gradle wrapper --gradle-version 8.11.1`, and keep `gradlew`, `gradlew.bat` and `gradle/wrapper/gradle-wrapper.jar` in the project |
| Cucumber fails with `UnsupportedClassVersionError` or `io.cucumber.core.cli.Main` in the stack trace | The run was started by the **Cucumber for Java** plugin, which runs on your Mac instead of the emulator | Run from the toolbar's **Cucumber -** configurations, or disable the Cucumber for Java and Cucumber for Kotlin plugins (keep Gherkin and Cucumber for Kotlin and Android) |
| `The component was not created. Check that you have added the HiltAndroidRule` | Hilt was not started before the app opened | Keep the `RuleChain` in `ComposeRuleHolder` (Hilt outside, the Compose rule inside) |
| `requires ... compile against version 37` | A test library version needs a newer Android SDK than the project uses | Keep `cucumber = "7.18.1"` in `gradle/libs.versions.toml` |
| A test can't find an element | The element has no test tag, or the tag changed | Add or fix the tag in `SearchTestTags` and on the screen with `Modifier.testTag` |
| Extra `...Changed("")` actions in a Compose UI test | The test showed the screen with state that never changes | Use the test's `show()` helper, which updates state when fields change, like the ViewModel |

### Adding tests

- **New screen element:** add its tag to `SearchTestTags` (`feature/search/src/main/.../SearchTestTags.kt`) and put it on the element with `Modifier.testTag(SearchTestTags.X)`. Both the Compose UI tests and the Cucumber steps use these tags.
- **New screen behavior:** add a test to `SearchScreenTest` using `show(state)`, then check what's on screen and which actions were sent.
- **New user journey:** add a scenario to `property_search.feature`, reusing existing steps where possible; new steps go in `SearchSteps.kt`.
- **New ViewModel logic:** add a unit test to `SearchViewModelTest`.

## Test environment

1. Start the provided API from `provided-api/`:

   ```bash
   python3 server.py
   ```

2. Confirm the API is healthy on the host machine:

   ```bash
   curl http://localhost:8000/health
   ```

   Expected response:

   ```json
   {"status":"ok"}
   ```

3. Start an Android emulator and run the `app` configuration. The emulator uses `http://10.0.2.2:8000/` to reach the host API.

## Automated unit tests

The project currently contains `SearchViewModelTest` with focused ViewModel tests using a fake repository rather than a network dependency.

### Implemented coverage

| Test | Purpose | Expected behavior |
| --- | --- | --- |
| Invalid price range | Set minimum price to `600000` and maximum price to `400000`, then search | ViewModel exposes a validation error and does **not** call the repository |
| Successful search | Set city to `Reston` and submit | Search completes, `hasSearched` becomes true, and the repository is called once |

Run them from Android Studio (open `SearchViewModelTest` and click the green arrow next to the class), or from the terminal with `./gradlew :feature:search:testDebugUnitTest`.

## Compose UI tests

`SearchScreenTest` (`feature/search/src/androidTest/.../SearchScreenTest.kt`) tests the `SearchScreen` composable on its own: no ViewModel, no network, no server. Each test renders the screen for one `SearchUiState`, finds elements through the tags in `SearchTestTags`, and checks what is shown and which `SearchAction` the screen sends.

| Area | What is checked |
| --- | --- |
| Start screen | All five fields, Search, Clear and the start message are shown; no Hide button or summary bar |
| Typing | Each field sends its action (`CityChanged`, `KeywordChanged`, `MinPriceChanged`, `MaxPriceChanged`, `MinBedroomsChanged`) and shows the value from state |
| Search / Clear | Tapping Search, or the keyboard's Search key on Min bedrooms, sends `SearchClicked`; Clear sends `ClearClicked` |
| Loading | Spinner shown; Search and Clear disabled |
| Results | Count header, one card per listing, and each card's address, location, beds, baths and sq ft |
| Partial page | "Showing 2 of 12 properties" when the API returned only the first page |
| Filter panel | Collapses into the summary bar after a search; tapping the bar reopens it with the values kept; Hide collapses it |
| No results | Message shown; Edit filters reopens the filters |
| Network error | Friendly message shown, the ViewModel's technical message is not; Try again sends `RetryClicked` |
| Price validation | Message shown under the prices; filters stay open; no error screen |

**Run them:** pick **Compose UI tests - Search screen** from the run dropdown (under "UI tests"), or open `SearchScreenTest.kt` and click the green arrow next to the class or any test. From the terminal:

```bash
./gradlew :feature:search:connectedDebugAndroidTest
```

An emulator must be running. These tests don't need `server.py`.

## Automated UI scenarios (Cucumber)

The search screen is also covered by plain-English Cucumber scenarios that run on an emulator and drive the Compose UI through test tags.

- **Scenarios:** `app/src/androidTest/assets/features/property_search.feature`
- **Step definitions:** `app/src/androidTest/java/com/johnvallon/listingsearch/cucumber/SearchSteps.kt`
- **Test tags:** `SearchTestTags` in `:feature:search`, set on the screen with `Modifier.testTag`
- **Data:** a fake listing service (`FakeListingRepository`) replaces the real one through Hilt, so the scenarios don't need `server.py` and always see the same six listings. A step can switch it to "unavailable" to test the error screen.

| Tag | Scenario |
| --- | --- |
| `@results` | Search by city shows the results list |
| `@bedrooms` | Filter by minimum bedrooms (two examples) |
| `@no-results` | Search with no matches shows the no results message |
| `@validation` | Minimum price above maximum price is rejected |
| `@edit-filters` | Reopen the filters from the summary bar |
| `@clear` | Clear resets the search |
| `@network-error` | Network error, then retry succeeds |

### Running them

Start an emulator first.

**All scenarios:**

```bash
./gradlew :app:connectedDebugAndroidTest
```

**One scenario, by tag** (for example Clear):

```bash
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.tags=@clear
```

**From Android Studio:** the project ships ready-made run configurations in `.run/`. Pick one from the run dropdown in the toolbar (under "Cucumber scenarios") and click Run:

- **Cucumber - All scenarios** runs every scenario in the feature file.
- **Cucumber - All scenarios (slow, to watch)** runs them all with a 1-second pause after each step, so you can follow along on the emulator. From the terminal, add `-Pandroid.testInstrumentationRunnerArguments.stepDelayMs=1000`.
- **Cucumber - Search results**, **Minimum bedrooms**, **No results**, **Price validation**, **Edit filters**, **Clear** and **Network error** each run one scenario.

With the **Gherkin** and **Cucumber for Kotlin and Android** plugins installed, you can also open the `.feature` file and click the green arrow next to `Feature:` (all scenarios) or next to a single `Scenario:`.

Results appear in Android Studio's Run panel and in `app/build/reports/androidTests/connected/`.

### Writing a new scenario

Add it to the `.feature` file using the existing steps, for example:

```gherkin
Scenario: Keyword search
  When I enter "condo" in the keyword field
  And I tap Search
  Then I see "1 property found" as the result count
```

A step that doesn't exist yet needs a new function in `SearchSteps.kt`. New screen elements need a tag in `SearchTestTags`.

## Manual verification scenarios

The following scenarios should be run before submission. Mark a scenario verified only after observing the expected behavior on the emulator/device.

The expected results below were confirmed against `server.py` and `data.json` on 2026-10-25 by sending the exact requests the app sends (the app always adds `page=1&pageSize=10`). The **Verified** column is for checking them on the emulator.

| # | Scenario | Steps / Input | Expected result | Verified |
| ---: | --- | --- | --- | :---: |
| 1 | API health | Start `server.py` from inside `provided-api/`; request `/health` | `200` with `{"status":"ok"}` | ☐ |
| 2 | Basic city search | City = `Reston`; Search | **2 properties found**: 22 Birch Ln ($610,000, 4 beds) and 100 Maple Dr ($585,000, 3 beds) | ☐ |
| 3 | Case-insensitive city | City = `reston`; Search | Same 2 listings as #2 | ☐ |
| 4 | Keyword search | Keyword = `Metro`; Search | **2 properties found**, both in Vienna: 55 Elm Ct and 55 Elm Court | ☐ |
| 5 | Minimum/maximum price | Min = `400000`, Max = `500000`; Search | **5 properties found**: 55 Elm Ct, 123 Main St Apt 4B, 123 Main Street Unit 4B, 55 Elm Court, 300 Cedar Blvd | ☐ |
| 6 | Minimum bedrooms | Min bedrooms = `3`; Search | **8 properties found**, all with 3 or 4 beds | ☐ |
| 7 | Combined filters | City = `Reston`, Min = `500000`, Min bedrooms = `3`; Search | **2 properties found**: 22 Birch Ln and 100 Maple Dr | ☐ |
| 8 | No matching results | City = `Alexandria`; Search | "No properties found" screen. The server returns `200` with 0 results; this is **not** an error | ☐ |
| 9 | Invalid price range | Min = `600000`, Max = `400000`; Search | Red message under the price fields: "Minimum price cannot be greater than maximum price." No request is sent | ☐ |
| 10 | Invalid numeric input | Try typing letters in a price or bedrooms field | Letters are ignored: price fields keep only digits and one `.`; bedrooms keeps only digits | ☐ |
| 11 | Loading state | Submit a valid search | Spinner in the results area while the request runs. The local server answers almost instantly, so it may only flash | ☐ |
| 12 | API unavailable | Stop `server.py`; submit a search | "Unable to load properties" with a **Try again** button; app does not crash | ☐ |
| 13 | Recovery after failure | Restart `server.py`; tap **Try again** | Results load normally | ☐ |
| 14 | Empty optional filters | Leave every field blank; Search | **Showing 10 of 12 properties**: the API returns the first page of 10. Pagination is not in scope | ☐ |
| 15 | Multiple searches | Search `Reston`, then tap the summary bar, change City to `Fairfax`, Search | Fairfax results replace Reston's: **2 properties found**, both 789 Pine Rd ($399,000 and $399,500) | ☐ |
| 16 | Recomposition/configuration | Search successfully, then rotate the emulator | Results, filters and the collapsed/expanded filter panel are kept | ☐ |
| 17 | Duplicate source records | City = `Springfield`; Search | **4 properties found**, shown as returned, including 123 Main St / 123 Main Street and 456 Oak Ave / 456 Oak Avenue | ☐ |
| 18 | Listing presentation | Inspect returned cards | Each card shows address, city/state/zip, price, beds, baths and sq ft | ☐ |

**City is an exact match.** The server compares the whole city name (ignoring case), so `Rest` returns no results while `Reston` returns 2.

## Useful deterministic data from the provided dataset

These inputs are useful for repeatable manual checks:

- `Reston` contains known sale listings, including **22 Birch Ln** and **100 Maple Dr**.
- `Fairfax` contains **789 Pine Rd** records.
- `Springfield` includes duplicate-source examples such as **123 Main St / 123 Main Street** and **456 Oak Ave / 456 Oak Avenue**.
- A search that legitimately matches nothing should produce a normal successful response with an empty result list, not an application error.

## Failure and edge-case expectations

### Validation failure
Invalid input that can be identified locally should fail fast. In particular, `minPrice > maxPrice` should not make an unnecessary API request.

### HTTP or connectivity failure
Network/API failures should be represented as UI error state rather than crashing the application. The user should be able to correct the condition and search again.

### Empty result
An HTTP `200` response containing an empty `results` array is a valid search outcome and should render an empty state rather than an error state.

### Duplicate and near-duplicate listings
The supplied data contains duplicate and near-duplicate metadata. The assessment does not define product rules for merging those records, so this version intentionally displays the server's results rather than guessing source precedence or a confidence threshold.

## Not currently in scope

The following are intentionally not claimed as implemented or verified in the MVP:

- Offline persistence / Room cache
- Listing deduplication business rules
- Rental-search UI
- Amenities-filter UI
- Pagination/load-more UI
- Automated UI scenarios against the real `server.py` (the Cucumber scenarios use a fake listing service)

These are extension points supported by the existing boundaries rather than requirements silently added to the take-home.
