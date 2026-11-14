package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto?
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val username: String,
    val bio: String?,
    val avatarPuzzleId: String? = null,
    val isPremium: Boolean,
    val emailVerified: Boolean,
    val stars: Int,
    val streakCount: Int,
    val solvedPuzzles: Int = 0,
    val solvedDailies: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val password: String
)

@Serializable
data class VerifyEmailRequest(
    val token: String
)

@Serializable
data class GoogleLoginRequest(
    val idToken: String,
    val deviceInfo: String? = null
)
