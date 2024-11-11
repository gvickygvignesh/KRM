package com.krm.rentalservices.data

// Data model for adding item
data class ItemRequestBody(
    val range: String,
    val majorDimension: String = "ROWS",
    val values: List<String>
)