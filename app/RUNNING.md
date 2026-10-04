# Running TripTogether

## Active Firebase configuration (13/10/2026)

The active app configuration now targets `triptogetherv1`, package `com.example.triptogether`. The downloaded JSON has no Realtime Database URL or OAuth clients yet. Complete the new project setup before backend testing; see [FIREBASE_NEW_PROJECT.vi.md](docs/FIREBASE_NEW_PROJECT.vi.md). Older project references below describe previous configuration and verification.


From the repository root:

```bash
bash app/run-project.sh build
bash app/run-project.sh check
bash app/run-project.sh run
```

From the app directory, omit `app/`. The runner uses an installed Java 21 or 17 on macOS and invokes the Gradle wrapper through Bash (the existing wrapper lacks executable permission). `run` requires one connected Android device or emulator; use `ANDROID_SERIAL` to select one when several are connected.

Android Studio: open the repository root, select Java 21 or 17 as the Gradle JDK, and install Android SDK Platform 35. Platform 35 was installed during this verification. Android Studio's bundled Java 25 failed with the current Gradle/Kotlin setup. Java/Kotlin source compatibility remains 11, which is separate from the Java runtime used for Gradle. Minimum device version is Android 8.0 / API 26.

## Fixed

- Removed duplicate Maps dependency and unused kapt plugin; used the catalog's Firebase BOM version.
- Optional local properties, Gradle property/environment key overrides, safe BuildConfig escaping, and manifest Maps key wiring.
- Corrected missing layout dimension and member-icon ID that prevented compilation.
- Declared network permissions directly and made MainActivity internal.
- Restorable itinerary dialogs using saved arguments and fragment results; rejected invalid edited times and reversed trip dates; tolerated malformed saved dates/times.
- Corrected the past-trip cutoff so trips ending today remain current.
- Added login retry after cancellation instead of repeatedly reopening sign-in.
- Added profile-name/photo fallbacks for Firebase sign-in profiles.
- Saved a new trip and all membership indexes atomically.
- Waited for file metadata to save before reporting upload success; handled missing document viewer apps and used filenames for MIME extensions.
- Added geocoding network timeouts and connection cleanup.

## Configuration gaps

### Google sign-in: local certificate configuration

Updated on 13/10/2026 from `/Users/ngobinh/Downloads/google-services.json`. The configured Firebase project is `trip-together-87754`, its Android package is `com.example.triptogether`, and the active configuration now includes this machine's debug SHA-1. Actual Google sign-in and Authentication provider settings still need verification.

Local debug SHA-1:

```
C0:4E:2B:7C:B4:D6:14:F4:D2:FC:30:5F:B9:A6:6B:A2:F9:57:71:F8
```

The downloaded configuration includes that fingerprint. Confirm Google sign-in is enabled under Authentication and test the app. For another signing key, register its fingerprint and download the configuration again. This requires access to the Firebase project; it cannot be repaired by editing Kotlin code. Email sign-in also requires the Email/Password provider to be enabled. Successful account authentication has not been verified.

### Maps and geocoding credentials

Set these in the repository-root `local.properties` (the app-level file is not read by the build), or supply Gradle properties/environment variables:

```properties
MAPS_API_KEY=your_maps_android_sdk_key
GEOCODING_API_KEY=your_geocoding_key
```

Hardcoded source/resource key fallbacks have been removed. Set keys through local build configuration; enabled APIs, billing, and restrictions have not been verified. Maps SDK and the HTTP Geocoding API are separate services; configure restrictions accordingly. Geocoding failure allows saving a trip with coordinates `(0, 0)`; the map now displays an unavailable-location message rather than a pin at those coordinates. A production implementation should geocode through a backend or use an Android-supported location lookup rather than embedding a web-service credential.

### Firebase backend access

No deployed database or storage rules are included in this checkout. Authentication providers, current rules, bucket availability, and billing are unverified. Trip listing reads the full `/trips` tree and invitation lookup reads `/users`; restrictive deployed rules may deny those operations even after login. The new atomic trip save requires access to the trip and membership indexes together. Do not make the database public to work around permission errors; align the rules and queries with membership-based access.

Live trip creation, invitations, chat, and file uploads have not been exercised with an authenticated account.

## Verification

The app now launches into its own email/password sign-in screen, with registration, password reset, and Google sign-in through FirebaseUI. The redesign verification covers 19 actual screen layouts through a debug-only host with local fixtures, including recreation. Additional instrumentation checks cover login validation/registration restoration and dark mode with larger text. This host is absent from release builds and never writes fixture data to Firebase. These checks do not replace authenticated end-to-end testing.

Screenshots and scope: `app/docs/REDESIGN.vi.md`.

Lint has zero errors; nonblocking warnings remain for accessibility, localization, old APIs, dependency updates, and image resources. Major toolchain upgrades were not required to build this project.

APK: `app/build/outputs/apk/debug/app-debug.apk`.
Lint report: `app/build/reports/lint-results-debug.html`.

## Refactor phase (10 October 2026)

Use `bash run-project.sh review` for formatting validation, debug compilation and Android lint without launching the app or accessing Firebase. `bash run-project.sh format` applies the shared Kotlin formatter. `check` additionally runs unit tests, for the later testing phase.

Code now uses repository access, ViewModel/StateFlow, lifecycle-aware collection, immutable lists with DiffUtil, ViewBinding, Parcelable trip navigation and document-picker contracts. Dependencies are explicitly cataloged and Firebase versions are resolved through the BOM. The Java/Kotlin bytecode target is 17; Gradle runtime remains Java 17/21.

Historical device test results and screenshots above describe the pre-refactor build. Functional tests and authenticated backend tests have not been rerun in this phase. See `docs/CODE_REFACTOR.vi.md` for scope and remaining backend constraints.
