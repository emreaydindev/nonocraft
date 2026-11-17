import Shared
import SwiftUI

@main
struct NonocraftApp: App {

    init() {
        KoinHelperKt.doInitKoin()
        Self.useDisplayFontForNavigationTitles()
    }

    /// Navigation titles are display text, so they use Chakra Petch (DECISIONS §15). Only the title
    /// fonts change; the bars themselves keep the system (Liquid Glass) look.
    private static func useDisplayFontForNavigationTitles() {
        let appearance = UINavigationBar.appearance()
        if let large = UIFont(name: "ChakraPetch-Bold", size: 34) {
            appearance.largeTitleTextAttributes = [.font: UIFontMetrics(forTextStyle: .largeTitle).scaledFont(for: large)]
        }
        if let inline = UIFont(name: "ChakraPetch-Bold", size: 18) {
            appearance.titleTextAttributes = [.font: UIFontMetrics(forTextStyle: .headline).scaledFont(for: inline)]
        }
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .preferredColorScheme(.dark)
        }
    }
}
