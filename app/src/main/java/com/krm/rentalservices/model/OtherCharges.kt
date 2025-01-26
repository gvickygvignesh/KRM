package com.krm.rentalservices.model

data class OtherCharges(
    var chargeType: String = "",
    var amount: Long = 0,
    var remarks: String = ""
)
