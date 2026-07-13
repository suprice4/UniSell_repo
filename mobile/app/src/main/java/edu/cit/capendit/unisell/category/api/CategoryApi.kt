package edu.cit.capendit.unisell.category.api

import edu.cit.capendit.unisell.category.model.CategoryRequest
import edu.cit.capendit.unisell.category.model.CategoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryApi {

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryResponse>>

    @POST("categories")
    suspend fun createCategory(@Body request: CategoryRequest): Response<CategoryResponse>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Long,
        @Body request: CategoryRequest
    ): Response<CategoryResponse>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long): Response<Unit>
}