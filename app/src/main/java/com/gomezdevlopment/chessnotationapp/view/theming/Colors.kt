package com.gomezdevlopment.chessnotationapp.view

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val teal = Color(0xFF03DA9D)
val tealDarker = Color(0xFF018786)
val transparentBlack = Color(0x4D000000)
val orange = Color(0xFFff9250)
val yellow = Color(0xFFFFE66A)
val purple = Color(0xFFAD5AC0)
val blue = Color(0xFF5D84E7)
val pink = Color(0xFFFF5DC3)
val textWhite = Color(0xFFDFDFDF)
val cardWhite = Color(0xFFFCFCFC)
val cardDark = Color(0xFF212928)
val navBarWhite = Color(0xFFF8F8F8)
val background = Color(0xFFDFF0F0)
val backgroundDark = Color(0xFF1B2525)
val white = Color(0xFFFFFFFF)

val greenCorrect = Color(0xFF4CE63B)
val redIncorrect = Color(0xFFE63B3B)

private val darkThemeColors = darkColors(
    primary = tealDarker,
    primaryVariant = teal,
    secondary = tealDarker,
    secondaryVariant = teal,
    background = backgroundDark,
    surface = cardDark,
    error = Color(0xFFB00020),
    onPrimary = textWhite,
    onSecondary = textWhite,
    onBackground = Color.White,
    onSurface = white,
    onError = Color.White,
)

private val lightThemeColors = lightColors(
    primary = tealDarker,
    primaryVariant = teal,
    secondary = tealDarker,
    secondaryVariant = teal,
    background = background,
    surface = cardWhite,
    error = Color(0xFFB00020),
    onPrimary = textWhite,
    onSecondary = textWhite,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
)

@Composable
fun AppTheme(darkTheme:Boolean, content: @Composable () -> Unit){
    MaterialTheme(colors = if(darkTheme) darkThemeColors  else lightThemeColors) {
        content()
    }
}