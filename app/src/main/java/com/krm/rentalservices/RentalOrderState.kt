package com.krm.rentalservices

import com.krm.rentalservices.model.RentalOrder

data class RentalOrderState(
    var isLoading: Boolean = false,
    var success: Int = 0,
    var data: List<RentalOrder> = emptyList(),
    var error: String = "",
    var internet: Boolean = false
)