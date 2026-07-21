package edu.cit.capendit.unisell.admin.returns.api

import edu.cit.capendit.unisell.admin.returns.model.AdminReturnResponse
import retrofit2.Response
import retrofit2.http.GET

interface AdminReturnsApi {

    @GET("admin/returns")
    suspend fun getReturns(): Response<List<AdminReturnResponse>>
}
