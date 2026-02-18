package com.farmledger.app.model

data class ProductResponse(
    val success: Boolean,
    val products: List<Product>
)
