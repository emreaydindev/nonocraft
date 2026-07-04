package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.RankingApiService
import com.neilb.nonocraft.domain.repository.RankingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RankingRepositoryImpl @Inject constructor(
    private val api: RankingApiService
) : RankingRepository {
    override suspend fun getRanking(limit: Int?, offset: Int?) = safeApiCall { api.getRanking(limit, offset) }
    override suspend fun getMyRank() = safeApiCall { api.getMyRank() }
}
