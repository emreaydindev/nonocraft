package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.unitBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class AuthApiService(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): ApiResponse<LoginResponse> =
        client.post("auth/login") { setBody(request) }.body()

    suspend fun register(request: RegisterRequest): ApiResponse<LoginResponse> =
        client.post("auth/register") { setBody(request) }.body()

    suspend fun refreshToken(request: RefreshTokenRequest): ApiResponse<LoginResponse> =
        client.post("auth/refresh") { setBody(request) }.body()

    suspend fun verifyEmail(request: VerifyEmailRequest): ApiResponse<Unit> =
        client.post("auth/verify-email") { setBody(request) }.unitBody()

    suspend fun forgotPassword(request: ForgotPasswordRequest): ApiResponse<Unit> =
        client.post("auth/forgot-password") { setBody(request) }.unitBody()

    suspend fun resetPassword(request: ResetPasswordRequest): ApiResponse<Unit> =
        client.post("auth/reset-password") { setBody(request) }.unitBody()

    suspend fun googleLogin(request: GoogleLoginRequest): ApiResponse<LoginResponse> =
        client.post("auth/google") { setBody(request) }.body()

    suspend fun logout(request: RefreshTokenRequest): ApiResponse<Unit> =
        client.post("auth/logout") { setBody(request) }.unitBody()
}
