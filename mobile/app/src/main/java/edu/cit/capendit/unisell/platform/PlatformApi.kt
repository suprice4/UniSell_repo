package edu.cit.capendit.unisell.platform

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface PlatformApi {

    @GET("platforms")
    suspend fun getPlatforms(): Response<List<PlatformResponse>>

    @POST("platforms")
    suspend fun createPlatform(@Body request: PlatformRequest): Response<PlatformResponse>

    @PUT("platforms/{id}")
    suspend fun updatePlatform(
        @Path("id") id: Long,
        @Body request: PlatformRequest
    ): Response<PlatformResponse>

    @DELETE("platforms/{id}")
    suspend fun deletePlatform(@Path("id") id: Long): Response<Unit>
}