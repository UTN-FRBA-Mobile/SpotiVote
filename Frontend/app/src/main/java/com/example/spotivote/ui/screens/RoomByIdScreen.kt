package com.example.spotivote.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spotivote.model.User
import com.example.spotivote.service.Callbacks
import com.example.spotivote.service.RoomResponse
import com.example.spotivote.service.VoteRequest
import com.example.spotivote.service.WebSocketListener
import com.example.spotivote.service.firebase.MyPreferences
import com.example.spotivote.service.firebase.sendNotificationToUser
import com.example.spotivote.service.localService
import com.example.spotivote.ui.components.CurrentlyPlaying
import com.example.spotivote.ui.components.NavBar
import com.example.spotivote.ui.components.TrackInPoll
import com.example.spotivote.ui.components.TrackInPollTrack
import com.example.spotivote.ui.components.VoteSection
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

@Composable
fun RoomByIdScreen(
    accessToken: String, user: User, roomId: String, onGoToSuggestTrack: () -> Unit
) {
    val context = LocalContext.current
    val firebaseToken = MyPreferences.getFirebaseToken(context)
    var room by remember { mutableStateOf<RoomResponse?>(null) }

    LaunchedEffect(roomId) {
        // Utilizar el roomId para obtener los datos de la sala
        val fetchedRoomConfig = localService.getRoom(roomId)
        room = fetchedRoomConfig
    }

    val coroutineScope = rememberCoroutineScope()

    // refresh
    suspend fun refreshRoom() {
        val fetchedRoomConfig = localService.getRoom(roomId)
        room = fetchedRoomConfig
    }

    val client = OkHttpClient()
    val socketUrl =
        "ws://192.168.0.3:8055"
    val request: Request = Request.Builder().url(socketUrl).build()

    val listener = WebSocketListener(
        Callbacks(
            onRefetch = {
                coroutineScope.launch { refreshRoom() }
            }
        )
    )
    val ws = client.newWebSocket(request, listener)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colors.background
    ) {
        if (room != null) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                NavBar(user = user)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Room ${room!!.name}",
                        style = MaterialTheme.typography.h1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CurrentlyPlaying(candidate = room!!.currentTrack)

                    Spacer(modifier = Modifier.height(24.dp))

                    VoteSection(
                        tracks = room!!.candidates.map { candidate ->
                            TrackInPoll(
                                track = TrackInPollTrack(
                                    candidate.track.id,
                                    candidate.track.name,
                                    candidate.track.artists.joinToString(", ") { it.name },
                                    candidate.track.album.images[0].url,
                                ),
                                votes = candidate.votes,
                            )
                        },
                        user,
                        onVote = { trackId ->
                            coroutineScope.launch {
                                room = localService.vote(
                                    roomId,
                                    VoteRequest(trackId, user.id)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            sendNotificationToUser(firebaseToken, "Hi SpotiVote User!!!", context)
                        }, modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(100.dp))
                    ) {
                        Text(
                            text = "Send notification",
                            style = MaterialTheme.typography.button,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}
