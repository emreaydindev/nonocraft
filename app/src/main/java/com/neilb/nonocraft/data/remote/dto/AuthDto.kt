package com.neilb.nonocraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("user") val user: UserDto?
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("bio") val bio: String?,
    @SerializedName("isPremium") val isPremium: Boolean,
    @SerializedName("isAdmin") val isAdmin: Boolean,
    @SerializedName("stars") val stars: Int,
    @SerializedName("streakCount") val streakCount: Int,
    @SerializedName("emailVerified") val emailVerified: Boolean,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("followerCount") val followerCount: Int = 0,
    @SerializedName("followingCount") val followingCount: Int = 0
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequest(
    @SerializedName("token") val token: String,
    @SerializedName("password") val password: String
)

data class VerifyEmailRequest(
    @SerializedName("token") val token: String
)

data class GoogleLoginRequest(
    @SerializedName("idToken") val idToken: String,
    @SerializedName("deviceInfo") val deviceInfo: String? = null
)
