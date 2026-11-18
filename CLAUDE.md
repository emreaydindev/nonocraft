# nonocraft_mobile

Kotlin Multiplatform app: `shared/` (domain, data, ViewModels, puzzle engine), `androidApp/` (Compose), `iosApp/` (SwiftUI, XcodeGen). Branch `kmp`. Architecture rules live in `../CLAUDE.md`. Game rules and numbers live in `../DECISIONS.md`; items below reference its sections as (D§n). Mirror its numbers in `shared/.../common/Constants.kt` (the server stays authoritative).

## What the design promises (read before building a screen)
The Android screens are the feature reference. **iOS no longer copies the Android layout**: same features and colours, but native SwiftUI with Liquid Glass (D§15).
- **Playground (home):** greeting, crystal counter with refill timer below cap, "Günün Bulmacası" card with past-days strip, current campaign level card, "Yarım kalan bölüm" resume card (size + % progress), offline random generator.
- **Random puzzle dialog:** sizes 5×5…15×15 free / up to 30×30 Pro (D§4), modes Classic / Hardcore / Colored (D§2).
- **Explore (Feed):** tabs Featured / Following / Newest / Most liked (D§10), create entry, post cards with like/comment/share/download, native ad every 8 posts (D§11).
- **Library:** Liked, Daily Adventure, Playlists (+ new list), Shared, Drafts, Solved, Downloaded.
- **Account / Profile:** avatar, stats row, Pro gold highlight, weekly top-3 badge, guest prompt to link an account.
- **Game:** timer, hearts (3 / 5 Pro), Place / Cross / Hint, revive sheet, game over and win dialogs with 1–3 star rating.
- **Settings:** theme (System, Dark, Light, AMOLED + Pro Cyberpunk, Retro Wave), app icon (Classic, Night + Pro Neon Cube, 8-Bit, Synthwave, Gold Crown), language (TR/EN), neon effects, wrong-move warning, sound effects, music, vibration, notifications, edit profile, password, delete account, privacy, terms, contact (`support@nonocraft.emreaydin.dev`).
- **Premium paywall:** monthly / yearly (trial, highlighted) / lifetime from RevenueCat, perks from D§3, restore.
- **Crafting (create puzzle):** empty placeholder on both platforms; the biggest missing screen.

## To-do: staging (internal beta / TestFlight against staging API)

### Identity, guest and language (D§12, D§14, D§15)
- [ ] iOS bundle id → `com.neilb.nonocraft` in `project.yml` (staging config `com.neilb.nonocraft.staging`); Android keeps `com.neilb.nonocraft` (+ `.staging` flavour).
- [ ] Silent anonymous account on first launch (`/auth/anonymous`), device secret in Keychain / Keystore; upgrade on sign-up; merge flow + confirmation UI when logging into an existing account.
- [ ] After a guest buys Pro: gentle "link your account" sheet.
- [ ] Email verification inline sheet on first Publish/Comment (6-digit code, one-time-code autofill); handle `EMAIL_NOT_VERIFIED`.
- [ ] Sign in with Apple on iOS (required alongside Google); Google sign-in on iOS.
- [ ] Localization TR + EN: Android `strings.xml` (`values/` EN, `values-tr/`), `AppCompatDelegate.setApplicationLocales` + `locales_config.xml`, language row in Settings; iOS `Localizable.xcstrings`, link to iOS per-app language settings. Shared code returns enums/codes, never text. Replace hardcoded `tr-TR` date formats with locale-aware ones.

### Gameplay and economy (D§1, D§2, D§7, D§8)
- [ ] Economy layer in shared: wallet (crystals + refill time, hints, freezes), play sessions, revive, hint, fail; replace demo values in `PlaygroundUiState`.
- [ ] Hearts 3 / 5 Pro; revive sheet (rewarded ad for free, free once for Pro); Hardcore = no hearts, no hints.
- [ ] 0 crystals: block starting online puzzles with a sheet (timer, watch ad +1, go Pro, play random offline).
- [ ] Hints inventory UI, out-of-hints sheet (ad +1, hint packs, Pro).
- [ ] Star rating 1–3 on the win screen and campaign map; ranking stars earned.
- [ ] Campaign **chapter map** (10 chapters × 15) in the current neon style; chapter unlock and bonus.
- [ ] Daily: today card, past-days strip (archive Pro-only), streak + auto freeze display.
- [ ] Resume draft game: persist the board on every move and on background; restore after process death.
- [ ] Offline random generator with procedural shapes (noise, symmetry, sine waves, geometry); never costs crystals, gives no ranking stars.
- [ ] Offline downloads: free 10, Pro 50 auto (Wi-Fi) + 200 manual; queued results synced idempotently.
- [ ] Big boards (≥ 16×16): pinch-zoom, pan, sticky clues, minimap; drag-fill stays precise when zoomed.
- [ ] Wrong-move warning setting; mistake feedback animation.

### Creation and AI (D§4, D§5)
- [ ] **Crafting editor**: grid (mono + colour palette), size picker (free ≤ 15, Pro ≤ 30), undo/redo, mirror, fill, live solvability check, title/description, drafts, publish.
- [ ] AI generate from prompt (stream with progress), photo-to-puzzle (camera/library picker, crop), AI colorize (Pro), AI hint for unsolvable drawings; quota chips from `GET /ai/quota`, upsell when exhausted.

### Social (D§9, D§10, D§13)
- [ ] Feed tabs wired to the real endpoints (TODO in `FeedViewModel.kt`); comments screen; report sheets; block user.
- [ ] Leaderboard screen: Weekly / Monthly / All-time / Following, own row pinned.
- [ ] Account stats + avatar loading (Coil / `AsyncImage` with cache) and avatar upload; follow lists; public profile; playlists CRUD.
- [ ] Share: branded image card (logo watermark, @creator, link) + link to `nonocraft.emreaydin.dev/p/{id}`; deep links (`applinks:` and Android `autoVerify`, `nonocraft://p/{id}`), signed-out and unknown-id cases.

### Monetization (D§3, D§11)
- [ ] RevenueCat: offerings, purchase, restore, entitlement listener, trial eligibility, hint packs; app user id = our user id (incl. anonymous).
- [ ] Paywall: prices from the store only (remove hardcoded ₺ values), yearly highlighted with trial, Terms/Privacy links, manage subscription.
- [ ] AdMob: rewarded (revive / crystal / hint, SSV with user id + `custom_data`), interstitial with the D§11 frequency rules, native ads in Explore; none for Pro; no banners.
- [ ] Google UMP consent on first launch; iOS ATT prompt after the first win.

### Platform features
- [ ] Themes from shared tokens (ARGB `Long`); remove iOS forced `UIUserInterfaceStyle: Dark`; Pro themes locked.
- [ ] Alternate app icons (iOS `CFBundleAlternateIcons`, Android `activity-alias`); Pro icons locked.
- [ ] Push: FCM both platforms, permission after the first daily solve, register with time zone/hour/locale, unregister on logout, notification settings.
- [ ] Remote config / force update from `/config` at splash; maintenance message; AI kill switch hides AI entry points.
- [ ] Sound (D§17): pick ~10 SFX from Kenney.nl (CC0) and one ambient loop from Pixabay; keep sources in `audio/CREDITS.md`; convert to `.caf`/`.m4a` (iOS) and `.ogg` (Android), < 3 MB total.
- [ ] Audio player per platform behind one shared `SoundEffect` enum: iOS `.ambient` session, Android `SoundPool` + `MediaPlayer` with audio focus; music pauses in background.
- [ ] Settings: "Sound effects" (default on), "Music" (default off), "Vibration"; haptics on fill, mistake, win.
- [ ] Win celebration (Lottie on Android, native animation on iOS) + picture reveal.

### iOS native redesign (D§15)
- [x] Deployment target iOS 18 in `project.yml` (D§15).
- [x] Build the shared glass wrapper in `iosApp/iosApp/Core/` (`GlassSurface` / `.appGlass(in: shape)` modifier + `AppGlassButtonStyle`): iOS 26 `glassEffect` / `.buttonStyle(.glass)` (and `GlassEffectContainer` for grouped elements), fallback `.ultraThinMaterial` + same shape/stroke on iOS 18–25, solid background when Reduce Transparency is on. Only this file checks `#available(iOS 26, *)`.
- [x] Rebuild screens natively for the iOS 26 look: system `TabView` bottom bar, `NavigationStack`/`NavigationSplitView` with system back buttons and toolbars (no custom top bars replacing them, e.g. retire `ScreenTopBar` where a toolbar fits), native sheets with detents, SF Symbols, large titles where fitting; keep neon accent colours and Chakra Petch for display text. Custom glass only through the wrapper.
- [x] Check every screen on an iOS 18 simulator as well as iOS 26 (fallback look, no missing APIs).
- [x] Game screen: glass control bar floating over the board; board stays opaque and high-contrast.
- [x] Respect Reduce Transparency / Increase Contrast (glass falls back to solid).

### Adaptive layout: iPhone Duo, foldables, iPad, tablets (D§15)
- [x] iPhone Duo (small outer screen + large inner screen, the lit screen follows the hinge): no fixed sizes; check every screen closed, half-open and open in the simulator. (Checked on the iPhone Duo simulator, iOS 27.2: outer 466×678 pt with the status bar at the trailing edge, inner 951×669 pt landscape, also lit when half-open.)
- [x] Game board rescales on a screen switch mid-game without losing state; clues readable on the outer screen. (Checked with the 5×5 feed puzzle. Clues are half a cell with an 8 pt floor, so boards of 15×15 and up get small clues on the outer screen until big-board zoom lands.)
- [x] iOS inner screen / iPad: landscape + two-pane layouts (Explore list + preview, Library sections + content, board + controls side by side), sidebar-adaptable tab bar. Phones stay portrait. (Checked on iPad; the Duo inner screen only through an iPad window of similar size, since the installed simulator runtime can't run the iPhone Duo.)
- [x] Android tablets / foldables: landscape + two-pane layouts (navigation rail, Explore list + preview, Library sections + content, board + controls side by side). Checked on the Resizable emulator in tablet and foldable modes.
- [x] iPad: support multitasking resizing (no `UIRequiresFullScreen`).
- [x] Android: `WindowSizeClass`, foldables incl. table-top posture (`WindowInfoTracker`), Chromebooks, config changes without state loss. (Table-top covered by `GameBoardLayoutTest`, since the emulator has no hinge sensor; desktop windowing mode isn't available on the AVD, so freeform sizes were checked with `wm size`. Keyboard and mouse play the board.)
- [x] iOS: Dynamic Type to the largest sizes.
- [x] Android: font scale to the largest sizes (checked every screen at 200 %).

### Quality
- [ ] Android: edge-to-edge, predictive back, system bars per theme, `data_extraction_rules.xml` excluding tokens, 16 KB page size.
- [ ] iOS: `PrivacyInfo.xcprivacy` (+ SDK manifests), `DEVELOPMENT_TEAM`, launch screen with logo.
- [ ] Tokens in Keychain / Keystore-backed storage; cleared on logout and deletion.
- [ ] Network edge cases: offline banner, retries, 401 → refresh once → re-auth, 429, 5xx, maintenance.
- [ ] Process death mid-game, mid-purchase, mid-AI-generation.
- [ ] Accessibility: VoiceOver/TalkBack for cells and clues, non-drag input, colour-blind patterns, Reduce Motion disables neon.
- [ ] Remove `ExampleUnitTest`/`ExampleInstrumentedTest`; tests for ViewModels, mappers, economy rules.

## To-do: production (store release)
- [ ] `Constants.API_URL` hardcoded to staging → flavours / Xcode configurations (debug/staging → staging API, release → production).
- [ ] Android release: signing, R8 rules (Ktor, kotlinx.serialization, Koin), Play App Signing, upload key backup, AAB, shared version scheme.
- [ ] iOS release: team id, App Store Connect record for `com.neilb.nonocraft`, capabilities (Push, Associated Domains, Sign in with Apple, In-App Purchase), Release framework, dSYMs.
- [ ] Crash reporting (Crashlytics or Sentry) and funnel analytics (onboarding → first solve → paywall → purchase).
- [ ] Store listings TR + EN: screenshots for every required size (iPhone 6.9"/6.5", iPad 13", Play phone/tablet/foldable), preview video, keywords, Puzzle category, age rating questionnaire (UGC + ads), support URL, marketing URL.
- [ ] App Privacy (Apple) and Data Safety (Play) matching the shipped SDKs (RevenueCat, AdMob, FCM, crash reporting).
- [ ] Review notes + demo account with Pro.
- [ ] Store products per D§3 (subscription group, trial, TRY prices, Family Sharing off, hint packs).
- [ ] Play closed testing: ≥ 12 testers for 14 days before production access (personal accounts) — start early.
- [ ] Performance: cold start < 2 s, smooth 30×30 board at 60/120 fps, low-end Android (minSdk 24), app size.
- [ ] Final edge cases: first launch offline, session expired after months, clock changes, low storage, pending purchases (Ask to Buy), refund revoking Pro, subscription expiring mid-session, deleting an account with an active subscription.
