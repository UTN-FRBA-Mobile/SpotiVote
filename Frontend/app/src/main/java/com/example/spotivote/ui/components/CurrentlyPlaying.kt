package com.example.spotivote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.model.Track
import com.example.spotivote.model.User
import com.example.spotivote.service.spotifyService


@Composable
fun CurrentlyPlaying(
    accessToken: String
) {
    var track by remember { mutableStateOf(Track()) }
    var user by remember { mutableStateOf(User()) }

    // esto tiene que escuchar eventos de websockets para actualizar la canción
    LaunchedEffect(accessToken) {
        val currentlyPlayingResponse = spotifyService.getCurrentlyPlaying("Bearer $accessToken")

        if (currentlyPlayingResponse.code() == 200) {
            val currentlyPlaying = currentlyPlayingResponse.body()!!
            val currentTrack = currentlyPlaying.item

            if (currentlyPlaying.is_playing && currentTrack != null) {
                val artists = currentTrack.artists.joinToString(separator = ", ") { it.name }
                track = Track(
                    id = currentTrack.id,
                    name = currentTrack.name,
                    artists = artists,
                    imageUri = currentTrack.album.images.elementAt(0).url,
                )
            }
        } else {
            println("Error getting currently playing track")
            return@LaunchedEffect
        }
    }

    // reemplazar por el usuario que agregó la canción
    LaunchedEffect(accessToken) {
        val userResponse = spotifyService.getMe("Bearer $accessToken")
        user = User(userResponse.display_name, userResponse.images.elementAt(0).url)
    }

    return Column(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Playing now", style = MaterialTheme.typography.h2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color(0xFF404040))
                    .padding(12.dp)
            ) {
                Row() {
                    AsyncImage(
                        model = track.imageUri,
                        contentDescription = "Track Image",
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .size(80.dp)
                            .fillMaxSize()
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = track.name, style = MaterialTheme.typography.body1
                        )
                        Text(
                            text = track.artists,
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TODO: ajustar tamaño y agregar botones 👍 👎
        Column {
            Text(text = "Added by", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color(0xFF404040))
                    .padding(12.dp)
            ) {
                Row {
                    AsyncImage(
                        model = user.imageUri,
                        contentDescription = "User Image",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(36.dp)
                            .fillMaxSize()
                    )
                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )
                    Text(
                        text = user.displayName, style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}
