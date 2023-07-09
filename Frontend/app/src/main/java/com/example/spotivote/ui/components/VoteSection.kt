package com.example.spotivote.ui.components

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
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
import com.example.spotivote.service.Callbacks
import com.example.spotivote.service.WebSocketListener
import com.example.spotivote.service.localService
import com.example.spotivote.ui.screens.RoomConfig
import okhttp3.OkHttpClient
import okhttp3.Request


data class TrackInPollTrack(
    val id: String = "",
    val name: String = "",
    val artists: String = "",
    val imageUri: String? = ""
)
data class TrackInPoll(val track: TrackInPollTrack, var votes: Int)

@Composable
fun VoteSection(tracks: List<TrackInPoll>, onVote: (String) -> Unit) {
    var trackId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Text(text = "Vote next track", style = MaterialTheme.typography.h2)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(6.dp))
                .background(color = Color(0xFF404040))
                .height(400.dp)
        ) {
            LazyColumn {
                items(items = tracks, itemContent = { trackInPoll ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.CenterStart)
                            .clickable(onClick = {
                                // vote
                                trackId = trackInPoll.track.id
                                onVote(trackInPoll.track.id)
                            })
                            .background(
                                color = if (trackId == trackInPoll.track.id) Color(
                                    0xFF303030
                                ) else Color.Transparent
                            )
                    ) {
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

                            BoxWithConstraints(
                                modifier = Modifier.size(36.dp), // Adjust the size of the circle as needed
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            color = if (trackId == trackInPoll.track.id) Color.Green else Color(
                                                0xFF303030
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        modifier = Modifier.padding(8.dp),
                                        text = trackInPoll.votes.toString(),
                                        style = MaterialTheme.typography.body1,
                                        color = if (trackId == trackInPoll.track.id) Color.Black else Color.White
                                    )
                                }
                            }

                        }
                    }
                })
            }
        }
    }
}