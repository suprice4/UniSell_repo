package edu.cit.capendit.unisell.order.api

import edu.cit.capendit.unisell.order.model.*
import retrofit2.Response
import retrofit2.http.*

interface OrderApi {
    @GET("orders")
    suspend fun getOrders(): Response<List<OrderResponse>>

    @GET("orders/{id}/items")
    suspend fun getOrderItems(@Path("id") id: Long): Response<List<OrderItemResponse>>

    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequest): Response<OrderResponse>

    @PUT("orders/{id}/status")
    suspend fun updateStatus(@Path("id") id: Long, @Body request: StatusUpdateRequest): Response<OrderResponse>

    @PUT("orders/{id}/payment-status")
    suspend fun updatePaymentStatus(@Path("id") id: Long, @Body request: PaymentStatusUpdateRequest): Response<OrderResponse>

    @PUT("orders/{id}/return")
    suspend fun processReturn(@Path("id") id: Long, @Body request: ReturnRequest): Response<OrderResponse>

    @PUT("orders/{id}/shipment-status")
    suspend fun markUncollected(@Path("id") id: Long, @Body request: ShipmentStatusUpdateRequest): Response<OrderResponse>

    @PUT("orders/{id}/shipment-details")
    suspend fun updateShipmentDetails(@Path("id") id: Long, @Body request: ShipmentDetailsRequest): Response<OrderResponse>

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Long): Response<Unit>
}