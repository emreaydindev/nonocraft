package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import retrofit2.http.*

interface SocialApiService {
    @DELETE("comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: String): ApiResponse<Unit>

    @POST("comments/{id}/like")
    suspend fun likeComment(@Path("id") commentId: String): ApiResponse<Unit>

    @POST("reports")
    suspend fun report(@Body request: ReportRequest): ApiResponse<Unit>
}
