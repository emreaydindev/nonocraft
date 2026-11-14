package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.AuthResult
import com.neilb.nonocraft.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<AuthResult>
    suspend fun register(email: String, username: String, password: String): Resource<AuthResult>
    suspend fun refreshToken(): Resource<AuthResult>
    suspend fun logout()
    suspend fun forgotPassword(email: String): Resource<Unit>
    suspend fun resetPassword(token: String, password: String): Resource<Unit>
    suspend fun verifyEmail(token: String): Resource<Unit>
    suspend fun googleLogin(idToken: String): Resource<AuthResult>
    fun getSavedUser(): User?
    fun isLoggedIn(): Boolean
    fun isGuest(): Boolean
    fun setGuestMode()
    fun clearSession()
}
