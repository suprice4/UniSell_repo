package edu.cit.capendit.unisell.core.util

const val DEFAULT_LOW_STOCK_THRESHOLD = 5

fun isLowStock(quantity: Int, threshold: Int?): Boolean =
    quantity < (threshold ?: DEFAULT_LOW_STOCK_THRESHOLD)
