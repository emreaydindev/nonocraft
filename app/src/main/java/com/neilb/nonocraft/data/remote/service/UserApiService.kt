package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import retrofit2.http.*

interface UserApiService {
    @GET("users/me")
    suspend fun getMyProfile(): ApiResponse<UserDto>

    @GET("users/{id}")
    suspend fun getUserProfile(@Path("id") userId: String): ApiResponse<UserDto>

    @PUT("users/me")
    suspend fun updateProfile(@Body request: Map<String, String?>): ApiResponse<Unit>

    @PUT("users/me/avatar")
    suspend fun updateAvatar(@Body request: Map<String, String?>): ApiResponse<UserDto>

    @PUT("users/me/password")
    suspend fun changePassword(@Body request: Map<String, String>): ApiResponse<Unit>

    @PUT("users/me/email")
    suspend fun changeEmail(@Body request: Map<String, String>): ApiResponse<Unit>

    @POST("users/{userId}/follow")
    suspend fun followUser(@Path("userId") userId: String): ApiResponse<Unit>

    @DELETE("users/{userId}/follow")
    suspend fun unfollowUser(@Path("userId") userId: String): ApiResponse<Unit>

    @GET("users/{id}/followers")
    suspend fun getFollowers(
        @Path("id") userId: String,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): ApiResponse<List<UserDto>>

    @GET("users/{id}/following")
    suspend fun getFollowing(
        @Path("id") userId: String,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): ApiResponse<List<UserDto>>

    @DELETE("users/me")
    suspend fun deleteAccount(): ApiResponse<Unit>
}
