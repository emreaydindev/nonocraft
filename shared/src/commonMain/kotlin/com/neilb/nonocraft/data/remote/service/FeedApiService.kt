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

class FeedApiService(private val client: HttpClient) {

    suspend fun getLatestFeed(limit: Int? = null, offset: Int? = null): ApiResponse<FeedListResponse> =
        getFeed("feed/latest", limit, offset)

    suspend fun getMostLikedFeed(limit: Int? = null, offset: Int? = null): ApiResponse<FeedListResponse> =
        getFeed("feed/most-liked", limit, offset)

    suspend fun getFollowingFeed(limit: Int? = null, offset: Int? = null): ApiResponse<FeedListResponse> =
        getFeed("feed/following", limit, offset)

    suspend fun getFeaturedFeed(limit: Int? = null, offset: Int? = null): ApiResponse<FeedListResponse> =
        getFeed("feed/featured", limit, offset)

    private suspend fun getFeed(path: String, limit: Int?, offset: Int?): ApiResponse<FeedListResponse> =
        client.get(path) {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body<ApiResponse<List<FeedItemDto>>>().map(::FeedListResponse)
}
