package com.krm.rentalservices

import com.krm.rentalservices.model.InventoryItem

data class InventoryState(
    var isLoading: Boolean = false,
    var success: Int = 0,
    var data: List<InventoryItem> = emptyList(),
    var error: String = "",
    var internet: Boolean = false
)