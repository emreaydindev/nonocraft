package com.neilb.nonocraft.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val pagination: Pagination? = null
)

@Serializable
data class Pagination(
    val total: Int,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)
