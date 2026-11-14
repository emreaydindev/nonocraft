package com.neilb.nonocraft.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val pagination: Pagination? = null
)

/** Re-wraps the payload, e.g. a bare list into the list response type the repositories expose. */
inline fun <T, R> ApiResponse<T>.map(transform: (T) -> R): ApiResponse<R> =
    ApiResponse(success = success, data = data?.let(transform), error = error, pagination = pagination)

@Serializable
data class Pagination(
    val total: Int,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)
