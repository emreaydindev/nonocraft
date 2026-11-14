package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val userId: String = "",
    @SerialName("postCount") val puzzleCount: Int? = null,
    val createdAt: String? = null
)

@Serializable
data class CreatePlaylistRequest(
    val name: String,
    val description: String? = null
)

@Serializable
data class PlaylistListResponse(
    val playlists: List<PlaylistDto>
)

@Serializable
data class AddPuzzleToPlaylistRequest(
    val puzzleId: String
)
