import Foundation

enum AuthRoute: Hashable {
    case login, register, forgotPassword, resetPassword
}

enum SettingsRoute: Hashable {
    case theme, appIcon, editProfile, changePassword, notifications
}

/// Screens pushed on top of the dashboard.
enum AppRoute: Hashable {
    case settings
    case settingsSub(SettingsRoute)
    case premium
    case crafting
    case tutorial
    case game(id: String)
    case libraryDetail(id: String)
    case profile(userId: String)
}
