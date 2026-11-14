package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.map
import com.neilb.nonocraft.data.remote.unitBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class PuzzleApiService(private val client: HttpClient) {

    suspend fun getPuzzles(
        limit: Int? = null,
        offset: Int? = null,
        type: String? = null,
        difficulty: String? = null
    ): ApiResponse<PuzzleListResponse> =
        client.get("puzzles") {
            parameter("limit", limit)
            parameter("offset", offset)
            parameter("types", type)
            parameter("difficulties", difficulty)
        }.body<ApiResponse<List<PuzzleDto>>>().map(::PuzzleListResponse)

    suspend fun getPuzzleById(puzzleId: String): ApiResponse<PuzzleDto> =
        client.get("puzzles/$puzzleId").body()

    suspend fun createPuzzle(request: CreatePuzzleRequest): ApiResponse<PuzzleDto> =
        client.post("puzzles") { setBody(request) }.body()

    suspend fun updatePuzzle(puzzleId: String, request: CreatePuzzleRequest): ApiResponse<PuzzleDto> =
        client.put("puzzles/$puzzleId") { setBody(request) }.body()

    suspend fun deletePuzzle(puzzleId: String): ApiResponse<Unit> =
        client.delete("puzzles/$puzzleId").unitBody()

    suspend fun likePuzzle(puzzleId: String): ApiResponse<LikeResponse> =
        client.post("puzzles/$puzzleId/like").body()

    suspend fun unlikePuzzle(puzzleId: String): ApiResponse<LikeResponse> =
        client.delete("puzzles/$puzzleId/like").body()

    suspend fun completePuzzle(puzzleId: String): ApiResponse<CompleteResponse> =
        client.post("puzzles/$puzzleId/complete").body()

    suspend fun playPuzzle(puzzleId: String): ApiResponse<Unit> =
        client.post("puzzles/$puzzleId/play").unitBody()

    suspend fun getComments(puzzleId: String): ApiResponse<CommentListResponse> =
        client.get("puzzles/$puzzleId/comments").body<ApiResponse<List<CommentDto>>>().map(::CommentListResponse)

    suspend fun createComment(puzzleId: String, request: CreateCommentRequest): ApiResponse<CommentDto> =
        client.post("puzzles/$puzzleId/comments") { setBody(request) }.body()
}
