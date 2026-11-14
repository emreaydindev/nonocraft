import Shared
import SwiftUI

/// Keeps a screen's shared ViewModels alive for the lifetime of the SwiftUI view that owns it
/// (use with `@StateObject`) and clears them, cancelling their coroutines, when the view goes away.
@MainActor
final class ViewModelStore: ObservableObject {
    let owner = ViewModelOwner()

    deinit {
        owner.clear()
    }
}
