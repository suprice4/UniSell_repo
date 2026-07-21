package edu.cit.capendit.unisell.admin.returns.model

data class AdminReturnResponse(
    val id: Long,
    val orderId: Long,
    val vendorName: String,
    val vendorEmail: String,
    val productName: String,
    val quantity: Int,
    val reason: String?,
    val returnedAt: String
)
