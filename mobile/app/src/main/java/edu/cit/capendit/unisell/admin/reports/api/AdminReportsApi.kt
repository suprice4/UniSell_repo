package edu.cit.capendit.unisell.admin.reports.api

import edu.cit.capendit.unisell.admin.reports.model.AdminVendorReportResponse
import retrofit2.Response
import retrofit2.http.GET

interface AdminReportsApi {

    @GET("admin/reports")
    suspend fun getVendorReports(): Response<List<AdminVendorReportResponse>>
}
