package com.krm.rentalservices.model

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import java.util.Date

data class RentalOrder(
    @SerializedName("orderId") var orderId: String = "",
    @SerializedName("customerId") var customerId: String = "",
    @SerializedName("customerName") var customerName: String = "",
    @SerializedName("orderDate") var orderDate: Long? = null,  // Convert Date to Long
    @SerializedName("orderRtnDate") var orderRtnDate: Long? = null,  // Convert Date to Long
    @SerializedName("orderStatus") var orderStatus: String = "",
    @SerializedName("orderItemList") var orderItemList: List<OrderItem> = emptyList(),
    @SerializedName("paymentList") var paymentList: List<Payment> = emptyList(),
    @SerializedName("otherChargesList") var otherChargesList: List<OtherCharges> = emptyList(),
    @SerializedName("totalAmt") var totalAmt: Long = 0,
    @SerializedName("discountAmt") var discountAmt: Long = 0,
    @SerializedName("paidAmt") var paidAmt: Long = 0,
    @SerializedName("balanceAmt") var balanceAmt: Long = 0,
    @SerializedName("returnOrderDate") var returnOrderDate: Long? = null,
    var timestamp: Timestamp? = null
) {
    // Helper functions to convert back to Date and Timestamp
    fun getOrderDateAsDate(): Date? = orderDate?.let { Date(it) }
    fun getReturnOrderDateAsDate(): Date? = returnOrderDate?.let { Date(it) }
    override fun toString(): String {
        return "RentalOrder(" +
                "orderId='$orderId', " +
                "customerId='$customerId', " +
                "customerName='$customerName', " +
                "orderDate=$orderDate, " +
                "orderRtnDate=$orderRtnDate, " +
                "orderStatus='$orderStatus', " +
                "orderItemList=$orderItemList, " +
                "paymentList=$paymentList, " +
                "otherChargesList=$otherChargesList, " +
                "totalAmt=$totalAmt, " +
                "discountAmt=$discountAmt, " +
                "paidAmt=$paidAmt, " +
                "balanceAmt=$balanceAmt, " +
                "returnOrderDate=$returnOrderDate, " +
                "timestamp=$timestamp" +
                ")"
    }
}


