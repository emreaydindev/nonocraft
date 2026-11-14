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

class UserApiService(private val client: HttpClient) {

    suspend fun getMyProfile(): ApiResponse<UserDto> =
        client.get("users/me").body()

    suspend fun getUserProfile(userId: String): ApiResponse<UserDto> =
        client.get("users/$userId").body()

    suspend fun updateProfile(request: Map<String, String?>): ApiResponse<Unit> =
        client.put("users/me") { setBody(request.withoutNulls()) }.unitBody()

    suspend fun updateAvatar(request: Map<String, String?>): ApiResponse<UserDto> =
        client.put("users/me/avatar") { setBody(request.withoutNulls()) }.body()

    suspend fun changePassword(request: Map<String, String>): ApiResponse<Unit> =
        client.put("users/me/password") { setBody(request) }.unitBody()

    suspend fun changeEmail(request: Map<String, String>): ApiResponse<Unit> =
        client.put("users/me/email") { setBody(request) }.unitBody()

    suspend fun followUser(userId: String): ApiResponse<Unit> =
        client.post("users/$userId/follow").unitBody()

    suspend fun unfollowUser(userId: String): ApiResponse<Unit> =
        client.delete("users/$userId/follow").unitBody()

    suspend fun getFollowers(userId: String, limit: Int? = null, offset: Int? = null): ApiResponse<List<UserDto>> =
        client.get("users/$userId/followers") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()

    suspend fun getFollowing(userId: String, limit: Int? = null, offset: Int? = null): ApiResponse<List<UserDto>> =
        client.get("users/$userId/following") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()

    suspend fun deleteAccount(): ApiResponse<Unit> =
        client.delete("users/me").unitBody()
}

/** Unset fields are omitted from the request, so the server leaves them unchanged. */
private fun Map<String, String?>.withoutNulls(): Map<String, String> =
    mapNotNull { (key, value) -> value?.let { key to it } }.toMap()
