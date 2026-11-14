package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.CreateCommentRequest
import com.neilb.nonocraft.data.remote.dto.CreatePuzzleRequest
import com.neilb.nonocraft.domain.model.*

interface PuzzleRepository {
    suspend fun getPuzzles(limit: Int?, offset: Int?, type: String?, difficulty: String?): Resource<List<Puzzle>>
    suspend fun getPuzzleById(id: String): Resource<Puzzle>
    suspend fun createPuzzle(request: CreatePuzzleRequest): Resource<Puzzle>
    suspend fun deletePuzzle(id: String): Resource<Unit>
    suspend fun likePuzzle(id: String): Resource<LikeResult>
    suspend fun unlikePuzzle(id: String): Resource<LikeResult>
    suspend fun completePuzzle(id: String): Resource<CompletionResult>
    suspend fun playPuzzle(id: String): Resource<Unit>
    suspend fun getComments(puzzleId: String): Resource<List<Comment>>
    suspend fun createComment(puzzleId: String, request: CreateCommentRequest): Resource<Comment>
}
