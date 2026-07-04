package com.neilb.nonocraft.domain.model

data class User(
    val id: String,
    val email: String,
    val username: String,
    val bio: String?,
    val isPremium: Boolean,
    val isAdmin: Boolean,
    val stars: Int,
    val streakCount: Int,
    val emailVerified: Boolean,
    val avatarUrl: String?,
    val followerCount: Int,
    val followingCount: Int
)

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
