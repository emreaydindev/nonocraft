package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.unitBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class PlaylistApiService(private val client: HttpClient) {

    suspend fun getPlaylists(): ApiResponse<PlaylistListResponse> =
        client.get("playlists").body()

    suspend fun createPlaylist(request: CreatePlaylistRequest): ApiResponse<PlaylistDto> =
        client.post("playlists") { setBody(request) }.body()

    suspend fun getPlaylist(playlistId: String): ApiResponse<PlaylistDto> =
        client.get("playlists/$playlistId").body()

    suspend fun updatePlaylist(playlistId: String, request: CreatePlaylistRequest): ApiResponse<PlaylistDto> =
        client.put("playlists/$playlistId") { setBody(request) }.body()

    suspend fun deletePlaylist(playlistId: String): ApiResponse<Unit> =
        client.delete("playlists/$playlistId").unitBody()

    suspend fun addToPlaylist(playlistId: String, request: AddPuzzleToPlaylistRequest): ApiResponse<Unit> =
        client.post("playlists/$playlistId/puzzles") { setBody(request) }.unitBody()

    suspend fun removeFromPlaylist(playlistId: String, puzzleId: String): ApiResponse<Unit> =
        client.delete("playlists/$playlistId/puzzles/$puzzleId").unitBody()
}
