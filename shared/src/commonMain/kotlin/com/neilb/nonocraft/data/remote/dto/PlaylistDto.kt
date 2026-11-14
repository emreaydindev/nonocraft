package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class PlaylistDto(
    val id: String,
    val name: String,
    val description: String?,
    val userId: String,
    val puzzleCount: Int?,
    val createdAt: String?
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
