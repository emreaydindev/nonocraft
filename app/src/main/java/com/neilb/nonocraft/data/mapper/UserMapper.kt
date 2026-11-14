package com.neilb.nonocraft.data.mapper

import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.domain.model.*

fun UserDto.toUser(): User {
    return User(
        id = id,
        username = username,
        email = email,
        bio = bio,
        avatarPuzzleId = avatarPuzzleId,
        followersCount = followersCount,
        followingCount = followingCount,
        postsCount = postsCount,
        isPremium = isPremium,
        emailVerified = emailVerified,
        stars = stars,
        streakCount = streakCount,
        solvedPuzzles = solvedPuzzles,
        solvedDailies = solvedDailies
    )
}

fun LoginResponse.toAuthResult(): AuthResult {
    return AuthResult(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user?.toUser()
    )
}
