package com.krm.rentalservices.model

import com.google.firebase.Timestamp
import java.util.Date

data class RentalOrder(
    var orderId: String = "",
    var customerId: String = "",
    var customerName: String = "",
    var orderDate: Date? = null,
    var orderStatus: String = "",
    var orderItemList: List<OrderItem> = emptyList(),
    var paymentList: List<Payment> = emptyList(),
    var otherChargesList: List<OtherCharges> = emptyList(),
    var totalAmt: Long = 0,
    var paidAmt: Long = 0,
    var balanceAmt: Long = 0,
    var returnOrderDate: Date? = null,
    var timestamp: Timestamp? = null
)
