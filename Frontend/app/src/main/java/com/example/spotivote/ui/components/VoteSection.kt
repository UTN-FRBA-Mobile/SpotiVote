package com.example.spotivote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.model.Track
import com.example.spotivote.service.spotifyService
import com.example.spotivote.service.localService
import com.example.spotivote.ui.screens.RoomConfig


@Composable
fun VoteSection(roomConfig: RoomConfig, accessToken: String) {
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var trackId by remember { mutableStateOf("") }

    // cambiar para que vaya a buscar las que se pueden votar ahora...
    LaunchedEffect(Unit) {
        val playlist =
            spotifyService.getTracksByPlaylistId(roomConfig.playlistId, "Bearer $accessToken")
        // TODO: Probar testear la API backend de localhost
        // val playlist =
        //    localService.getTracksByPlaylistId(roomConfig.playlistId, "Bearer $accessToken")

        tracks = playlist.items.map { it ->
            Track(
                id = it.track.id,
                name = it.track.name,
                artists = it.track.artists.joinToString(separator = ", ") { it.name },
                imageUri = it.track.album.images.elementAt(0).url,
            )
        }
    }

    Column(
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Text(text = "Vote next track", style = MaterialTheme.typography.h2)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(6.dp))
                .background(color = Color(0xFF404040))
                .fillMaxHeight(0.76f)
        ) {
            LazyColumn {
                items(items = tracks, itemContent = { track ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.CenterStart)
                            .clickable(
                                onClick = {
                                    // vote
                                    trackId = track.id
                                }
                            )
                            .background(
                                color = if (trackId == track.id) Color(
                                    0xFF303030
                                ) else Color.Transparent
                            )
                    ) {
                        Row() {
                            AsyncImage(
                                model = track.imageUri,
                                contentDescription = "Playlist Image",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .size(50.dp)
                                    .fillMaxSize()
                            )
                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Column() {
                                Text(
                                    text = track.name,
                                    style = MaterialTheme.typography.body1,
                                    color = if (trackId == track.id) Color.Green else Color.White
                                )
                                Text(
                                    text = track.artists,
                                    style = MaterialTheme.typography.body2,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                })
            }
        }
    }
}