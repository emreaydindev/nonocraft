import XCTest

/// Walks the main screens with a fresh install, prints each screen's accessibility tree and runs Xcode's
/// accessibility audit. Issues are printed, not failed on, so one run lists everything; grep the log for
/// "AX-TREE" and "AX-ISSUE". AX_LANG=tr (TEST_RUNNER_AX_LANG from xcodebuild) runs the app in Turkish; English otherwise.
final class AccessibilityAuditTests: XCTestCase {
    private let app = XCUIApplication()
    private let isTurkish = ProcessInfo.processInfo.environment["AX_LANG"] == "tr"

    override func setUp() {
        continueAfterFailure = true
        app.launchArguments += ["-AppleLanguages", isTurkish ? "(tr)" : "(en)", "-AppleLocale", isTurkish ? "tr_TR" : "en_US"]
        app.launch()
    }

    /// The label in the language the app runs in.
    private func t(_ en: String, _ tr: String) -> String { isTurkish ? tr : en }
    private var skip: String { t("Skip", "Atla") }
    private var guest: String { t("Continue as guest", "Misafir olarak devam et") }
    private var exploreTab: String { t("Explore", "Keşfet") }
    private func cellLabel(_ row: Int, _ col: Int) -> String { t("Row \(row), column \(col)", "Satır \(row), sütun \(col)") }
    private var postPlay: XCUIElement { app.buttons["post_play"].firstMatch }

    /// Opens Explore and waits for the feed (the staging feed may need a second try).
    private func openFeedPost() -> XCUIElement {
        app.tabBars.buttons[exploreTab].tap()
        if !postPlay.waitForExistence(timeout: 8) {
            app.buttons[t("Newest", "En Yeniler")].tap(); sleep(2)
            app.buttons[t("Featured", "Öne Çıkanlar")].tap()
        }
        return postPlay
    }

    func testMainScreens() throws {
        // Splash, then onboarding, auth and tutorial on a fresh install.
        if app.buttons[skip].waitForExistence(timeout: 15) {
            report("Onboarding")
            app.buttons[skip].tap()
        }
        if app.buttons[guest].waitForExistence(timeout: 5) {
            report("AuthGateway")
            app.buttons[guest].tap()
        }
        if app.buttons[skip].waitForExistence(timeout: 5) {
            report("Tutorial")
            app.buttons[skip].tap()
        }
        XCTAssertTrue(app.tabBars.firstMatch.waitForExistence(timeout: 10))
        for (screen, tab) in [("Playground", t("Play", "Oyun Alanı")), ("Explore", exploreTab),
                              ("Library", t("Collection", "Koleksiyon")), ("Profile", t("Profile", "Profil"))] {
            app.tabBars.buttons[tab].tap()
            sleep(3)
            report(screen)
        }
        app.tabBars.buttons[t("Profile", "Profil")].tap()
        let premium = app.buttons.matching(NSPredicate(format: "label BEGINSWITH %@", t("Go Premium", "Premium'a Geç"))).firstMatch
        if premium.waitForExistence(timeout: 3) {
            premium.tap()
            sleep(2)
            report("Paywall")
            app.buttons[t("Close", "Kapat")].firstMatch.tap()
            sleep(1)
        }
        if app.buttons[t("Settings", "Ayarlar")].waitForExistence(timeout: 3) {
            app.buttons[t("Settings", "Ayarlar")].tap()
            sleep(2)
            report("Settings")
            app.navigationBars.buttons.firstMatch.tap()
        }
        // The 5×5 feed post opens a game.
        let play = openFeedPost()
        if play.waitForExistence(timeout: 8) {
            play.tap()
            sleep(4)
            report("Game")
        }
    }

    /// The board's VoiceOver elements: clues, cells with state, the cursor stick (adjustable) and the place
    /// button. Custom actions are checked in the Accessibility Inspector; XCUITest cannot run them.
    func testGameBoard() throws {
        for label in [skip, guest, skip] where app.buttons[label].waitForExistence(timeout: 6) {
            app.buttons[label].tap()
        }
        XCTAssertTrue(app.tabBars.firstMatch.waitForExistence(timeout: 10))
        let play = openFeedPost()
        XCTAssertTrue(play.waitForExistence(timeout: 8))
        play.tap()
        let cell = app.buttons[cellLabel(1, 2)]
        XCTAssertTrue(cell.waitForExistence(timeout: 8))
        report("GameBoard")
        print("AX-GAME cell r1c2 value=\(cell.value ?? "-") frame=\(cell.frame)")
        print("AX-GAME clue row1=\(app.descendants(matching: .any).matching(NSPredicate(format: "label BEGINSWITH %@", t("Row 1:", "Satır 1:"))).firstMatch.label)")
        cell.tap()
        sleep(1)
        print("AX-GAME cell r1c2 after tap value=\(app.buttons[cellLabel(1, 2)].value ?? "-")")
        let stick = app.descendants(matching: .any)[t("Cursor", "İmleç")].firstMatch
        print("AX-GAME stick exists=\(stick.exists) value=\(stick.value ?? "-")")
        app.buttons[t("Fill the square under the cursor", "İmleçteki kareyi doldur")].tap(); sleep(1)
        print("AX-GAME after place r1c3=\(app.buttons[cellLabel(1, 3)].value ?? "-") hearts=\(app.otherElements.matching(NSPredicate(format: "label ENDSWITH %@", t("left", "can kaldı"))).firstMatch.label)")
        // AX_HOLD=<seconds> (TEST_RUNNER_AX_HOLD from xcodebuild) keeps the game open for the Accessibility Inspector.
        sleep(UInt32(ProcessInfo.processInfo.environment["AX_HOLD"] ?? "") ?? 0)
    }

    /// Colour-blind shapes on the board, clues and palette (setting forced on unless AX_SYSTEM_DEFAULT=1). AX_HOLD keeps it open for a
    /// screenshot.
    func testColorPatterns() throws {
        app.terminate()
        // AX_SYSTEM_DEFAULT=1 leaves the setting unset, to check it follows Differentiate Without Colour.
        if ProcessInfo.processInfo.environment["AX_SYSTEM_DEFAULT"] != "1" {
            app.launchArguments += ["-a11yColorPatterns", "YES"]
        }
        app.launch()
        for label in [skip, guest, skip] where app.buttons[label].waitForExistence(timeout: 6) {
            app.buttons[label].tap()
        }
        XCTAssertTrue(app.tabBars.firstMatch.waitForExistence(timeout: 10))
        let play = openFeedPost()
        XCTAssertTrue(play.waitForExistence(timeout: 10))
        play.tap()
        XCTAssertTrue(app.buttons[cellLabel(1, 1)].waitForExistence(timeout: 8))
        for col in 1...5 { app.buttons[cellLabel(1, col)].tap() }
        print("AX-PATTERNS ready")
        sleep(UInt32(ProcessInfo.processInfo.environment["AX_HOLD"] ?? "") ?? 0)
    }

    private func report(_ screen: String) {
        print("AX-TREE \(screen)\n\(app.debugDescription)\nAX-TREE-END \(screen)")
        // AX_SHOTS=<folder> saves a screenshot of each screen next to its audit (the simulator writes to the Mac).
        if let folder = ProcessInfo.processInfo.environment["AX_SHOTS"] {
            try? XCUIScreen.main.screenshot().pngRepresentation
                .write(to: URL(fileURLWithPath: folder).appendingPathComponent("\(screen).png"))
        }
        do {
            try app.performAccessibilityAudit { issue in
                let element = issue.element.map { "\($0.elementType.rawValue) '\($0.label)' \($0.frame)" } ?? "-"
                print("AX-ISSUE \(screen): \(issue.auditType) | \(issue.compactDescription) | \(element) | \(issue.detailedDescription)")
                return true
            }
        } catch {
            print("AX-ISSUE \(screen): audit failed \(error)")
        }
    }
}
