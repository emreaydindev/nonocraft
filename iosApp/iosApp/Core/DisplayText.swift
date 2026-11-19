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
        case (.network, _): return "Bağlantı hatası. Lütfen internetinizi kontrol edin."
        case (.unauthorized, .login), (.invalidCredentials, _): return "E-posta veya şifre hatalı"
        case (.conflict, .register): return "Bu e-posta veya kullanıcı adı zaten kullanılıyor"
        case (.unauthorized, .changePassword), (.wrongPassword, _): return "Mevcut şifre hatalı"
        case (.notFound, .resetPassword), (.validation, .resetPassword), (.invalidCode, _): return "Geçersiz veya süresi dolmuş kod"
        case (.conflict, .editProfile), (.usernameTaken, _): return "Bu kullanıcı adı zaten alınmış"
        case (.notFound, .game), (.puzzleNotFound, _): return "Bulmaca bulunamadı"
        case (.emailTaken, _): return "Bu e-posta zaten kullanılıyor"
        case (.sessionExpired, _), (.unauthorized, _): return "Oturum süresi doldu, lütfen tekrar giriş yapın"
        case (.emailNotVerified, _): return "Önce e-posta adresinizi doğrulayın"
        case (.insufficientCrystals, _): return "Yetersiz kristal"
        case (.dailyLimitReached, _): return "Günlük limitine ulaştın"
        case (.googleSignInFailed, _): return "Google ile giriş yapılamadı"
        case (.rateLimited, _): return "Çok fazla deneme. Biraz sonra tekrar dene."
        case (.server, _): return "Sunucu hatası. Lütfen daha sonra tekrar deneyin."
        case (_, .login): return "Giriş yapılamadı"
        case (_, .register): return "Kayıt olunamadı"
        case (_, .changePassword): return "Şifre güncellenemedi"
        case (_, .editProfile): return "Profil güncellenemedi"
        case (_, .deleteAccount): return "Hesap silinemedi"
        default: return "Beklenmedik bir hata oluştu"
        }
    }
}

extension FeedTab {
    var label: String {
        switch self {
        case .featured: "Öne Çıkanlar"
        case .latest: "En Yeniler"
        case .following: "Takip Edilenler"
        case .mostLiked: "En Beğenilenler"
        }
    }
}

extension TimeRange {
    var label: String {
        switch self {
        case .today: "Son 24 Saat"
        case .week: "Bu Hafta"
        case .month: "Bu Ay"
        case .allTime: "Tüm Zamanlar"
        }
    }
}

extension LibraryCategory {
    func title(playlistName: String? = nil) -> String {
        switch self {
        case .liked: "Beğenilenler"
        case .downloaded: "İndirilenler"
        case .shared: "Paylaşılanlar"
        case .drafts: "Taslaklar"
        case .completed: "Çözülenler"
        case .dailies: "Günlük Serüven"
        case .playlist: playlistName ?? "Oynatma Listesi"
        }
    }
}

extension Difficulty {
    var label: String {
        switch self {
        case .easy: "Kolay"
        case .medium: "Orta"
        case .hard: "Zor"
        }
    }
}

enum DisplayText {
    /// "5×5"; the multiplication sign reads the same in both languages.
    static func boardSize(_ size: Int32) -> String { "\(size)×\(size)" }

    static func author(_ author: String?) -> String { author ?? "Anonim" }

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
