package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.PuzzleApiService
import com.neilb.nonocraft.data.remote.service.SocialApiService
import com.neilb.nonocraft.domain.repository.SocialRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val puzzleApi: PuzzleApiService,
    private val socialApi: SocialApiService
) : SocialRepository {

    override suspend fun getComments(puzzleId: String) =
        safeApiCall { puzzleApi.getComments(puzzleId) }

    override suspend fun createComment(puzzleId: String, text: String) =
        safeApiCall { puzzleApi.createComment(puzzleId, CreateCommentRequest(text)) }

    override suspend fun deleteComment(commentId: String) =
        safeApiCall { socialApi.deleteComment(commentId) }

    override suspend fun likeComment(commentId: String) =
        safeApiCall { socialApi.likeComment(commentId) }

    override suspend fun reportPuzzle(puzzleId: String, reason: String) =
        safeApiCall { socialApi.report(ReportRequest(puzzleId = puzzleId, reason = reason)) }

    override suspend fun reportComment(commentId: String, reason: String) =
        safeApiCall { socialApi.report(ReportRequest(commentId = commentId, reason = reason)) }
}
