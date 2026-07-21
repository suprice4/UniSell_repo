package edu.cit.capendit.unisell.admin.vendors.api

import edu.cit.capendit.unisell.admin.vendors.model.VendorResponse
import edu.cit.capendit.unisell.admin.vendors.model.VendorStatusUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminVendorsApi {

    @GET("admin/vendors")
    suspend fun getVendors(): Response<List<VendorResponse>>

    @PUT("admin/vendors/{id}/status")
    suspend fun updateVendorStatus(
        @Path("id") id: Long,
        @Body request: VendorStatusUpdateRequest
    ): Response<VendorResponse>
}
