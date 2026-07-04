package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.*

interface PlaylistRepository {
    suspend fun getPlaylists(): Resource<PlaylistListResponse>
    suspend fun createPlaylist(name: String, description: String?): Resource<PlaylistDto>
    suspend fun getPlaylist(id: String): Resource<PlaylistDto>
    suspend fun deletePlaylist(id: String): Resource<Unit>
    suspend fun addToPlaylist(playlistId: String, puzzleId: String): Resource<Unit>
    suspend fun removeFromPlaylist(playlistId: String, puzzleId: String): Resource<Unit>
}
