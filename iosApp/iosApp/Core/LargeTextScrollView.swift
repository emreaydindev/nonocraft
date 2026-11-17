import SwiftUI

/// For screens laid out as one fixed page (onboarding, tutorial, auth gateway): the page as designed at
/// regular text sizes, and a scroll view at accessibility sizes, where its text no longer fits the screen.
/// Flexible spacers and visuals take their natural size while scrolling.
struct LargeTextScrollView<Content: View>: View {
    @ViewBuilder var content: Content

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        if dynamicTypeSize.isAccessibilitySize {
            ScrollView {
                content.frame(maxWidth: .infinity)
            }
        } else {
            content
        }
    }
}
