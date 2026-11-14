package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.mapper.toComment
import com.neilb.nonocraft.data.mapper.toCompletionResult
import com.neilb.nonocraft.data.mapper.toLikeResult
import com.neilb.nonocraft.data.mapper.toPuzzle
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.PuzzleApiService
import com.neilb.nonocraft.domain.model.Comment
import com.neilb.nonocraft.domain.model.CompletionResult
import com.neilb.nonocraft.domain.model.LikeResult
import com.neilb.nonocraft.domain.model.Puzzle
import com.neilb.nonocraft.domain.repository.PuzzleRepository

class PuzzleRepositoryImpl(
    private val api: PuzzleApiService
) : PuzzleRepository {

    override suspend fun getPuzzles(limit: Int?, offset: Int?, type: String?, difficulty: String?): Resource<List<Puzzle>> =
        safeApiCall { api.getPuzzles(limit, offset, type, difficulty) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data?.puzzles?.map { it.toPuzzle() } ?: emptyList())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun getPuzzleById(id: String): Resource<Puzzle> =
        safeApiCall { api.getPuzzleById(id) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data!!.toPuzzle())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun createPuzzle(request: CreatePuzzleRequest): Resource<Puzzle> =
        safeApiCall { api.createPuzzle(request) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data!!.toPuzzle())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun deletePuzzle(id: String): Resource<Unit> =
        safeApiCall { api.deletePuzzle(id) }

    override suspend fun likePuzzle(id: String): Resource<LikeResult> =
        safeApiCall { api.likePuzzle(id) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data!!.toLikeResult())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun unlikePuzzle(id: String): Resource<LikeResult> =
        safeApiCall { api.unlikePuzzle(id) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data!!.toLikeResult())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun completePuzzle(id: String): Resource<CompletionResult> =
        safeApiCall { api.completePuzzle(id) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data!!.toCompletionResult())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun playPuzzle(id: String): Resource<Unit> =
        safeApiCall { api.playPuzzle(id) }

    override suspend fun getComments(puzzleId: String): Resource<List<Comment>> =
        safeApiCall { api.getComments(puzzleId) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data?.comments?.map { it.toComment() } ?: emptyList())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun createComment(puzzleId: String, request: CreateCommentRequest): Resource<Comment> =
        safeApiCall { api.createComment(puzzleId, request) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data!!.toComment())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }
}
