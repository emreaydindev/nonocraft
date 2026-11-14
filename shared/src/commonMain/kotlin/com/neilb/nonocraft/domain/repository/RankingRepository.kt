package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.*

interface RankingRepository {
    suspend fun getRanking(limit: Int?, offset: Int?): Resource<RankingResponse>
    suspend fun getMyRank(): Resource<MyRankResponse>
}
