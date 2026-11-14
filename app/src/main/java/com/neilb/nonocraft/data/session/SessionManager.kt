package com.neilb.nonocraft.data.session

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.domain.model.User
import com.neilb.nonocraft.domain.use_case.user.GetMyProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val getCurrentUser: GetMyProfile,
) {
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    suspend fun refreshSession(): Boolean {
        if (!tokenManager.isLoggedIn) {
            _userProfile.value = null
            return false
        }
        return when (val result = getCurrentUser()) {
            is Resource.Success -> {
                _userProfile.value = result.data
                println(result.data)
                true
            }
            is Resource.Error -> {
                tokenManager.clear()
                _userProfile.value = null
                false
            }
            is Resource.Loading -> false
        }
    }

    fun clearSession() {
        tokenManager.clear()
        tokenManager.clearGuestMode()
        _userProfile.value = null
    }

    suspend fun refreshProfile() {
        refreshSession()
    }
}