import Foundation

/// Lookups in `Localizable.xcstrings`. Keys are the same names as Android's `strings.xml`, so both
/// platforms say the same thing; English is the source language, Turkish the translation (D§14).
enum L10n {
    /// The text for `key` in the app's current language.
    static func string(_ key: String) -> String {
        String(localized: String.LocalizationValue(key))
    }

    /// The text for `key` with its `%lld` / `%@` placeholders filled. Keys with plural variations
    /// pick the form for the number (`NSLocalizedString` keeps the plural rules for the formatter).
    static func format(_ key: String, _ arguments: CVarArg...) -> String {
        String(format: NSLocalizedString(key, comment: ""), locale: .current, arguments: arguments)
    }

    /// A deferred lookup for text stored outside a view (models, static lists, announcements); it
    /// resolves in the language that is current when it is shown.
    static func resource(_ key: String) -> LocalizedStringResource {
        LocalizedStringResource(String.LocalizationValue(key))
    }
}
