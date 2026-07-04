package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.UserDto
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.POST

data class PremiumStatus(
    @SerializedName("isPremium") val isPremium: Boolean,
    @SerializedName("expirationDate") val expirationDate: String?,
    @SerializedName("planType") val planType: String?
)

interface PremiumApiService {
    @GET("premium/status")
    suspend fun getPremiumStatus(): ApiResponse<PremiumStatus>

    @POST("premium/subscribe")
    suspend fun subscribe(): ApiResponse<UserDto>

    @POST("premium/cancel")
    suspend fun cancel(): ApiResponse<Unit>
}
