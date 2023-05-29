package com.example.spotivote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.model.Track
import com.example.spotivote.model.User
import com.example.spotivote.service.spotifyService
import java.net.HttpURLConnection


@Composable
fun RoomByIdScreen(
    accessToken: String, roomName: String, playlistId: String, onGoToSuggestTrack: () -> Unit
) {
    var trackCurrentlyPlaying by remember { mutableStateOf(Track()) }
    var user by remember { mutableStateOf(User()) }
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }

    LaunchedEffect(Unit) {
        val response = spotifyService.getCurrentlyPlaying("Bearer $accessToken")
        if (response.code() == HttpURLConnection.HTTP_OK) {
            val artists: String =
                response.body()!!.item.artists.joinToString(separator = ", ") { it.name }

            trackCurrentlyPlaying = Track(
                id = response.body()!!.item.id,
                name = response.body()!!.item.name,
                artists = artists,
                playlistId = playlistId,
                imageUri = response.body()!!.item.album.images.elementAt(0).url,
            )


            if (playlistId.isNotEmpty()) {
                val tracksResponse = spotifyService.getTracksByPlaylistId(
                    playlistId, "Bearer $accessToken"
                )
                if (tracksResponse.code() != HttpURLConnection.HTTP_OK) return@LaunchedEffect
                val track =
                    tracksResponse.body()!!.items.find { it.track.id == response.body()!!.item.id }


                val userResponse = if (track != null) {
                    spotifyService.getUserById(
                        track.added_by.id, "Bearer $accessToken"
                    )
                } else {
                    spotifyService.getMe(
                        "Bearer $accessToken"
                    )
                }

                trackCurrentlyPlaying.addedById = userResponse.id
                user = User(userResponse.display_name, userResponse.images.first().url)
            }

            val playlistResponse =
                spotifyService.getTracksByPlaylistId(playlistId, "Bearer $accessToken")

            if (playlistResponse.code() != HttpURLConnection.HTTP_OK) return@LaunchedEffect
            val playlist = playlistResponse.body()!!

            tracks = playlist.items.map { it ->
                Track(
                    id = it.track.id,
                    name = it.track.name,
                    artists = it.track.artists.joinToString(separator = ", ") { it.name },
                    imageUri = it.track.album.images.elementAt(0).url,
                )
            }.slice(0..2)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Room $roomName",
                style = MaterialTheme.typography.h1,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text(
                    text = "Playing now", style = MaterialTheme.typography.h2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(6.dp))
                            .background(color = Color(0xFF404040))
                            .padding(12.dp)
                    ) {
                        AsyncImage(
                            model = trackCurrentlyPlaying.imageUri,
                            contentDescription = "Track Image",
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
                                text = trackCurrentlyPlaying.name,
                                style = MaterialTheme.typography.body1
                            )
                            Text(
                                text = trackCurrentlyPlaying.artists,
                                style = MaterialTheme.typography.body2,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                    AsyncImage(
                        model = user.imageUri,
                        contentDescription = "User Image",
                        modifier = Modifier
                            .clip(CircleShape)
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
                            text = user.displayName, style = MaterialTheme.typography.body1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(text = "Vote next track", style = MaterialTheme.typography.h2)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(6.dp))
                        .background(color = Color(0xFF404040))
                ) {
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(items = tracks, itemContent = { track ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .align(Alignment.CenterStart)
//                                    .clickable(onClick = { playTrack(track.id) })
                            ) {
                                AsyncImage(
                                    model = track.imageUri,
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
                                        text = track.name, style = MaterialTheme.typography.body1
                                    )
                                    Text(
                                        text = track.artists,
                                        style = MaterialTheme.typography.body2,
                                        color = Color.Gray
                                    )
                                }
                            }
                        })
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(34.dp)
            )

            Button(
                onClick = { onGoToSuggestTrack() },
                modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(100.dp))
            ) {
                Text(
                    text = "Suggest Track",
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}
