import SwiftUI

extension View {
    /// Caps the content at a comfortable width and centres it, so single-column screens don't stretch
    /// edge to edge on iPad, the iPhone Duo inner screen or a wide multitasking window. Phones are narrower
    /// than the cap and are unaffected.
    func readableWidth(_ maxWidth: CGFloat = 560) -> some View {
        frame(maxWidth: maxWidth).frame(maxWidth: .infinity)
    }
}
