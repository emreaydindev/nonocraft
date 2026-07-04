package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Query

interface RankingApiService {
    @GET("ranking")
    suspend fun getRanking(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): ApiResponse<RankingResponse>

    @GET("ranking/my-rank")
    suspend fun getMyRank(): ApiResponse<MyRankResponse>
}
