package edu.cit.capendit.unisell.admin.reports.model

data class AdminVendorReportResponse(
    val vendorId: Long,
    val vendorName: String,
    val vendorEmail: String,
    val totalOrders: Long,
    val totalInventory: Int,
    val paymentStatusBreakdown: Map<String, Long>
)
