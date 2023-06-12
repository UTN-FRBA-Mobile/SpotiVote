package com.example.spotivote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.service.dto.PlaylistItem

val defaultImage =
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRwZ4mTuUvdD6l60AzmWTIZ341ALx1udRQn3zv5va8czuI5VNApMbGqiIJGSuoe1EhreQY&usqp=CAU"


@Composable
fun PlaylistRowItem(playlist: PlaylistItem, onClick: () -> Unit, selected: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .background(
                color = if (selected) Color(
                    0xFF303030
                ) else Color.Transparent
            )
            .padding(12.dp)
    ) {
        AsyncImage(
            model = if (playlist.tracks.total != 0) playlist.images.elementAt(
                0
            ).url else defaultImage,
            contentDescription = "Playlist Image",
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .size(50.dp)
                .align(Alignment.CenterStart)
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .padding(start = 60.dp)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.body1,
            )
            Text(
                text = "${playlist.tracks.total} song${(if (playlist.tracks.total != 1) "s" else "")}",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
    }
}