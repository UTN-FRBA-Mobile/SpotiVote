package com.example.spotivote.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spotivote.model.Device
import com.example.spotivote.model.DeviceType
import com.example.spotivote.model.User
import com.example.spotivote.service.dto.PlaylistItem
import com.example.spotivote.service.spotifyService
import com.example.spotivote.ui.components.DeviceItem
import com.example.spotivote.ui.components.NavBar
import com.example.spotivote.ui.components.PlaylistRowItem
import java.util.Locale

fun mapToDeviceType(type: String): DeviceType {
    return when (type.lowercase(Locale.getDefault())) {
        "smartphone" -> DeviceType.SMARTPHONE
        "computer" -> DeviceType.COMPUTER
        "tv" -> DeviceType.TV
        "speaker" -> DeviceType.SPEAKER
        else -> DeviceType.SMARTPHONE
    }
}

fun deviceTypeName(type: DeviceType): String {
    return when (type) {
        DeviceType.SMARTPHONE -> "Smartphone"
        DeviceType.COMPUTER -> "Computer"
        DeviceType.TV -> "TV"
        DeviceType.SPEAKER -> "Speaker"
    }
}

fun deviceTypeIcon(type: DeviceType): String {
    return when (type) {
        DeviceType.SMARTPHONE -> "📱"
        DeviceType.COMPUTER -> "💻"
        DeviceType.TV -> "📺"
        DeviceType.SPEAKER -> "🔊"
    }
}

data class RoomConfig(
    val name: String,
    val playlistId: String,
    val device: String,
    val context: Context,
)

@Composable
fun CreateRoomScreen(
    accessToken: String, user: User, onCreateRoom: (roomConfig: RoomConfig) -> Unit
) {
    var playlists by remember { mutableStateOf<List<PlaylistItem>>(emptyList()) }
    var devices by remember { mutableStateOf<List<Device>>(emptyList()) }

    var name by remember { mutableStateOf("Test") }
    var playlistId by remember { mutableStateOf("") }
    var device by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val response = spotifyService.getMyPlaylists("Bearer $accessToken")
        playlists = response.items
    }

    LaunchedEffect(Unit) {
        val response = spotifyService.getAvailableDevices("Bearer $accessToken")
        devices = response.devices.map { sDevice ->
            Device(sDevice.name, mapToDeviceType(sDevice.type), sDevice.id)
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
                    .fillMaxHeight()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Create Room",
                    style = MaterialTheme.typography.h1,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "Room name", style = MaterialTheme.typography.h2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Enter room name") },
                        placeholder = { Text(text = "Room X") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Text(text = "Your devices", style = MaterialTheme.typography.h2)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select your device through which the songs will be played",
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(6.dp))
                            .background(color = Color(0xFF404040))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            devices.forEach { sDevice ->
                                DeviceItem(device = sDevice, onClick = {
                                    device = sDevice.id
                                }, selected = device == sDevice.id)
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Text(text = "Your playlists", style = MaterialTheme.typography.h2)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select your playlist you want to play initially",
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(6.dp))
                            .height(400.dp)
                            .background(color = Color(0xFF404040))
                    ) {
                        LazyColumn {
                            items(items = playlists) { playlist ->
                                PlaylistRowItem(playlist = playlist, onClick = {
                                    playlistId = playlist.id
                                }, selected = playlistId == playlist.id)
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                if (playlistId != "" && device != "" && name != "") {
                    Button(
                        onClick = {
                            onCreateRoom(
                                RoomConfig(
                                    name = name,
                                    playlistId = playlistId,
                                    device = device,
                                    context = context
                                )
                            )
                        }, modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(100.dp))
                    ) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.button,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
