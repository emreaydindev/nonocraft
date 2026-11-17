import SwiftUI

struct NotificationSettingsScreen: View {
    // Not persisted yet, same as on Android.
    @State private var dailyPuzzle = true
    @State private var newFollowers = true
    @State private var likesAndComments = true
    @State private var announcements = false

    var body: some View {
        SettingsForm(title: "Bildirim Tercihleri") {
            SettingsSection {
                Toggle(isOn: $dailyPuzzle) {
                    SettingsLabel(title: "Günün Bulmacası Hatırlatıcısı", systemImage: "calendar")
                }
                Toggle(isOn: $newFollowers) {
                    SettingsLabel(title: "Yeni Takipçiler", systemImage: "person.badge.plus", tint: .purpleAccent)
                }
                Toggle(isOn: $likesAndComments) {
                    SettingsLabel(title: "Beğeni ve Yorumlar", systemImage: "heart.fill", tint: .dangerRed)
                }
                Toggle(isOn: $announcements) {
                    SettingsLabel(title: "Duyurular ve Etkinlikler", systemImage: "megaphone.fill", tint: .gameGold)
                }
            }
        }
    }
}
