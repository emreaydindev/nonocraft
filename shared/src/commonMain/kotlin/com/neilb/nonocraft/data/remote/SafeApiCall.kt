package com.neilb.nonocraft.data.remote

import com.neilb.nonocraft.common.AppError
import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.common.Resource
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Error body: `error` is developer text, `code` (when the backend sends it) is stable. */
@Serializable
private data class ErrorBody(val error: String? = null, val code: String? = null)

private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

@PublishedApi
internal suspend fun ResponseException.toAppError(): AppError {
    val status = response.status.value
    val body = try {
        errorJson.decodeFromString<ErrorBody>(response.bodyAsText())
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
    return AppError(
        code = ErrorCode.fromBackendCode(body?.code) ?: ErrorCode.fromHttpStatus(status),
        httpStatus = status,
        detail = body?.error,
    )
}

@PublishedApi
internal fun Exception.toAppError(): AppError = when (this) {
    is IOException -> AppError(ErrorCode.NETWORK, detail = message)
    else -> AppError(ErrorCode.UNKNOWN, detail = message)
}

suspend inline fun <reified T> safeApiCall(call: () -> ApiResponse<T>): Resource<T> {
    return try {
        val response = call()
        if (response.success) {
            // Endpoints without a payload are typed as Unit; their `data` is absent.
            Resource.Success(if (Unit is T) Unit as T else response.data as T)
        } else {
            Resource.Error(AppError(ErrorCode.UNKNOWN, detail = response.error))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        Resource.Error(e.toAppError())
    } catch (e: Exception) {
        Resource.Error(e.toAppError())
    }
}

suspend inline fun <T> safeApiCallRaw(call: () -> T): Resource<T> {
    return try {
        Resource.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        Resource.Error(e.toAppError())
    } catch (e: Exception) {
        Resource.Error(e.toAppError())
    }
}
