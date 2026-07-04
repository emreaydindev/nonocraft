package com.neilb.nonocraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RankingEntry(
    @SerializedName("userId") val userId: String,
    @SerializedName("username") val username: String,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("stars") val stars: Int,
    @SerializedName("rank") val rank: Int
)

data class RankingResponse(
    @SerializedName("ranking") val ranking: List<RankingEntry>
)

data class MyRankResponse(
    @SerializedName("rank") val rank: Int,
    @SerializedName("totalUsers") val totalUsers: Int,
    @SerializedName("stars") val stars: Int
)
