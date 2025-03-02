package com.krm.rentalservices.model

data class Payment(
    var date: String = "",
    var payMode: String = "",
    var amount: Long = 0,
    var remarks: String = ""
)
