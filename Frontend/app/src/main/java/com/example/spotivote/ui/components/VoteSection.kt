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
import com.example.spotivote.service.Callbacks
import com.example.spotivote.service.WebSocketListener
import com.example.spotivote.service.spotifyService
import com.example.spotivote.ui.screens.RoomConfig
import okhttp3.OkHttpClient
import okhttp3.Request


data class TrackInPoll(val track: Track, var votes: Int)

@Composable
fun VoteSection(roomConfig: RoomConfig, accessToken: String) {
    var tracks by remember { mutableStateOf<List<TrackInPoll>>(emptyList()) }
    var trackId by remember { mutableStateOf("") }

    // cambiar para que vaya a buscar las que se pueden votar ahora...
    LaunchedEffect(Unit) {
        val playlist =
            spotifyService.getTracksByPlaylistId(roomConfig.playlistId, "Bearer $accessToken")
        // TODO: Probar testear la API backend de localhost
        // val playlist =
        //    localService.getTracksByPlaylistId(roomConfig.playlistId, "Bearer $accessToken")

        tracks = playlist.items.map { it ->
            TrackInPoll(
                Track(
                    id = it.track.id,
                    name = it.track.name,
                    artists = it.track.artists.joinToString(separator = ", ") { it.name },
                    imageUri = it.track.album.images.elementAt(0).url,
                ), 0
            )
        }
    }

    val client = OkHttpClient()
    val socketUrl =
        "wss://s9157.nyc1.piesocket.com/v3/1?api_key=SqxisaikNnHg4X7o3DxtIm7sAPCz6ho8Wu9Z76PF&notify_self=1"
    val request: Request = Request.Builder().url(socketUrl).build()

    val listener = WebSocketListener(
        Callbacks(
            thumbsDown = {
                println("Thumbs down")
            },
            thumbsUp = {
                println("Thumbs up")
                tracks[0].votes += 1
            },
        )
    )
    val ws = client.newWebSocket(request, listener)

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
                items(items = tracks, itemContent = { trackInPoll ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.CenterStart)
                        .clickable(onClick = {
                            // vote
                            trackId = trackInPoll.track.id
                        })
                        .background(
                            color = if (trackId == trackInPoll.track.id) Color(
                                0xFF303030
                            ) else Color.Transparent
                        )) {
                        Row() {
                            AsyncImage(
                                model = trackInPoll.track.imageUri,
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
                                    text = trackInPoll.track.name,
                                    style = MaterialTheme.typography.body1,
                                    color = if (trackId == trackInPoll.track.id) Color.Green else Color.White
                                )
                                Text(
                                    text = trackInPoll.track.artists,
                                    style = MaterialTheme.typography.body2,
                                    color = Color.Gray
                                )
                            }

                            Spacer(
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = trackInPoll.votes.toString(),
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                })
            }
        }
    }
}