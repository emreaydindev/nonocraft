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
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ErrorCodeMappingTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    private fun repository(engine: MockEngine): UserRepositoryImpl {
        val tokens = TokenManager(MapSettings()).apply { accessToken = "token" }
        return UserRepositoryImpl(UserApiService(createHttpClient(engine, createJson(), tokens)))
    }

    @Test
    fun backendCodeWinsOverStatus() = runTest {
        val engine = MockEngine {
            respond("""{"success":false,"error":"taken","code":"USERNAME_TAKEN"}""", HttpStatusCode.Conflict, jsonHeaders)
        }
        val result = repository(engine).updateProfile("x", null)
        assertIs<Resource.Error<*>>(result)
        assertEquals(ErrorCode.USERNAME_TAKEN, result.error.code)
        assertEquals(409, result.error.httpStatus)
    }

    @Test
    fun unknownBackendCodeFallsBackToStatus() = runTest {
        val engine = MockEngine {
            respond("""{"success":false,"error":"Bu kullanıcı adı zaten alınmış","code":"SOMETHING_NEW"}""", HttpStatusCode.Conflict, jsonHeaders)
        }
        val result = repository(engine).updateProfile("x", null)
        assertIs<Resource.Error<*>>(result)
        assertEquals(ErrorCode.CONFLICT, result.error.code)
        assertEquals("Bu kullanıcı adı zaten alınmış", result.error.detail)
    }

    @Test
    fun nonJsonServerErrorMapsToServer() = runTest {
        val engine = MockEngine { respond("<html>oops</html>", HttpStatusCode.BadGateway) }
        val result = repository(engine).getMyProfile()
        assertIs<Resource.Error<*>>(result)
        assertEquals(ErrorCode.SERVER, result.error.code)
    }

    @Test
    fun ioFailureMapsToNetwork() = runTest {
        val engine = MockEngine { throw IOException("offline") }
        val result = repository(engine).getMyProfile()
        assertIs<Resource.Error<*>>(result)
        assertEquals(ErrorCode.NETWORK, result.error.code)
    }

    @Test
    fun statusFallbacks() {
        assertEquals(ErrorCode.VALIDATION, ErrorCode.fromHttpStatus(400))
        assertEquals(ErrorCode.UNAUTHORIZED, ErrorCode.fromHttpStatus(401))
        assertEquals(ErrorCode.FORBIDDEN, ErrorCode.fromHttpStatus(403))
        assertEquals(ErrorCode.NOT_FOUND, ErrorCode.fromHttpStatus(404))
        assertEquals(ErrorCode.RATE_LIMITED, ErrorCode.fromHttpStatus(429))
        assertEquals(ErrorCode.SERVER, ErrorCode.fromHttpStatus(503))
        assertEquals(ErrorCode.UNKNOWN, ErrorCode.fromHttpStatus(418))
    }
}
