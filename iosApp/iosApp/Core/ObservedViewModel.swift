import Shared
import SwiftUI

/// Binds one shared ViewModel and its `StateFlow` to a SwiftUI screen.
///
/// Create it with `@StateObject` so SwiftUI builds it once per screen: the ViewModel, the state
/// subscription and the owner that clears it all live and die together.
@MainActor
final class ObservedViewModel<ViewModel: AnyObject, State: AnyObject>: ObservableObject {
    let viewModel: ViewModel
    @Published private(set) var state: State

    private let owner: ViewModelOwner
    private var subscription: Task<Void, Never>?

    init(
        _ resolve: (ViewModelOwner) -> ViewModel,
        state flow: (ViewModel) -> SkieSwiftStateFlow<State>
    ) {
        let owner = ViewModelOwner()
        let viewModel = resolve(owner)
        let stateFlow = flow(viewModel)
        self.owner = owner
        self.viewModel = viewModel
        self.state = stateFlow.value
        subscription = Task { [weak self] in
            for await next in stateFlow {
                self?.state = next
            }
        }
    }

    deinit {
        subscription?.cancel()
        owner.clear()
    }
}

/// Holds a shared ViewModel that exposes no state flow (only actions and one-off events).
@MainActor
final class HeldViewModel<ViewModel: AnyObject>: ObservableObject {
    let viewModel: ViewModel
    private let owner: ViewModelOwner

    init(_ resolve: (ViewModelOwner) -> ViewModel) {
        let owner = ViewModelOwner()
        self.owner = owner
        viewModel = resolve(owner)
    }

    deinit {
        owner.clear()
    }
}
