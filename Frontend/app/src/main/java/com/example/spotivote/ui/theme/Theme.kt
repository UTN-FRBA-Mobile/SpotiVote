package com.example.spotivote.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

@Composable
fun SpotivoteTheme(content: @Composable () -> Unit) {
    val colors = darkColors(
        primary = Green,
        background = Dark,
        onBackground = Light
    )

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}