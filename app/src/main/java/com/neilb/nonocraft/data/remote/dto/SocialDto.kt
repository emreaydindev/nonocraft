package com.neilb.nonocraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id") val id: String,
    @SerializedName("puzzleId") val puzzleId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("username") val username: String,
    @SerializedName("text") val text: String,
    @SerializedName("likeCount") val likeCount: Int?,
    @SerializedName("isLiked") val isLiked: Boolean?,
    @SerializedName("createdAt") val createdAt: String?
)

data class CreateCommentRequest(
    @SerializedName("text") val text: String
)

data class CommentListResponse(
    @SerializedName("comments") val comments: List<CommentDto>
)

data class ReportRequest(
    @SerializedName("puzzleId") val puzzleId: String? = null,
    @SerializedName("commentId") val commentId: String? = null,
    @SerializedName("reason") val reason: String
)
