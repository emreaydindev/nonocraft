package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.FeedListResponse
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.FeedApiService
import com.neilb.nonocraft.domain.repository.FeedRepository

class FeedRepositoryImpl(
    private val api: FeedApiService
) : FeedRepository {
    override suspend fun getLatest(limit: Int?, offset: Int?) = safeApiCall { api.getLatestFeed(limit, offset) }
    override suspend fun getMostLiked(limit: Int?, offset: Int?) = safeApiCall { api.getMostLikedFeed(limit, offset) }
    override suspend fun getFollowing(limit: Int?, offset: Int?) = safeApiCall { api.getFollowingFeed(limit, offset) }
    override suspend fun getFeatured(limit: Int?, offset: Int?) = safeApiCall { api.getFeaturedFeed(limit, offset) }
}
