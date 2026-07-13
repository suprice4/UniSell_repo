package edu.cit.capendit.unisell.inventory.model

data class ProductPlatformInventoryRequest(
    val platformId: Long,
    val allocatedQuantity: Int
)

data class ProductPlatformInventoryResponse(
    val id: Long,
    val platformId: Long,
    val platformName: String,
    val productId: Long,
    val allocatedQuantity: Int
)