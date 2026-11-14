package com.neilb.nonocraft.data.remote

import com.neilb.nonocraft.common.Resource
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class ErrorBody(val error: String? = null)

private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

@PublishedApi
internal suspend fun ResponseException.toErrorMessage(): String {
    val code = response.status.value
    return try {
        errorJson.decodeFromString<ErrorBody>(response.bodyAsText()).error ?: "Sunucu hatası ($code)"
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        "Sunucu hatası ($code)"
    }
}

suspend inline fun <reified T> safeApiCall(call: () -> ApiResponse<T>): Resource<T> {
    return try {
        val response = call()
        if (response.success) {
            // Endpoints without a payload are typed as Unit; their `data` is absent.
            Resource.Success(if (Unit is T) Unit as T else response.data as T)
        } else {
            Resource.Error(response.error ?: "Bilinmeyen bir hata oluştu")
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        Resource.Error(e.toErrorMessage())
    } catch (_: IOException) {
        Resource.Error("Bağlantı hatası. Lütfen internetinizi kontrol edin.")
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Beklenmedik bir hata oluştu")
    }
}

suspend inline fun <T> safeApiCallRaw(call: () -> T): Resource<T> {
    return try {
        Resource.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        Resource.Error(e.toErrorMessage())
    } catch (_: IOException) {
        Resource.Error("Bağlantı hatası. Lütfen internetinizi kontrol edin.")
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Beklenmedik bir hata oluştu")
    }
}
