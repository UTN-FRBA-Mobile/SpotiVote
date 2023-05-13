package com.example.spotivote.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

private fun launchSuggestTrack(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
    //val intent =
    //launcher.launch(intent)
}

@Composable
fun SuggestTrackScreen(accessToken: String, onSuggestTrack: () -> Unit) {
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }

    LaunchedEffect(Unit) {
        val response = spotifyService.getUserTopItems("tracks", 10, "Bearer $accessToken")

        tracks = response.items.map { it ->
            val artists: String = it.artists.joinToString(separator = ", ") { it.name }
            Track(
                id = it.id,
                name = it.name,
                artists = artists,
                imageUri = it.album.images.elementAt(0).url,
            )

        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
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
                    modifier = Modifier
                        .padding(top = 12.dp)
                ) {
                    Text(text = "Your most listened songs", style = MaterialTheme.typography.h2)

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .fillMaxHeight(0.8f)
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

                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                            .background(color = Color(0xFF404040))
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable { onSuggestTrack() }
                    ) {
                        Text(
                            text = "Search another",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {},
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
