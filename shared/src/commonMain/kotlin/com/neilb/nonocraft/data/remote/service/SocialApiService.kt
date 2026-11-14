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

class SocialApiService(private val client: HttpClient) {

    suspend fun deleteComment(commentId: String): ApiResponse<Unit> =
        client.delete("comments/$commentId").unitBody()

    suspend fun likeComment(commentId: String): ApiResponse<Unit> =
        client.post("comments/$commentId/like").unitBody()

    suspend fun report(request: ReportRequest): ApiResponse<Unit> =
        client.post("reports") { setBody(request) }.unitBody()
}
