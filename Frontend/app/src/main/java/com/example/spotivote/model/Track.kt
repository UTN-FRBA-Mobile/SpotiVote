package com.example.spotivote.model

import android.graphics.Bitmap

data class Track(
    val _id: String = "", val id: String = "", val name: String = "", val artists: String = "",
    val playlistId: String = "", val imageUri: String? = "", var addedById: String = "",
    val imageBitmap: Bitmap? = null
)

