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

class RankingApiService(private val client: HttpClient) {

    suspend fun getRanking(limit: Int? = null, offset: Int? = null): ApiResponse<RankingResponse> =
        client.get("ranking") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()

    suspend fun getMyRank(): ApiResponse<MyRankResponse> =
        client.get("ranking/my-rank").body()
}
