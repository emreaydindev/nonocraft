package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.PlaylistApiService
import com.neilb.nonocraft.domain.repository.PlaylistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val api: PlaylistApiService
) : PlaylistRepository {
    override suspend fun getPlaylists() = safeApiCall { api.getPlaylists() }
    override suspend fun createPlaylist(name: String, description: String?) =
        safeApiCall { api.createPlaylist(CreatePlaylistRequest(name, description)) }
    override suspend fun getPlaylist(id: String) = safeApiCall { api.getPlaylist(id) }
    override suspend fun deletePlaylist(id: String) = safeApiCall { api.deletePlaylist(id) }
    override suspend fun addToPlaylist(playlistId: String, puzzleId: String) =
        safeApiCall { api.addToPlaylist(playlistId, AddPuzzleToPlaylistRequest(puzzleId)) }
    override suspend fun removeFromPlaylist(playlistId: String, puzzleId: String) =
        safeApiCall { api.removeFromPlaylist(playlistId, puzzleId) }
}
