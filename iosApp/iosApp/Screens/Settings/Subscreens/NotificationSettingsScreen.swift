import SwiftUI

struct NotificationSettingsScreen: View {
    let onNavigateBack: () -> Void

    // Not persisted yet, same as on Android.
    @State private var dailyPuzzle = true
    @State private var newFollowers = true
    @State private var likesAndComments = true
    @State private var announcements = false

    var body: some View {
        SettingsScaffold(title: "Bildirim Tercihleri", onBack: onNavigateBack) {
            DashboardCard(cornerRadius: 20) {
                VStack(spacing: 0) {
                    SettingsSwitchRow(label: "Günün Bulmacası Hatırlatıcısı", isOn: $dailyPuzzle)
                    SettingsSwitchRow(label: "Yeni Takipçiler", isOn: $newFollowers)
                    SettingsSwitchRow(label: "Beğeni ve Yorumlar", isOn: $likesAndComments)
                    SettingsSwitchRow(label: "Duyurular ve Etkinlikler", isOn: $announcements)
                }
            }
        }
    }
}
