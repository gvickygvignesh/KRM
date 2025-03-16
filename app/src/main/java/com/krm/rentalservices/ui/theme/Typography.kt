package com.krm.rentalservices.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.krm.rentalservices.R  // Ensure correct package reference

// Define custom font family
val CustomFont = FontFamily(
    Font(R.font.regular, FontWeight.Normal),
    Font(R.font.bold, FontWeight.Bold),
//    Font(R.font.custom_italic, FontWeight.it)
)

// Define custom typography
val CustomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)
