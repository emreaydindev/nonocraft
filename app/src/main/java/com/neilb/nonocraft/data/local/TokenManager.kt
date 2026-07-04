package com.neilb.nonocraft.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nonocraft_auth", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString(KEY_ACCESS, null)
        set(value) = prefs.edit { putString(KEY_ACCESS, value) }

    var refreshToken: String?
        get() = prefs.getString(KEY_REFRESH, null)
        set(value) = prefs.edit { putString(KEY_REFRESH, value) }

    val isLoggedIn: Boolean get() = accessToken != null

    val isGuest: Boolean
        get() = prefs.getBoolean(KEY_GUEST, false)

    var hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING, value) }

    var hasSeenTutorial: Boolean
        get() = prefs.getBoolean(KEY_TUTORIAL, false)
        set(value) = prefs.edit { putBoolean(KEY_TUTORIAL, value) }

    fun setGuestMode() {
        prefs.edit { putBoolean(KEY_GUEST, true) }
    }

    fun clearGuestMode() {
        prefs.edit { putBoolean(KEY_GUEST, false) }
    }

    fun clear() {
        prefs.edit {
            remove(KEY_ACCESS)
                .remove(KEY_REFRESH)
                .remove(KEY_GUEST)
        }
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_GUEST = "is_guest"
        const val KEY_ONBOARDING = "has_completed_onboarding"
        const val KEY_TUTORIAL = "has_seen_tutorial"
    }
}