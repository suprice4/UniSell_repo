package edu.cit.capendit.unisell.category

data class CategoryRequest(
    val name: String
)

data class CategoryResponse(
    val id: Long,
    val name: String
)