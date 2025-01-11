package com.krm.rentalservices

import com.krm.rentalservices.model.Customer

data class CustomerState(
    var isLoading: Boolean = false,
    var success: Int = 0,
    var data: List<Customer> = emptyList(),
    var error: String = "",
    var internet: Boolean = false
)