package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.UserDto
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.PremiumApiService
import com.neilb.nonocraft.data.remote.service.PremiumStatus
import com.neilb.nonocraft.domain.repository.PremiumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumRepositoryImpl @Inject constructor(
    private val api: PremiumApiService
) : PremiumRepository {
    override suspend fun getStatus() = safeApiCall { api.getPremiumStatus() }
    override suspend fun subscribe() = safeApiCall { api.subscribe() }
    override suspend fun cancel() = safeApiCall { api.cancel() }
}
