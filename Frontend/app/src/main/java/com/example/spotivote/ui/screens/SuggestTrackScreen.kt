package com.example.spotivote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.model.Track
import com.example.spotivote.model.User
import com.example.spotivote.service.spotifyService
import com.example.spotivote.ui.components.NavBar

@Composable
fun SuggestTrackScreen(
    accessToken: String, user: User, onSuggestTrack: (trackId: String, userId: String) -> Unit
) {
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var trackId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val response = spotifyService.getUserTopItems("tracks", 10, "Bearer $accessToken")

        tracks = response.items.map { track ->
            val artists: String = track.artists.joinToString(separator = ", ") { track.name }
            Track(
                id = track.id,
                name = track.name,
                artists = artists,
                imageUri = track.album.images.elementAt(0).url,
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            NavBar(user = user)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Suggest Track",
                    style = MaterialTheme.typography.h1,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Column(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(text = "Your most listened songs", style = MaterialTheme.typography.h2)

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(
                                    shape = RoundedCornerShape(
                                        topStart = 6.dp, topEnd = 6.dp
                                    )
                                )
                                .height(400.dp)
                                .background(color = Color(0xFF404040))
                        ) {
                            LazyColumn {
                                items(items = tracks, itemContent = { track ->
                                    Box(
                                        modifier = Modifier
                                            .clickable(onClick = {
                                                trackId = track.id
                                            })
                                            .background(
                                                color = if (trackId == track.id) Color(
                                                    0xFF303030
                                                ) else Color.Transparent
                                            )
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                            .align(Alignment.CenterStart)
                                    ) {
                                        AsyncImage(
                                            model = track.imageUri,
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
                                })
                            }
                        }

//                        Box(modifier = Modifier
//                            .clip(
//                                shape = RoundedCornerShape(
//                                    bottomStart = 6.dp, bottomEnd = 6.dp
//                                )
//                            )
//                            .background(color = Color(0xFF404040))
//                            .fillMaxWidth()
//                            .height(50.dp)
//                            .clickable { onSuggestTrack() }) {
//                            Text(
//                                text = "Search another",
//                                modifier = Modifier
//                                    .align(Alignment.Center)
//                                    .padding(horizontal = 24.dp)
//                            )
//                        }
                    }
                }

                Spacer(
                    modifier = Modifier.defaultMinSize(minHeight = 24.dp)
                )
                if (trackId != "") {
                    Button(
                        onClick = {
                            onSuggestTrack(trackId, user.id)
                        }, modifier = Modifier
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
    }
}
