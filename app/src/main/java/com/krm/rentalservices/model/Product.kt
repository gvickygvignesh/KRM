package com.krm.rentalservices.model

import com.google.firebase.Timestamp

data class Product(
    var id: String = "",
    var name: String = "",
    var rentalPrice: Long = 0,
    var description: String = "",
    var timestamp: Timestamp?
)
