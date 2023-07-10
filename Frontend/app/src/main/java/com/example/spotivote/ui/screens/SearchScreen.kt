package com.example.spotivote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    accessToken: String, user: User, onSuggestTrack: (trackId: String, userId: String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var trackId by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        NavBar(user = user)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    coroutineScope.launch {
                        tracks =
                            if (searchText.isNotEmpty()) searchTracksByText(accessToken, searchText)
                            else emptyList()
                    }
                },
                label = { Text(text = "What song/artist do you want to search?") },
                placeholder = { Text(text = "Search...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Column(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(6.dp))
                        .fillMaxHeight(0.9f)
                        .background(color = Color(0xFF404040))
                ) {
                    LazyColumn {
                        items(items = tracks, itemContent = { track ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = {
                                        trackId = track.id
                                    })
                                    .background(
                                        color = if (trackId == track.id) Color(
                                            0xFF303030
                                        ) else Color.Transparent
                                    )
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
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onSuggestTrack(trackId, user.id)
                }, modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(100.dp))
            ) {
                Text(
                    text = "Confirm suggest",
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

private suspend fun searchTracksByText(accessToken: String, searchText: String): List<Track> {
    val response = spotifyService.searchTracks("Bearer $accessToken", query = searchText)

    return response.tracks.items.map { it ->
        val artists: String = it.artists.joinToString(separator = ", ") { it.name }
        Track(
            id = it.id,
            name = it.name,
            artists = artists,
            imageUri = it.album.images.elementAt(0).url,
        )
    }
}



