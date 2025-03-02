package com.krm.rentalservices.model

data class OrderItem(
    var productId: String = "",
    var productName: String = "",
    var quantity: Int = 0,
    var rtnQty: Int = 0,
    var days: Int = 0,
    var rentalPrice: Long = 0,
    var amount: Long = 0,
)