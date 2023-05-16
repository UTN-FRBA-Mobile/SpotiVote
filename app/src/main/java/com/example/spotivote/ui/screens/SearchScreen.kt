package com.example.spotivote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.model.Track
import com.example.spotivote.service.spotifyService
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(accessToken: String, onSearchTrack: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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

            Column {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
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
                                            style = MaterialTheme.typography.body1
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
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {}, modifier = Modifier
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



