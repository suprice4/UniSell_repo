package edu.cit.capendit.unisell.api

import edu.cit.capendit.unisell.model.AuthResponse
import edu.cit.capendit.unisell.model.LoginRequest
import edu.cit.capendit.unisell.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}