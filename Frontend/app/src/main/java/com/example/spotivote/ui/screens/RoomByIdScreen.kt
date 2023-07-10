package com.example.spotivote.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spotivote.model.User
import com.example.spotivote.service.Callbacks
import com.example.spotivote.service.JoinRoomRequest
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
    accessToken: String,
    user: User,
    roomId: String,
    onGoToSuggestTrack: () -> Unit,
    onGoToQrCodeGenerator: () -> Unit
) {
    val context = LocalContext.current
    var room by remember { mutableStateOf<RoomResponse?>(null) }
    val deviceToken = MyPreferences.getFirebaseToken(context)

    LaunchedEffect(roomId) {
        // Utilizar el roomId para obtener los datos de la sala
        try {
            val fetchedRoomConfig =
                localService.joinRoom(roomId, JoinRoomRequest(user.id, accessToken))
            room = fetchedRoomConfig
        } catch (e: Exception) {
            Log.e("Backend Error", "Local service backend error", e)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    // refresh
    suspend fun refreshRoom() {
        try {
            val fetchedRoomConfig = localService.getRoom(roomId)
            room = fetchedRoomConfig
        } catch (e: Exception) {
            Log.e("Backend Error", "Local service backend error", e)
        }
    }

    val client = OkHttpClient()
    val socketUrl = "ws://192.168.0.3:8055"
    val request: Request = Request.Builder().url(socketUrl).build()

    val listener = WebSocketListener(Callbacks(onRefetch = {
        coroutineScope.launch { refreshRoom() }
    }))
    client.newWebSocket(request, listener)

    fun sendInvitations() {
        coroutineScope.launch {
            try {
                val tokensResponse = localService.getAllDeviceTokens()
                var isSuccessful = false
                tokensResponse.map {
                    if (deviceToken != it.deviceToken)
                        isSuccessful = sendNotificationToUser(
                            it.deviceToken,
                            "You are invited to the room ${room!!.name}",
                            room!!._id
                        )
                }
                Toast
                    .makeText(
                        context,
                        "The invitations has${if (isSuccessful) "n't" else ""} been sent!",
                        Toast.LENGTH_LONG
                    )
                    .show()
            } catch (e: Exception) {
                Log.e("Backend Error", "Local service backend error", e)
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colors.background
    ) {
        if (room != null) {
            val userPoints = room?.users?.find { it.id == user.id }?.points ?: 0
            val canSuggest = userPoints.toInt() >= 3
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                NavBar(user = user, userPoints = userPoints)
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

                    VoteSection(tracks = room!!.candidates.map { candidate ->
                        TrackInPoll(
                            track = TrackInPollTrack(
                                candidate.track.id,
                                candidate.track.name,
                                candidate.track.artists.joinToString(", ") { it.name },
                                candidate.track.album.images[0].url,
                            ),
                            votes = candidate.votes,
                        )
                    }, user, onVote = { trackId ->
                        coroutineScope.launch {
                            try {
                                room = localService.vote(
                                    roomId, VoteRequest(trackId, user.id)
                                )
                            } catch (e: Exception) {
                                Log.e("Backend Error", "Local service backend error", e)
                            }
                        }
                    })

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onGoToSuggestTrack() },
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        enabled = canSuggest
                    ) {
                        Text(
                            text = "Suggest Track",
                            style = MaterialTheme.typography.button,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Send invitations",
                        style = MaterialTheme.typography.button,
                        color = Color.Green,
                        modifier = Modifier
                            .clickable {
                                sendInvitations()
                            }
                            .padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Generate QR Code",
                        style = MaterialTheme.typography.button,
                        color = Color.Green,
                        modifier = Modifier
                            .clickable {
                                onGoToQrCodeGenerator()
                            }
                            .padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}
