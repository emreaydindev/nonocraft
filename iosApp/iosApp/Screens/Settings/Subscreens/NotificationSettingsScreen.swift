import SwiftUI

struct NotificationSettingsScreen: View {
    // Not persisted yet, same as on Android.
    @State private var dailyPuzzle = true
    @State private var newFollowers = true
    @State private var likesAndComments = true
    @State private var announcements = false

    var body: some View {
        SettingsForm(title: L10n.string("settings_notifications")) {
            SettingsSection {
                Toggle(isOn: $dailyPuzzle) {
                    SettingsLabel(title: L10n.string("notif_daily"), systemImage: "calendar")
                }
                Toggle(isOn: $newFollowers) {
                    SettingsLabel(title: L10n.string("notif_followers"), systemImage: "person.badge.plus", tint: .purpleAccent)
                }
                Toggle(isOn: $likesAndComments) {
                    SettingsLabel(title: L10n.string("notif_likes_comments"), systemImage: "heart.fill", tint: .dangerRed)
                }
                Toggle(isOn: $announcements) {
                    SettingsLabel(title: L10n.string("notif_announcements"), systemImage: "megaphone.fill", tint: .gameGold)
                }
            }
        }
    }
}
