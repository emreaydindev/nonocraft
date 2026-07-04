package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.UserDto
import com.neilb.nonocraft.data.remote.service.PremiumStatus

interface PremiumRepository {
    suspend fun getStatus(): Resource<PremiumStatus>
    suspend fun subscribe(): Resource<UserDto>
    suspend fun cancel(): Resource<Unit>
}
