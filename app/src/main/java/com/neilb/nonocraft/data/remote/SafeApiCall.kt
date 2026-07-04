package com.neilb.nonocraft.data.remote

import com.google.gson.Gson
import com.neilb.nonocraft.common.Resource
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(call: suspend () -> ApiResponse<T>): Resource<T> {
    return try {
        val response = call()
        if (response.success) {
            @Suppress("UNCHECKED_CAST")
            Resource.Success(response.data as T)
        } else {
            Resource.Error(response.error ?: "Bilinmeyen bir hata oluştu")
        }
    } catch (e: HttpException) {
        val errorBody = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        }
        val errorMessage = try {
            val apiResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
            apiResponse.error ?: "Sunucu hatası (${e.code()})"
        } catch (_: Exception) {
            "Sunucu hatası (${e.code()})"
        }
        Resource.Error(errorMessage)
    } catch (_: IOException) {
        Resource.Error("Bağlantı hatası. Lütfen internetinizi kontrol edin.")
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Beklenmedik bir hata oluştu")
    }
}

suspend fun <T> safeApiCallRaw(call: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(call())
    } catch (e: HttpException) {
        val errorBody = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        }
        val errorMessage = try {
            val apiResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
            apiResponse.error ?: "Sunucu hatası (${e.code()})"
        } catch (_: Exception) {
            "Sunucu hatası (${e.code()})"
        }
        Resource.Error(errorMessage)
    } catch (_: IOException) {
        Resource.Error("Bağlantı hatası. Lütfen internetinizi kontrol edin.")
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Beklenmedik bir hata oluştu")
    }
}
