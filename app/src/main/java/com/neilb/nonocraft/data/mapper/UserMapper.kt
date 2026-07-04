package com.neilb.nonocraft.data.mapper

import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.domain.model.*

fun UserDto.toUser(): User {
    return User(
        id = id,
        email = email,
        username = username,
        bio = bio,
        isPremium = isPremium,
        isAdmin = isAdmin,
        stars = stars,
        streakCount = streakCount,
        emailVerified = emailVerified,
        avatarUrl = avatarUrl,
        followerCount = followerCount,
        followingCount = followingCount
    )
}

fun LoginResponse.toAuthResult(): AuthResult {
    return AuthResult(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toUser()
    )
}
