package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class RankingEntry(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val stars: Int,
    val rank: Int
)

@Serializable
data class RankingResponse(
    val ranking: List<RankingEntry>
)

@Serializable
data class MyRankResponse(
    val rank: Int,
    val totalUsers: Int,
    val stars: Int
)
