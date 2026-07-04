package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import retrofit2.http.*

interface PuzzleApiService {
    @GET("puzzles")
    suspend fun getPuzzles(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("type") type: String? = null,
        @Query("difficulty") difficulty: String? = null
    ): ApiResponse<PuzzleListResponse>

    @GET("puzzles/{id}")
    suspend fun getPuzzleById(@Path("id") puzzleId: String): ApiResponse<PuzzleDto>

    @POST("puzzles")
    suspend fun createPuzzle(@Body request: CreatePuzzleRequest): ApiResponse<PuzzleDto>

    @PUT("puzzles/{id}")
    suspend fun updatePuzzle(
        @Path("id") puzzleId: String,
        @Body request: CreatePuzzleRequest
    ): ApiResponse<PuzzleDto>

    @DELETE("puzzles/{id}")
    suspend fun deletePuzzle(@Path("id") puzzleId: String): ApiResponse<Unit>

    @POST("puzzles/{id}/like")
    suspend fun likePuzzle(@Path("id") puzzleId: String): ApiResponse<LikeResponse>

    @DELETE("puzzles/{id}/like")
    suspend fun unlikePuzzle(@Path("id") puzzleId: String): ApiResponse<LikeResponse>

    @POST("puzzles/{id}/complete")
    suspend fun completePuzzle(@Path("id") puzzleId: String): ApiResponse<CompleteResponse>

    @POST("puzzles/{id}/play")
    suspend fun playPuzzle(@Path("id") puzzleId: String): ApiResponse<Unit>

    @GET("puzzles/{id}/comments")
    suspend fun getComments(@Path("id") puzzleId: String): ApiResponse<CommentListResponse>

    @POST("puzzles/{id}/comments")
    suspend fun createComment(
        @Path("id") puzzleId: String,
        @Body request: CreateCommentRequest
    ): ApiResponse<CommentDto>
}
