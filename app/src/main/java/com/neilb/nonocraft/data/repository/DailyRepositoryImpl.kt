package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.DailyApiService
import com.neilb.nonocraft.domain.repository.DailyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyRepositoryImpl @Inject constructor(
    private val api: DailyApiService
) : DailyRepository {
    override suspend fun getTodayPuzzle() = safeApiCall { api.getTodayPuzzle() }
    override suspend fun getStreak() = safeApiCall { api.getStreak() }
    override suspend fun getCalendar() = safeApiCall { api.getCalendar() }
    override suspend fun completeDaily() = safeApiCall { api.completeDaily() }
}
