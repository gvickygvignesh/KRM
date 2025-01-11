package com.krm.rentalservices.model

import com.google.firebase.Timestamp

data class Customer(
    var id: String = "",
    var name: String = "",
    var type: String = "",
    var address: String = "",
    var mobNo: String = "",
    var idNo: String = "",
    var idType: String = "",
    var idPhoto: String = "",
    var timeStamp: Timestamp?
)
