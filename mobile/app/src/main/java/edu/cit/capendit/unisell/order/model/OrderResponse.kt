package edu.cit.capendit.unisell.order.model

data class OrderResponse(
    val id: Long,
    val vendorId: Long,
    val platformId: Long,
    val platformName: String,
    val customerName: String?,
    val customerAddress: String?,
    val trackingNumber: String?,
    val courierName: String?,
    val status: String,
    val paymentStatus: String,
    val shipmentStatus: String,
    val totalAmount: Double,
    val createdAt: String
)

data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val priceAtTimeOfOrder: Double
)

data class StatusUpdateRequest(val status: String)
data class PaymentStatusUpdateRequest(val paymentStatus: String)
data class ShipmentStatusUpdateRequest(val status: String)
data class ShipmentDetailsRequest(val trackingNumber: String, val courierName: String)
data class ReturnRequest(val orderItemIds: List<Long>, val reason: String)

data class OrderItemRequest(val productId: Long, val quantity: Int)
data class OrderRequest(
    val platformId: Long,
    val customerName: String,
    val customerAddress: String,
    val items: List<OrderItemRequest>
)