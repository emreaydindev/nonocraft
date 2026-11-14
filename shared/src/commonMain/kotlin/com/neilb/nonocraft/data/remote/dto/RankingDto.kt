package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingEntry(
    @SerialName("id") val userId: String,
    val username: String,
    val avatarPuzzleId: String? = null,
    val stars: Int,
    val rank: Int
)

@Serializable
data class RankingResponse(
    @SerialName("rankings") val ranking: List<RankingEntry>,
    val total: Int = 0
)

@Serializable
data class MyRankResponse(
    val rank: Int,
    val totalUsers: Int = 0,
    val stars: Int
)
