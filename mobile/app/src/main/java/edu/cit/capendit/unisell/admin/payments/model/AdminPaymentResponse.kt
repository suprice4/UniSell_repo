package edu.cit.capendit.unisell.admin.payments.model

data class AdminPaymentResponse(
    val orderId: Long,
    val vendorName: String,
    val vendorEmail: String,
    val platformName: String,
    val totalAmount: Double,
    val paymentStatus: String,
    val createdAt: String
)
