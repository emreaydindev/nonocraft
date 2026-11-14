package com.neilb.nonocraft.data.remote

import com.neilb.nonocraft.common.Constants
import com.neilb.nonocraft.data.local.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

private const val TIMEOUT_MILLIS = 30_000L

fun createJson(): Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    coerceInputValues = true
    isLenient = true
}

fun createHttpClient(engine: HttpClientEngine, json: Json, tokenManager: TokenManager): HttpClient =
    HttpClient(engine) {
        // Non-2xx responses throw ResponseException, which safeApiCall maps to Resource.Error.
        expectSuccess = true

        install(ContentNegotiation) { json(json) }

        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT_MILLIS
            requestTimeoutMillis = TIMEOUT_MILLIS
            socketTimeoutMillis = TIMEOUT_MILLIS
        }

        defaultRequest {
            url(Constants.API_URL)
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            tokenManager.accessToken?.let { bearerAuth(it) }
        }
    }

/** Decodes a payload-less response; the server's `data` (a message, null or missing) is ignored. */
internal suspend fun HttpResponse.unitBody(): ApiResponse<Unit> {
    val raw = body<ApiResponse<JsonElement>>()
    return ApiResponse(success = raw.success, data = Unit, error = raw.error, pagination = raw.pagination)
}
