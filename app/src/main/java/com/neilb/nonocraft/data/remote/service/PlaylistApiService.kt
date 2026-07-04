package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import retrofit2.http.*

interface PlaylistApiService {
    @GET("playlists")
    suspend fun getPlaylists(): ApiResponse<PlaylistListResponse>

    @POST("playlists")
    suspend fun createPlaylist(@Body request: CreatePlaylistRequest): ApiResponse<PlaylistDto>

    @GET("playlists/{id}")
    suspend fun getPlaylist(@Path("id") playlistId: String): ApiResponse<PlaylistDto>

    @PUT("playlists/{id}")
    suspend fun updatePlaylist(
        @Path("id") playlistId: String,
        @Body request: CreatePlaylistRequest
    ): ApiResponse<PlaylistDto>

    @DELETE("playlists/{id}")
    suspend fun deletePlaylist(@Path("id") playlistId: String): ApiResponse<Unit>

    @POST("playlists/{id}/puzzles")
    suspend fun addToPlaylist(
        @Path("id") playlistId: String,
        @Body request: AddPuzzleToPlaylistRequest
    ): ApiResponse<Unit>

    @DELETE("playlists/{id}/puzzles/{puzzleId}")
    suspend fun removeFromPlaylist(
        @Path("id") playlistId: String,
        @Path("puzzleId") puzzleId: String
    ): ApiResponse<Unit>
}
