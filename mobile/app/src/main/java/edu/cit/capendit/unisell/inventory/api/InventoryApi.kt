package edu.cit.capendit.unisell.inventory.api

import edu.cit.capendit.unisell.inventory.model.ProductPlatformInventoryRequest
import edu.cit.capendit.unisell.inventory.model.ProductPlatformInventoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InventoryApi {

    @GET("products/{productId}/inventory")
    suspend fun getAllocations(@Path("productId") productId: Long): Response<List<ProductPlatformInventoryResponse>>

    @POST("products/{productId}/inventory")
    suspend fun allocateStock(
        @Path("productId") productId: Long,
        @Body request: ProductPlatformInventoryRequest
    ): Response<ProductPlatformInventoryResponse>

    @PUT("products/{productId}/inventory/{platformId}")
    suspend fun updateAllocation(
        @Path("productId") productId: Long,
        @Path("platformId") platformId: Long,
        @Body request: ProductPlatformInventoryRequest
    ): Response<ProductPlatformInventoryResponse>

    @DELETE("products/{productId}/inventory/{platformId}")
    suspend fun deleteAllocation(
        @Path("productId") productId: Long,
        @Path("platformId") platformId: Long
    ): Response<Unit>
}