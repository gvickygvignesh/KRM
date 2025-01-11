package com.krm.rentalservices

import com.krm.rentalservices.model.Product

data class ProdState(
    var isLoading: Boolean = false,
    var success: Int = 0,
    var data: List<Product> = emptyList(),
    var error: String = "",
    var internet: Boolean = false
)