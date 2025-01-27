package com.krm.rentalservices

data class AddFireStoreState(
    var isLoading: Boolean = false,
    var success: Int = 0,
    var data: String = "",
    var error: String = "",
    var internet: Boolean = false,
    val isEventHandled: Boolean = false // Event flag
)