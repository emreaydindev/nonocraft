package com.neilb.nonocraft.domain.model

data class User(
    val id: String,
    val email: String,
    val username: String,
    val bio: String?,
    val avatarPuzzleId: String?,
    val isPremium: Boolean,
    val emailVerified: Boolean,
    val stars: Int,
    val streakCount: Int,
    val solvedPuzzles: Int,
    val solvedDailies: Int,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int,
)

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val user: User?
)
