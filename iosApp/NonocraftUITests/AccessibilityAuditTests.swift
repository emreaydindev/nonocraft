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
