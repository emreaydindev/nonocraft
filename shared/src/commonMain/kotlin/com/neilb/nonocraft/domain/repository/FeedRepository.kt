package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.FeedListResponse

interface FeedRepository {
    suspend fun getLatest(limit: Int?, offset: Int?): Resource<FeedListResponse>
    suspend fun getMostLiked(limit: Int?, offset: Int?): Resource<FeedListResponse>
    suspend fun getFollowing(limit: Int?, offset: Int?): Resource<FeedListResponse>
    suspend fun getFeatured(limit: Int?, offset: Int?): Resource<FeedListResponse>
}
