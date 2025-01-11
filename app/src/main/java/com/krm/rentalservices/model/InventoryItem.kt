package com.krm.rentalservices.model

import com.google.firebase.Timestamp

data class InventoryItem(
    var prodId: String = "",
    var prodName: String = "",
    var totCount: Int = 0,
    var rentedCount: Int = 0,
    var avlCount: Int = 0,
    var damagedCount: Int = 0,
    var timestamp: Timestamp?
)
