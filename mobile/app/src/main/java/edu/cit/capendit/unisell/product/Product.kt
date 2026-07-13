package edu.cit.capendit.unisell.product

data class ProductRequest(
    val name: String,
    val sku: String,
    val price: Double,
    val quantity: Int,
    val lowStockThreshold: Int?,
    val categoryId: Long
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val sku: String,
    val price: Double,
    val quantity: Int,
    val lowStockThreshold: Int,
    val categoryId: Long,
    val categoryName: String
)