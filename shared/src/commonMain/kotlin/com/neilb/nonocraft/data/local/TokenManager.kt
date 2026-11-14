package com.neilb.nonocraft.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class TokenManager(
    private val settings: Settings
) {

    var accessToken: String?
        get() = settings.getStringOrNull(KEY_ACCESS)
        set(value) = putOrRemove(KEY_ACCESS, value)

    var refreshToken: String?
        get() = settings.getStringOrNull(KEY_REFRESH)
        set(value) = putOrRemove(KEY_REFRESH, value)

    val isLoggedIn: Boolean get() = accessToken != null

    val isGuest: Boolean
        get() = settings.getBoolean(KEY_GUEST, false)

    var hasCompletedOnboarding: Boolean
        get() = settings.getBoolean(KEY_ONBOARDING, false)
        set(value) { settings[KEY_ONBOARDING] = value }

    var hasSeenTutorial: Boolean
        get() = settings.getBoolean(KEY_TUTORIAL, false)
        set(value) { settings[KEY_TUTORIAL] = value }

    fun setGuestMode() {
        settings[KEY_GUEST] = true
    }

    fun clearGuestMode() {
        settings[KEY_GUEST] = false
    }

    fun clear() {
        settings.remove(KEY_ACCESS)
        settings.remove(KEY_REFRESH)
        settings.remove(KEY_GUEST)
    }

    private fun putOrRemove(key: String, value: String?) {
        if (value == null) settings.remove(key) else settings[key] = value
    }

    companion object {
        /** Name of the backing store; Android keeps using the existing SharedPreferences file. */
        const val STORE_NAME = "nonocraft_auth"

        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_GUEST = "is_guest"
        private const val KEY_ONBOARDING = "has_completed_onboarding"
        private const val KEY_TUTORIAL = "has_seen_tutorial"
    }
}
