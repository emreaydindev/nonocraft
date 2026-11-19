package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.mapper.toAuthResult
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.AuthApiService
import com.neilb.nonocraft.domain.model.AuthResult
import com.neilb.nonocraft.domain.model.User
import com.neilb.nonocraft.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        val result = safeApiCall { api.login(LoginRequest(email, password)) }
        return when (result) {
            is Resource.Success -> {
                tokenManager.accessToken = result.data!!.accessToken
                tokenManager.refreshToken = result.data.refreshToken
                tokenManager.clearGuestMode()
                Resource.Success(result.data.toAuthResult())
            }
            is Resource.Error -> Resource.Error(result.error)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun register(email: String, username: String, password: String): Resource<AuthResult> {
        val result = safeApiCall { api.register(RegisterRequest(email, username, password)) }
        return when (result) {
            is Resource.Success -> {
                tokenManager.accessToken = result.data!!.accessToken
                tokenManager.refreshToken = result.data.refreshToken
                tokenManager.clearGuestMode()
                Resource.Success(result.data.toAuthResult())
            }
            is Resource.Error -> Resource.Error(result.error)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun refreshToken(): Resource<AuthResult> {
        val rt = tokenManager.refreshToken ?: return Resource.Error(ErrorCode.SESSION_EXPIRED)
        val result = safeApiCall { api.refreshToken(RefreshTokenRequest(rt)) }
        return when (result) {
            is Resource.Success -> {
                tokenManager.accessToken = result.data!!.accessToken
                tokenManager.refreshToken = result.data.refreshToken
                Resource.Success(result.data.toAuthResult())
            }
            is Resource.Error -> {
                tokenManager.clear()
                Resource.Error(result.error)
            }
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun logout() {
        tokenManager.refreshToken?.let {
            try { api.logout(RefreshTokenRequest(it)) } catch (_: Exception) {}
        }
        tokenManager.clear()
    }

    override suspend fun forgotPassword(email: String): Resource<Unit> =
        safeApiCall { api.forgotPassword(ForgotPasswordRequest(email)) }

    override suspend fun resetPassword(token: String, password: String): Resource<Unit> =
        safeApiCall { api.resetPassword(ResetPasswordRequest(token, password)) }

    override suspend fun verifyEmail(token: String): Resource<Unit> =
        safeApiCall { api.verifyEmail(VerifyEmailRequest(token)) }

    override suspend fun googleLogin(idToken: String): Resource<AuthResult> {
        val result = safeApiCall { api.googleLogin(GoogleLoginRequest(idToken)) }
        return when (result) {
            is Resource.Success -> {
                tokenManager.accessToken = result.data!!.accessToken
                tokenManager.refreshToken = result.data.refreshToken
                tokenManager.clearGuestMode()
                Resource.Success(result.data.toAuthResult())
            }
            is Resource.Error -> Resource.Error(result.error)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override fun getSavedUser(): User? = null

    override fun isLoggedIn(): Boolean = tokenManager.isLoggedIn

    override fun isGuest(): Boolean = tokenManager.isGuest

    override fun setGuestMode() = tokenManager.setGuestMode()

    override fun clearSession() = tokenManager.clear()
}
