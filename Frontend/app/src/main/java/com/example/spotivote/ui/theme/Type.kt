package com.example.spotivote.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.spotivote.R

@OptIn(ExperimentalTextApi::class)
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

@OptIn(ExperimentalTextApi::class)
val fontName = GoogleFont("Work Sans")

@OptIn(ExperimentalTextApi::class)
val fontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = fontFamily,
    h1 = TextStyle(
        color = Green,
        textAlign = TextAlign.Left,
        fontSize = 36.sp,
        fontWeight = FontWeight.Medium
    ),
    h2 = TextStyle(
        color = Color(0xFFFAFAFA),
        textAlign = TextAlign.Left,
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        textAlign = TextAlign.Left
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        textAlign = TextAlign.Left
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        textAlign = TextAlign.Left
    ),
    button = TextStyle(
        color = Color(0xFFFAFAFA),
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    ),
)