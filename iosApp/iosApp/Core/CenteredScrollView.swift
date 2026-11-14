import SwiftUI

/// Scrolls when the content is taller than the space available, otherwise centres it vertically
/// (Compose: `verticalScroll` + `Arrangement.Center`).
struct CenteredScrollView<Content: View>: View {
    @ViewBuilder var content: Content

    var body: some View {
        GeometryReader { proxy in
            ScrollView {
                content
                    .frame(maxWidth: .infinity)
                    .frame(minHeight: proxy.size.height, alignment: .center)
            }
            .scrollBounceBehavior(.basedOnSize)
            .scrollDismissesKeyboard(.interactively)
        }
    }
}
