package edu.cit.capendit.unisell.admin.payments.api

import edu.cit.capendit.unisell.admin.payments.model.AdminPaymentResponse
import retrofit2.Response
import retrofit2.http.GET

interface AdminPaymentsApi {

    @GET("admin/payments")
    suspend fun getPayments(): Response<List<AdminPaymentResponse>>
}
