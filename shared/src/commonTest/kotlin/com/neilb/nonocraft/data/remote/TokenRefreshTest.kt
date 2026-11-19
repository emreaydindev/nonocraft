package com.neilb.nonocraft.data.remote

import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.remote.service.UserApiService
import com.neilb.nonocraft.data.repository.UserRepositoryImpl
import com.russhwolf.settings.MapSettings
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TokenRefreshTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val user = """{"id":"u1","email":"a@b.c","username":"neil","bio":null,"isPremium":false,
        "emailVerified":true,"stars":5,"streakCount":0}"""

    private fun tokenManager(access: String?, refresh: String?) = TokenManager(MapSettings()).apply {
        accessToken = access
        refreshToken = refresh
    }

    @Test
    fun expiredAccessTokenIsRefreshedAndRequestRepeated() = runTest {
        val tokens = tokenManager(access = "expired", refresh = "refresh-1")
        val paths = mutableListOf<String>()
        val engine = MockEngine { request ->
            paths += request.url.encodedPath
            val bearer = request.headers[HttpHeaders.Authorization]
            when {
                request.url.encodedPath.endsWith("auth/refresh") -> respond(
                    """{"success":true,"data":{"accessToken":"fresh","refreshToken":"refresh-2","user":null}}""",
                    HttpStatusCode.OK, jsonHeaders,
                )
                bearer == "Bearer fresh" -> respond("""{"success":true,"data":$user}""", HttpStatusCode.OK, jsonHeaders)
                else -> respond("""{"success":false,"error":"Unauthorized"}""", HttpStatusCode.Unauthorized, jsonHeaders)
            }
        }
        val repository = UserRepositoryImpl(UserApiService(createHttpClient(engine, createJson(), tokens)))

        val result = repository.getMyProfile()

        assertIs<Resource.Success<*>>(result)
        assertEquals("neil", result.data?.username)
        assertEquals("fresh", tokens.accessToken)
        assertEquals("refresh-2", tokens.refreshToken)
        assertEquals(listOf("/users/me", "/auth/refresh", "/users/me"), paths)
    }

    @Test
    fun failedRefreshSurfacesTheOriginalError() = runTest {
        val tokens = tokenManager(access = "expired", refresh = "revoked")
        val engine = MockEngine {
            respond("""{"success":false,"error":"Oturum süresi dolmuş"}""", HttpStatusCode.Unauthorized, jsonHeaders)
        }
        val repository = UserRepositoryImpl(UserApiService(createHttpClient(engine, createJson(), tokens)))

        val result = repository.getMyProfile()

        assertIs<Resource.Error<*>>(result)
        assertEquals(ErrorCode.UNAUTHORIZED, result.error.code)
        assertEquals(401, result.error.httpStatus)
        assertEquals("Oturum süresi dolmuş", result.error.detail)
    }
}
