import XCTest

/// Walks the main screens with a fresh install, prints each screen's accessibility tree and runs Xcode's
/// accessibility audit. Issues are printed, not failed on, so one run lists everything; grep the log for
/// "AX-TREE" and "AX-ISSUE".
final class AccessibilityAuditTests: XCTestCase {
    private let app = XCUIApplication()

    override func setUp() {
        continueAfterFailure = true
        app.launch()
    }

    func testMainScreens() throws {
        // Splash, then onboarding, auth and tutorial on a fresh install.
        if app.buttons["Atla"].waitForExistence(timeout: 15) {
            report("Onboarding")
            app.buttons["Atla"].tap()
        }
        if app.buttons["Misafir olarak devam et"].waitForExistence(timeout: 5) {
            report("AuthGateway")
            app.buttons["Misafir olarak devam et"].tap()
        }
        if app.buttons["Atla"].waitForExistence(timeout: 5) {
            report("Tutorial")
            app.buttons["Atla"].tap()
        }
        XCTAssertTrue(app.tabBars.firstMatch.waitForExistence(timeout: 10))
        for tab in ["Oyun Alanı", "Keşfet", "Koleksiyon", "Profil"] {
            app.tabBars.buttons[tab].tap()
            sleep(3)
            report(tab)
        }
        app.tabBars.buttons["Profil"].tap()
        let premium = app.buttons.matching(NSPredicate(format: "label BEGINSWITH %@", "Premium'a Geç")).firstMatch
        if premium.waitForExistence(timeout: 3) {
            premium.tap()
            sleep(2)
            report("Paywall")
            app.buttons["Kapat"].firstMatch.tap()
            sleep(1)
        }
        if app.buttons["Ayarlar"].waitForExistence(timeout: 3) {
            app.buttons["Ayarlar"].tap()
            sleep(2)
            report("Settings")
            app.navigationBars.buttons.firstMatch.tap()
        }
        // The 5×5 feed post opens a game (the staging feed may need a second try).
        app.tabBars.buttons["Keşfet"].tap()
        var play = app.buttons["Oyna"].firstMatch
        if !play.waitForExistence(timeout: 8) {
            app.buttons["En Yeniler"].tap(); sleep(2)
            app.buttons["Öne Çıkanlar"].tap()
            play = app.buttons["Oyna"].firstMatch
        }
        if play.waitForExistence(timeout: 8) {
            play.tap()
            sleep(4)
            report("Game")
        }
    }

    /// The board's VoiceOver elements: clues, cells with state, the cursor stick (adjustable) and the place
    /// button. Custom actions are checked in the Accessibility Inspector; XCUITest cannot run them.
    func testGameBoard() throws {
        for skip in ["Atla", "Misafir olarak devam et", "Atla"] where app.buttons[skip].waitForExistence(timeout: 6) {
            app.buttons[skip].tap()
        }
        XCTAssertTrue(app.tabBars.firstMatch.waitForExistence(timeout: 10))
        app.tabBars.buttons["Keşfet"].tap()
        var play = app.buttons["Oyna"].firstMatch
        if !play.waitForExistence(timeout: 8) {
            app.buttons["En Yeniler"].tap(); sleep(2)
            app.buttons["Öne Çıkanlar"].tap()
            play = app.buttons["Oyna"].firstMatch
        }
        XCTAssertTrue(play.waitForExistence(timeout: 8))
        play.tap()
        let cell = app.buttons["Satır 1, sütun 2"]
        XCTAssertTrue(cell.waitForExistence(timeout: 8))
        report("GameBoard")
        print("AX-GAME cell r1c2 value=\(cell.value ?? "-") frame=\(cell.frame)")
        print("AX-GAME clue row1=\(app.descendants(matching: .any).matching(NSPredicate(format: "label BEGINSWITH 'Satır 1:'")).firstMatch.label)")
        cell.tap()
        sleep(1)
        print("AX-GAME cell r1c2 after tap value=\(app.buttons["Satır 1, sütun 2"].value ?? "-")")
        let stick = app.descendants(matching: .any)["İmleç"].firstMatch
        print("AX-GAME stick exists=\(stick.exists) value=\(stick.value ?? "-")")
        app.buttons["İmleçteki kareyi doldur"].tap(); sleep(1)
        print("AX-GAME after place r1c3=\(app.buttons["Satır 1, sütun 3"].value ?? "-") hearts=\(app.otherElements.matching(NSPredicate(format: "label ENDSWITH 'can kaldı'")).firstMatch.label)")
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
        for skip in ["Atla", "Misafir olarak devam et", "Atla"] where app.buttons[skip].waitForExistence(timeout: 6) {
            app.buttons[skip].tap()
        }
        XCTAssertTrue(app.tabBars.firstMatch.waitForExistence(timeout: 10))
        app.tabBars.buttons["Keşfet"].tap()
        let play = app.buttons["Oyna"].firstMatch
        XCTAssertTrue(play.waitForExistence(timeout: 10))
        play.tap()
        XCTAssertTrue(app.buttons["Satır 1, sütun 1"].waitForExistence(timeout: 8))
        for col in 1...5 { app.buttons["Satır 1, sütun \(col)"].tap() }
        print("AX-PATTERNS ready")
        sleep(UInt32(ProcessInfo.processInfo.environment["AX_HOLD"] ?? "") ?? 0)
    }

    private func report(_ screen: String) {
        print("AX-TREE \(screen)\n\(app.debugDescription)\nAX-TREE-END \(screen)")
        do {
            try app.performAccessibilityAudit { issue in
                let element = issue.element.map { "\($0.elementType.rawValue) '\($0.label)' \($0.frame)" } ?? "-"
                print("AX-ISSUE \(screen): \(issue.auditType) | \(issue.compactDescription) | \(element)")
                return true
            }
        } catch {
            print("AX-ISSUE \(screen): audit failed \(error)")
        }
    }
}
