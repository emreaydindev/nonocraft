package com.neilb.nonocraft.data.remote

import com.neilb.nonocraft.common.Constants
import com.neilb.nonocraft.data.local.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.plugin
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.neilb.nonocraft.data.remote.dto.LoginResponse
import com.neilb.nonocraft.data.remote.dto.RefreshTokenRequest
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

fun createHttpClient(engine: HttpClientEngine, json: Json, tokenManager: TokenManager): HttpClient {
    val client = HttpClient(engine) {
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
    // Refresh calls go through a bare client so they never re-enter the 401 handling below.
    val refreshClient = HttpClient(engine) {
        install(ContentNegotiation) { json(json) }
        defaultRequest {
            url(Constants.API_URL)
            contentType(ContentType.Application.Json)
        }
    }
    client.installTokenRefresh(tokenManager, refreshClient)
    return client
}

private const val REFRESH_PATH = "auth/refresh"

/**
 * When a request comes back 401 and a refresh token is stored, exchanges it for a new token pair
 * and repeats the request once. Concurrent 401s share a single refresh.
 */
private fun HttpClient.installTokenRefresh(tokenManager: TokenManager, refreshClient: HttpClient) {
    val refreshMutex = Mutex()

    plugin(HttpSend).intercept { request ->
        val call = execute(request)
        val isAuthEndpoint = request.url.encodedPath.contains("/auth/")
        if (call.response.status != HttpStatusCode.Unauthorized || isAuthEndpoint) return@intercept call

        val rejectedToken = request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")
        val refreshed = refreshMutex.withLock {
            // Another request may have refreshed while this one waited for the lock.
            val current = tokenManager.accessToken
            if (current != null && current != rejectedToken) return@withLock true
            val refreshToken = tokenManager.refreshToken ?: return@withLock false
            try {
                val response = refreshClient.post(REFRESH_PATH) { setBody(RefreshTokenRequest(refreshToken)) }
                if (!response.status.isSuccess()) return@withLock false
                val tokens = response.body<ApiResponse<LoginResponse>>().data ?: return@withLock false
                tokenManager.accessToken = tokens.accessToken
                tokenManager.refreshToken = tokens.refreshToken
                true
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                false
            }
        }
        if (!refreshed) return@intercept call

        request.headers.remove(HttpHeaders.Authorization)
        tokenManager.accessToken?.let { request.bearerAuth(it) }
        execute(request)
    }
}

/** Decodes a payload-less response; the server's `data` (a message, null or missing) is ignored. */
internal suspend fun HttpResponse.unitBody(): ApiResponse<Unit> {
    val raw = body<ApiResponse<JsonElement>>()
    return ApiResponse(success = raw.success, data = Unit, error = raw.error, pagination = raw.pagination)
}
