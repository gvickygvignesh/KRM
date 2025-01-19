package com.krm.rentalservices.model

import com.google.firebase.Timestamp
import java.util.Date

data class RentalOrder(
    var customerId: String = "",
    var customerName: String = "",
    var orderDate: Date?,
    var orderStatus: String = "",
    var orderItemList: List<OrderItem> = emptyList(),
    var totalAmt: Long = 0,
    var paidAmt: Long = 0,
    var balanceAmt: Long = 0,
    var timestamp: Timestamp?
)
