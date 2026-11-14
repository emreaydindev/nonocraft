package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.CommentDto
import com.neilb.nonocraft.data.remote.dto.CommentListResponse

interface SocialRepository {
    suspend fun getComments(puzzleId: String): Resource<CommentListResponse>
    suspend fun createComment(puzzleId: String, text: String): Resource<CommentDto>
    suspend fun deleteComment(commentId: String): Resource<Unit>
    suspend fun likeComment(commentId: String): Resource<Unit>
    suspend fun reportPuzzle(puzzleId: String, reason: String): Resource<Unit>
    suspend fun reportComment(commentId: String, reason: String): Resource<Unit>
}
