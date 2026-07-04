package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedApiService {
    @GET("feed/latest")
    suspend fun getLatestFeed(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): ApiResponse<FeedListResponse>

    @GET("feed/most-liked")
    suspend fun getMostLikedFeed(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): ApiResponse<FeedListResponse>

    @GET("feed/following")
    suspend fun getFollowingFeed(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): ApiResponse<FeedListResponse>

    @GET("feed/featured")
    suspend fun getFeaturedFeed(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): ApiResponse<FeedListResponse>
}
