package com.example.spotivote.ui.components

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.spotivote.model.Track
import com.example.spotivote.model.User
import com.example.spotivote.service.localService
import com.example.spotivote.service.spotifyAppRemote
import com.example.spotivote.service.spotifyService
import com.example.spotivote.ui.screens.RoomConfig


@Composable
fun CurrentlyPlaying(
    roomConfig: RoomConfig,
    accessToken: String,
) {
    var track: Track? by remember { mutableStateOf(null) }
    var user: User? by remember { mutableStateOf(null) }
    var isPaused by remember { mutableStateOf(false) }
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(accessToken) {
        spotifyAppRemote?.let {
            it.playerApi.subscribeToPlayerState().setEventCallback { playerState ->
                val playerTrack = playerState.track
                isPaused = playerState.isPaused

                it.imagesApi.getImage(playerTrack.imageUri).setResultCallback { bitmap ->
                    imageBitmap.value = bitmap
                }

                if (playerTrack != null) track = Track(
                    id = playerTrack.uri.split(':')[2],
                    name = playerTrack.name,
                    artists = playerTrack.artist.name,
                    playlistId = roomConfig.playlistId,
                )
            }
        }
    }
    //val currentlyPlayingResponse = spotifyService.getCurrentlyPlaying("Bearer $accessToken")
    //Log.d(TAG, "id track: " + currentlyPlayingResponse.body()!!.item!!.id)
    /*
            val currentlyPlayingResponse = spotifyService.getCurrentlyPlaying("Bearer $accessToken")

            if (currentlyPlayingResponse.code() == 200) {
                val currentlyPlaying = currentlyPlayingResponse.body()!!
                val currentTrack = currentlyPlaying.item

                if (currentlyPlaying.is_playing && currentTrack != null) {
                    val artists = currentTrack.artists.joinToString(separator = ", ") { it.name }
                    track = Track(
                        id = currentTrack.id,
                        name = currentTrack.name,
                        artists = artists,
                        imageUri = currentTrack.album.images.elementAt(0).url,
                    )
                }
            } else {
                println("Error getting currently playing track")
                return@LaunchedEffect
            }
    */
    LaunchedEffect(accessToken, track) {
        if (track != null) {
            try {
                Log.e(TAG, "Local service backend error $track ${track!!.playlistId} ${track!!.id}")

                val songResponse =
                    localService.getSong(track!!.playlistId, track!!.id, "Bearer $accessToken")
                if (songResponse.song.addedById.isNotEmpty()) {
                    //val userResponse = spotifyService.getMe("Bearer $accessToken")
                    val userResponse =
                        spotifyService.getUserById(songResponse.song.addedById, "Bearer $accessToken")
                    user = User(
                        userResponse.id,
                        userResponse.display_name,
                        userResponse.images.elementAt(0).url
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Local service backend error", e)
            }
        }
    }

    return Column(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Playing now", style = MaterialTheme.typography.h2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(6.dp))
                    .background(color = Color(0xFF404040))
                    .padding(12.dp)
            ) {
                if (track != null) {
                    Row() {
                        Image(
                            painter = rememberAsyncImagePainter(imageBitmap.value),
                            contentDescription = "Track Image",
                            modifier = Modifier
                                .clip(RoundedCornerShape(2.dp))
                                .size(80.dp)
                                .fillMaxSize()
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = track!!.name, style = MaterialTheme.typography.body1
                            )
                            Text(
                                text = track!!.artists,
                                style = MaterialTheme.typography.body2,
                                color = Color.Gray
                            )
                            Text(text = if (isPaused) "Paused" else "")
                        }
                    }
                } else {
                    Text(
                        text = "No song is currently playing",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TODO: ajustar tamaño y agregar botones 👍 👎
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
                if (user != null) {
                    Row {
                        AsyncImage(
                            model = user!!.imageUri,
                            contentDescription = "User Image",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(36.dp)
                                .fillMaxSize()
                        )
                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )
                        Text(
                            text = user!!.displayName, style = MaterialTheme.typography.body1
                        )
                    }
                } else {
                    Text(
                        text = "No data",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
