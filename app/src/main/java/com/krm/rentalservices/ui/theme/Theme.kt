package com.krm.rentalservices.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = cyan300,
    secondary = cyan400,
    tertiary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = black,
    secondary = deepOrange300,
    tertiary = deepOrange200,
////     Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color.White,
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
)

// Define button colors for light and dark themes
val ColorScheme.buttonColors
    @Composable
    get() = ButtonDefaults.buttonColors(
        containerColor = gray200,
        contentColor = Color.Black,
        disabledContainerColor = Color(0xFF9E9E9E),
        disabledContentColor = Color.Black
    )

@Composable
fun KRMRentalServicesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else
            dynamicLightColorScheme(context)
        }

//        darkTheme -> DarkColorScheme
        else ->
            LightColorScheme
    }


    // 🌟 Change the status bar color dynamically
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = black, // Change to any color you want
        darkIcons = !darkTheme // Adjust icons for better contrast
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )

}