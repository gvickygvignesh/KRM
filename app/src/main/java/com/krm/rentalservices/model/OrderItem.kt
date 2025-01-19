package com.krm.rentalservices.model

data class OrderItem(
    var productId: String,
    var productName: String,
    var quantity: Int,
    var days: Int,
    var rentalPrice: Long,
    var price: Long
)