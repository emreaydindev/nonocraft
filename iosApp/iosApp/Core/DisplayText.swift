import Foundation
import Shared

/// Where an error is shown, so the same code can read differently per screen.
enum ErrorContext {
    case general, login, register, forgotPassword, resetPassword, changePassword, editProfile, deleteAccount, feed, game
}

extension ErrorCode {
    /// Text for a shared error code on the screen it appears on.
    func message(_ context: ErrorContext = .general) -> String {
        switch (self, context) {
        case (.network, _): return L10n.string("error_network")
        case (.unauthorized, .login), (.invalidCredentials, _): return L10n.string("error_invalid_credentials")
        case (.conflict, .register): return L10n.string("error_email_or_username_taken")
        case (.unauthorized, .changePassword), (.wrongPassword, _): return L10n.string("error_wrong_password")
        case (.notFound, .resetPassword), (.validation, .resetPassword), (.invalidCode, _): return L10n.string("error_invalid_code")
        case (.conflict, .editProfile), (.usernameTaken, _): return L10n.string("error_username_taken")
        case (.notFound, .game), (.puzzleNotFound, _): return L10n.string("error_puzzle_not_found")
        case (.emailTaken, _): return L10n.string("error_email_taken")
        case (.sessionExpired, _), (.unauthorized, _): return L10n.string("error_session_expired")
        case (.emailNotVerified, _): return L10n.string("error_email_not_verified")
        case (.insufficientCrystals, _): return L10n.string("error_insufficient_crystals")
        case (.dailyLimitReached, _): return L10n.string("error_daily_limit")
        case (.googleSignInFailed, _): return L10n.string("error_google_sign_in")
        case (.rateLimited, _): return L10n.string("error_rate_limited")
        case (.server, _): return L10n.string("error_server")
        case (_, .login): return L10n.string("error_login_failed_generic")
        case (_, .register): return L10n.string("error_register_failed_generic")
        case (_, .changePassword): return L10n.string("error_change_password_generic")
        case (_, .editProfile): return L10n.string("error_edit_profile_generic")
        case (_, .deleteAccount): return L10n.string("error_delete_account_generic")
        default: return L10n.string("error_unexpected")
        }
    }
}

extension FeedTab {
    var label: String {
        switch self {
        case .featured: L10n.string("feed_tab_featured")
        case .latest: L10n.string("feed_tab_latest")
        case .following: L10n.string("feed_tab_following")
        case .mostLiked: L10n.string("feed_tab_most_liked")
        }
    }
}

extension TimeRange {
    var label: String {
        switch self {
        case .today: L10n.string("time_range_today")
        case .week: L10n.string("time_range_week")
        case .month: L10n.string("time_range_month")
        case .allTime: L10n.string("time_range_all")
        }
    }
}

extension LibraryCategory {
    func title(playlistName: String? = nil) -> String {
        switch self {
        case .liked: L10n.string("library_liked")
        case .downloaded: L10n.string("library_downloaded")
        case .shared: L10n.string("library_shared")
        case .drafts: L10n.string("library_drafts")
        case .completed: L10n.string("library_completed")
        case .dailies: L10n.string("library_dailies")
        case .playlist: playlistName ?? L10n.string("library_playlist")
        }
    }
}

extension Difficulty {
    var label: String {
        switch self {
        case .easy: L10n.string("difficulty_easy")
        case .medium: L10n.string("difficulty_medium")
        case .hard: L10n.string("difficulty_hard")
        }
    }
}

enum DisplayText {
    /// "5×5"; the multiplication sign reads the same in both languages.
    static func boardSize(_ size: Int32) -> String { "\(size)×\(size)" }

    static func author(_ author: String?) -> String { author ?? L10n.string("common_anonymous") }

    /// "@name", or the localized "Anonymous" when unknown.
    static func handle(_ author: String?) -> String { author.map { "@\($0)" } ?? DisplayText.author(nil) }

    /// "5 hr. ago" in the app's current locale; empty when unknown.
    static func relativeTime(_ epochMillis: KotlinLong?, now: Date = .now) -> String {
        guard let epochMillis else { return "" }
        let date = Date(timeIntervalSince1970: TimeInterval(epochMillis.int64Value) / 1000)
        let formatter = RelativeDateTimeFormatter()
        formatter.unitsStyle = .abbreviated
        return formatter.localizedString(for: date, relativeTo: now)
    }
}
