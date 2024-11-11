package com.krm.rentalservices

data class AddInventoryState(
    var isLoading: Boolean = false,
    var success: Int = 0,
    var data: String = "",
    var error: String = "",
    var internet: Boolean = false
)