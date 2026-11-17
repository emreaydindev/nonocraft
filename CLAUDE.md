# nonocraft_mobile

Kotlin Multiplatform app: `shared/` (domain, data, ViewModels, puzzle engine), `androidApp/` (Compose), `iosApp/` (SwiftUI, XcodeGen). Branch `kmp`. Architecture rules live in `../CLAUDE.md`. Open product questions are in `../QUESTIONS.md`; items that depend on an answer are marked (Q#).

## What the design promises (read before building a screen)
The Android screens are the design source; iOS mirrors them. The design already tells the product story, so build to it rather than inventing:
- **Playground (home):** greeting, crystal counter with a refill timer when empty, "Günün Bulmacası" card with past-days strip, current campaign level card, "Yarım kalan bölüm" resume card (board size + % progress), "Rastgele Bölüm Oluştur" simulation that works offline.
- **Random puzzle dialog:** sizes 5×5…20×20, modes Classic / Hardcore / Colored, lives count.
- **Explore (Feed):** tabs, "Oluştur" (create) entry, post cards with like/comment/share/download.
- **Library:** Liked, Daily Adventure, Playlists (+ new list), Shared, Drafts, Solved, Downloaded; some sections tagged PRO.
- **Account / Profile:** avatar, stats row, Pro badge / gold highlight, guest mode ("İlerlemeni kaybetmemek için giriş yap").
- **Game:** timer, hearts, Place / Cross / Hint, game over and win dialogs; sound + Lottie on win (TODO in `GameScreen.kt`).
- **Settings:** theme (System, Dark, Light, AMOLED + Pro Cyberpunk, Retro Wave), app icon (Classic, Night + Pro Neon Cube, 8-Bit, Synthwave, Gold Crown), neon effects, wrong-move warning, sound, vibration, notifications, edit profile, password, delete account, privacy, terms, contact.
- **Premium paywall:** monthly / yearly plans, perks list, trial, "processing" and "welcome" states.
- **Crafting (create puzzle):** empty placeholder on both platforms — the biggest missing screen.

## To-do: staging (internal beta / TestFlight against staging API)

### Missing features (shared + both UIs)
- [ ] **Crafting / puzzle editor**: draw grid (mono + colour palette), size picker (free vs Pro limits, Q5), undo/redo, mirror tools, fill, live solvability check via `POST /puzzles/check-solvability`, title/description, save draft, publish to community, AI generate from prompt (`/ai/generate/stream` with progress), AI colorize (Q2), photo-to-puzzle (Q6), quota display (`GET /ai/quota`).
- [ ] **Economy**: `EconomyApiService` + repository + use cases for `GET /economy/wallet`, `POST /economy/spend` (hint, life refill, streak freeze). Replace demo values in `PlaygroundUiState` (`crystals`, `crystalRefillMinutes`, `currentLevelNumber`, `hasDraft`, `draftSize`, `draftProgress`).
- [ ] **Daily**: hook the Playground daily card and "past days" strip to `/daily/today`, `/daily/calendar`, `/daily/streak`; daily archive behind Pro if sold (Q4).
- [ ] **Campaign progress**: `/puzzles/levels/progress` and `next-level-info` instead of hardcoded `currentLevelNumber = 3`; level-select map/list (Q13).
- [ ] **Resume draft game**: persist in-progress board locally (multiplatform-settings or a file) on every move and on background; restore after process death.
- [ ] **Feed tabs**: wire `/feed/featured`, `/feed/following`, `/feed/most-liked` (TODO in `FeedViewModel.kt`). Comments screen, report sheet, block user (needed for App Review 1.2).
- [ ] **Offline download** (TODO in `FeedViewModel`/`ProfileViewModel`): save puzzles locally, "Downloaded" library section, auto-download next N levels (Q4).
- [ ] **Account stats**: solved/followers/following (TODO in `AccountViewModel.kt`); avatar loading (Coil on Android, `AsyncImage` + cache on iOS) and avatar upload (`PUT /users/me/avatar`).
- [ ] **Follow lists**, public profile puzzles, playlists CRUD screens.
- [ ] **Leaderboard** screen if ranking is part of v1 (Q14).
- [ ] **Guest mode** end to end: what is saved locally, how it merges on sign-up (Q11).
- [ ] **Email verification** screen/flow (`/auth/verify-email`) if required (Q10).
- [ ] **Sign in with Apple** on iOS (required by App Store because Google sign-in is offered); backend `POST /auth/apple` exists. Google sign-in on iOS (GoogleSignIn SDK) if not done.
- [ ] **Hints**: hint reveals a correct cell/line, costs crystals or free for Pro (Q1), animated.
- [ ] **Wrong-move warning** setting actually toggles mistake feedback; Hardcore mode = no warning?
- [ ] **Sound effects** (wrong, correct pop, win) and **haptics**, respecting the settings toggles and the device silent switch on iOS.
- [ ] **Win celebration**: Lottie (Android) / equivalent on iOS, reveal the coloured picture, stats (time, hearts left, crystals earned), share button.
- [ ] **Themes**: Light/AMOLED/Cyberpunk/Retro Wave are listed but iOS forces `UIUserInterfaceStyle: Dark`. Implement theme tokens in shared (ARGB `Long`) and apply on both platforms; lock Pro themes.
- [ ] **Alternate app icons**: iOS `CFBundleAlternateIcons` + `setAlternateIconName`; Android `activity-alias` per icon. Lock Pro icons.
- [ ] **Neon effects** toggle (glows/animations off = also battery saver / reduce motion).
- [ ] **Notifications**: FCM on both platforms (APNs key in Firebase), permission prompt at a good moment (after first daily solve, not at launch), `POST /notifications/register` with time zone and reminder hour, unregister on logout, notification settings screen wired to `/notifications/preferences`.
- [ ] **Deep links**: `https://nonocraft.emreaydin.dev/p/{id}` (Associated Domains `applinks:`, Android `autoVerify` intent filter) and `nonocraft://p/{id}`; open the game, handle signed-out and unknown-id cases. Share sheet from post cards and win screen.
- [ ] **Monetization SDKs**: RevenueCat (offerings, purchase, restore, entitlement listener, trial eligibility), AdMob rewarded ads with SSV `user_id` = our user id, UMP consent form (GDPR/KVKK) and iOS App Tracking Transparency before ads load.
- [ ] **Paywall**: real store prices from RevenueCat (today hardcoded ₺49.90 / ₺599 / ₺89 vs catalog $4.99 / $29.99 / $59.99, Q15), lifetime plan, trial text, Restore Purchases, Terms + Privacy links (App Store requirement), manage subscription link.
- [ ] **Remote config / force update**: read `/config` at splash; block below `minVersion`, soft prompt below `latestVersion`; hide features flagged off.
- [ ] **Localization** (Q9): all UI strings are hardcoded Turkish (Compose literals and Swift literals). Move to Compose Multiplatform resources or `strings.xml` + `Localizable.xcstrings`; localize backend error codes; date formats currently hardcoded `tr-TR`.

### Platform quality
- [ ] **Bundle / application id mismatch**: Android `com.neilb.nonocraft`, iOS `dev.emreaydin.nonocraft` (Q12). Changing Android's id after release is impossible — decide now.
- [ ] Android: edge-to-edge (enforced at targetSdk 35+), predictive back, dark/light system bars per theme, `data_extraction_rules.xml` TODO (exclude tokens from backup), 16 KB page-size compatible native libs.
- [ ] iOS: privacy manifest `PrivacyInfo.xcprivacy` (required reason APIs: UserDefaults, file timestamps) incl. third-party SDK manifests; `DEVELOPMENT_TEAM` empty in `project.yml`; launch screen with logo instead of empty `UILaunchScreen`.
- [ ] Token storage: tokens in Keychain (iOS) / EncryptedSharedPreferences or DataStore + Keystore (Android), not plain settings. Clear on logout and account deletion.
- [ ] Network edge cases: offline banner, retry, request timeouts, 401 → refresh → retry once → logout, 429 rate-limit message, 5xx message, maintenance mode via `/config`.
- [ ] Process death / backgrounding mid-game, mid-purchase, mid-AI-generation (stream reconnect or resume by job id).
- [ ] Remove the leftover `ExampleUnitTest` / `ExampleInstrumentedTest` and add real tests: engine (exists), ViewModels, mappers.

### Adaptive layout: iPhone Duo, foldables, iPad, tablets
- [ ] iPhone Duo (small outer screen + large inner screen, iOS picks the lit screen by hinge pose): no fixed widths/heights; layout from `GeometryReader`/size classes. Check every screen in the closed, half-open and open poses in the simulator.
- [ ] Game board must rescale when the screen switches mid-game without losing state (timer keeps running correctly, selection/drag cancelled cleanly). Clue numbers stay readable on the outer screen; allow pinch-zoom/pan on big boards (20×20+).
- [ ] Inner (large) screen: two-pane layouts where they help — Explore list + puzzle preview, Library sections + content, game board + clue/side controls; the bottom tab bar can become a sidebar (`NavigationSplitView` / `TabView` sidebar-adaptable).
- [ ] Decide orientation support (Q16): today iOS is portrait-only. The inner screen and iPad likely need landscape (board left, controls right).
- [ ] iPad: decide whether to ship as iPad app (Q16); if yes, multitasking (Split View / Slide Over / Stage Manager resizing) means not setting `UIRequiresFullScreen`.
- [ ] Android: `WindowSizeClass` for compact/medium/expanded, foldables (Galaxy Z Fold/Flip, Pixel Fold) including table-top posture via `WindowInfoTracker`, Chromebooks, `resizeableActivity`, config changes without losing game state.
- [ ] Dynamic Type / font scale up to the largest accessibility sizes; the board keeps its size while labels wrap.

### Accessibility
- [ ] VoiceOver / TalkBack labels for cells ("row 3 column 5, filled"), clues and controls; a non-drag input mode.
- [ ] Colour-blind safe palettes for coloured puzzles (pattern overlay option), contrast in every theme, Reduce Motion disables neon/animations.

## To-do: production (store release)
- [ ] `Constants.API_URL` is hardcoded to staging. Build flavours / Xcode configurations: debug → staging, release → production; staging builds get a separate bundle id suffix (`.staging`, already in `APPLE_AUDIENCES`) and app name.
- [ ] Android release: signing config, `isMinifyEnabled` + R8 rules for Ktor/kotlinx.serialization/Koin, Play App Signing, upload key backup, AAB, `versionCode` scheme shared with iOS build numbers.
- [ ] iOS release: team id, App Store Connect app record, capabilities (Push, Associated Domains, Sign in with Apple, In-App Purchase), archive with the shared framework in Release config, dSYMs upload.
- [ ] Crash reporting (Firebase Crashlytics or Sentry) with KMP stack traces; analytics events for funnel (onboarding → first solve → paywall → purchase).
- [ ] Store listings: screenshots for every required size (6.9", 6.5", iPad 13" if iPad, Play phone/tablet/foldable), app preview video, descriptions TR + EN, keywords, category Puzzle, age rating (UGC + ads → answer honestly), support URL, marketing URL.
- [ ] App Privacy (Apple) and Data Safety (Play) forms matching the SDKs actually shipped.
- [ ] Review notes: demo account with Pro on staging-like prod user, explanation of AI generation and UGC moderation.
- [ ] Subscription setup in both stores: groups, trial, localized prices, tax category, promotional offers (optional), family sharing (Q15).
- [ ] Play: closed testing track with ≥12 testers for 14 days (required for new personal developer accounts) — plan this early.
- [ ] Performance: cold start < 2 s, board rendering at 60/120 fps on 25×25, memory on low-end Android (minSdk 24), APK/IPA size.
- [ ] Last pass on edge cases: no network on first launch, expired session after months, clock changes, low storage, purchase pending (Ask to Buy / slow card), refund revoking Pro, subscription expiring mid-session, user deletes account with active subscription (tell them to cancel in the store).
