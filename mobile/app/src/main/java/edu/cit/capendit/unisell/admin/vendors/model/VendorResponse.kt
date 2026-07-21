package edu.cit.capendit.unisell.admin.vendors.model

data class VendorResponse(
    val id: Long,
    val name: String,
    val email: String,
    val enabled: Boolean
)

data class VendorStatusUpdateRequest(
    val enabled: Boolean
)
