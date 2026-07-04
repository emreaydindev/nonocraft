package com.neilb.nonocraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlaylistDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("userId") val userId: String,
    @SerializedName("puzzleCount") val puzzleCount: Int?,
    @SerializedName("createdAt") val createdAt: String?
)

data class CreatePlaylistRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null
)

data class PlaylistListResponse(
    @SerializedName("playlists") val playlists: List<PlaylistDto>
)

data class AddPuzzleToPlaylistRequest(
    @SerializedName("puzzleId") val puzzleId: String
)
