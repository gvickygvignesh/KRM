package com.krm.rentalservices.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Utils {
    companion object {
        fun getCurrentDateTime(): String {
            val current = LocalDateTime.now() // Get the current datetime
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss") // Define the format
            return current.format(formatter) // Format the datetime
        }
    }
}