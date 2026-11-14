package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.unitBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class PremiumApiService(private val client: HttpClient) {

    suspend fun getPremiumStatus(): ApiResponse<PremiumStatus> =
        client.get("premium/status").body()

    suspend fun subscribe(): ApiResponse<UserDto> =
        client.post("premium/subscribe").body()

    suspend fun cancel(): ApiResponse<Unit> =
        client.post("premium/cancel").unitBody()
}
