package edu.cit.capendit.unisell.auth.api

import edu.cit.capendit.unisell.auth.dto.AuthResponse
import edu.cit.capendit.unisell.auth.dto.LoginRequest
import edu.cit.capendit.unisell.auth.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}