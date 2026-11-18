import SwiftUI
import UIKit
import XCTest
@testable import Nonocraft

/// VoiceOver reads every clue and cell on the drawn board, and the cell actions reach the game.
@MainActor
final class BoardAccessibilityTests: XCTestCase {
    private var moves: [String] = []
    private var window: UIWindow?

    override class func setUp() {
        super.setUp()
        // SwiftUI builds its accessibility tree only while assistive technology is on; turn it on for this
        // test process the way KIF does (test target only).
        if let handle = dlopen("/usr/lib/libAccessibility.dylib", RTLD_NOW),
           let symbol = dlsym(handle, "_AXSApplicationAccessibilitySetEnabled") {
            typealias SetEnabled = @convention(c) (Bool) -> Void
            unsafeBitCast(symbol, to: SetEnabled.self)(true)
        }
    }

    override func tearDown() {
        window?.isHidden = true
        window = nil
        moves = []
    }

    private func render(paletteHex: [String], userGrid: [[Int]]) {
        let board = GameGridArea(
            size: 3,
            palette: paletteHex.map { Color(hexString: $0) },
            paletteHex: paletteHex,
            userGrid: userGrid,
            rowClues: [[GridClue(count: 1, colorIndex: 1), GridClue(count: 1, colorIndex: 2)], [GridClue(count: 3, colorIndex: 1)], []],
            colClues: Array(repeating: [GridClue(count: 2, colorIndex: 1)], count: 3),
            cursor: nil,
            onCellTouched: { [unowned self] row, col in moves.append("tap \(row) \(col)") },
            cellActions: BoardCellActions(
                fill: { [unowned self] row, col, color in moves.append("fill \(row) \(col) \(color)") },
                cross: { [unowned self] row, col in moves.append("cross \(row) \(col)") },
                clear: { [unowned self] row, col in moves.append("clear \(row) \(col)") }
            )
        )
        let host = UIHostingController(rootView: board.frame(width: 300, height: 300))
        let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 300, height: 300))
        window.rootViewController = host
        window.makeKeyAndVisible()
        host.view.layoutIfNeeded()
        self.window = window
        RunLoop.main.run(until: Date().addingTimeInterval(0.5))
    }

    /// All accessibility elements under the window, as VoiceOver would walk them.
    private func elements() -> [NSObject] {
        var found: [NSObject] = []
        func walk(_ object: NSObject) {
            if object.isAccessibilityElement { found.append(object) }
            var children: [Any] = object.accessibilityElements ?? []
            let count = object.accessibilityElementCount()
            if children.isEmpty, count > 0, count != NSNotFound {
                children = (0..<count).compactMap { object.accessibilityElement(at: $0) }
            }
            if let view = object as? UIView { children += view.subviews }
            children.compactMap { $0 as? NSObject }.forEach(walk)
        }
        window.map(walk)
        if found.isEmpty { print("BOARD-AX none; window tree: \(window?.subviews.map { "\($0)" } ?? [])") }
        return found
    }

    private func element(_ label: String) throws -> NSObject {
        try XCTUnwrap(elements().first { $0.accessibilityLabel == label }, "no element '\(label)'")
    }

    private func actionNames(_ label: String) throws -> [String] {
        try element(label).accessibilityCustomActions?.map(\.name) ?? []
    }

    private func perform(_ actionName: String, on label: String) throws {
        let action = try XCTUnwrap(element(label).accessibilityCustomActions?.first { $0.name == actionName })
        _ = action.actionHandler?(action)
    }

    func testCluesAndCellsReadAsPhrases() throws {
        render(paletteHex: ["#FF0000", "#0000FF"], userGrid: [[0, -1, 2], [0, 0, 0], [0, 0, 0]])
        XCTAssertNoThrow(try element("Satır 1: 1 kırmızı, 1 mavi"))
        XCTAssertNoThrow(try element("Satır 3: boş"))
        XCTAssertNoThrow(try element("Sütun 2: 2 kırmızı"))
        XCTAssertEqual(try element("Satır 1, sütun 1").accessibilityValue, "boş")
        XCTAssertEqual(try element("Satır 1, sütun 2").accessibilityValue, "çarpı")
        XCTAssertEqual(try element("Satır 1, sütun 3").accessibilityValue, "dolu, renk 2, mavi")
    }

    func testEmptyCellOffersFillPerColourAndCross() throws {
        render(paletteHex: ["#FF0000", "#0000FF"], userGrid: [[0, -1, 2], [0, 0, 0], [0, 0, 0]])
        XCTAssertEqual(try actionNames("Satır 1, sütun 1"), ["Doldur: renk 1, kırmızı", "Doldur: renk 2, mavi", "Çarpı koy"])
        XCTAssertEqual(try actionNames("Satır 1, sütun 2"), ["Doldur: renk 1, kırmızı", "Doldur: renk 2, mavi", "Temizle"])
        XCTAssertEqual(try actionNames("Satır 1, sütun 3"), [])

        try perform("Doldur: renk 2, mavi", on: "Satır 1, sütun 1")
        try perform("Çarpı koy", on: "Satır 1, sütun 1")
        try perform("Temizle", on: "Satır 1, sütun 2")
        XCTAssertTrue(try element("Satır 2, sütun 3").accessibilityActivate())
        XCTAssertEqual(moves, ["fill 0 0 2", "cross 0 0", "clear 0 1", "tap 1 2"])
    }

    func testSingleColourPuzzleSaysFillAndDoesNotNameTheColour() throws {
        render(paletteHex: ["#FFFFFF"], userGrid: [[1, 0, 0], [0, 0, 0], [0, 0, 0]])
        XCTAssertNoThrow(try element("Satır 1: 1, 1"))
        XCTAssertEqual(try element("Satır 1, sütun 1").accessibilityValue, "dolu")
        XCTAssertEqual(try actionNames("Satır 1, sütun 2"), ["Doldur", "Çarpı koy"])
    }
}
