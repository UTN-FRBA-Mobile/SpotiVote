package com.example.spotivote.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.util.*
import com.example.spotivote.model.Device
import com.example.spotivote.model.DeviceType
import com.example.spotivote.service.*
import com.example.spotivote.service.dto.*

fun mapToDeviceType(type: String): DeviceType {
    return when (type.lowercase(Locale.getDefault())) {
        "smartphone" -> DeviceType.SMARTPHONE
        "computer" -> DeviceType.COMPUTER
        "tv" -> DeviceType.TV
        "speaker" -> DeviceType.SPEAKER
        else -> DeviceType.SMARTPHONE
    }
}

// list of dummy devices
val dummyDevices = listOf(
    Device("Device 1", DeviceType.SMARTPHONE, "1"),
    Device("Device 2", DeviceType.COMPUTER, "2"),
    Device("Device 3", DeviceType.TV, "3"),
)

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

@Composable
fun DeviceItem(device: Device, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(shape = CircleShape)
                .background(color = Color(0xFFFAFAFA))
        ) {
            Text(
                text = deviceTypeIcon(device.type),
                style = MaterialTheme.typography.h2,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = device.name, style = MaterialTheme.typography.body2)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = deviceTypeName(device.type), style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun CreateRoomScreen(accessToken: String, onCreateRoom: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var playlists by remember { mutableStateOf<List<PlaylistItem>>(emptyList()) }
    var devices by remember { mutableStateOf<List<Device>>(emptyList()) }

    LaunchedEffect(Unit) {
        Log.d("CreateRoomScreen", "Access Token: $accessToken")
        val response = spotifyService.getMyPlaylists("Bearer $accessToken")
        playlists = response.items
    }

    LaunchedEffect(Unit) {
        val response = spotifyService.getAvailableDevices("Bearer $accessToken")
        devices = response.devices.map { sDevice ->
            Device(sDevice.name, mapToDeviceType(sDevice.type), sDevice.id)
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
                text = "Create Room",
                style = MaterialTheme.typography.h1,
                modifier = Modifier.fillMaxWidth()
            )

            Column {
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
                        placeholder = { Text(text = "Lorem, ipsum dolor sit amet") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
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
                        devices.forEach { device ->
                            DeviceItem(device = device, onClick = {})
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
                        .fillMaxHeight(0.55f)
                        .background(color = Color(0xFF404040))
                ) {
                    LazyColumn {
                        items(items = playlists, itemContent = { playlist ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .align(Alignment.CenterStart)
                            ) {
                                AsyncImage(
                                    model = if (playlist.tracks.total != 0) playlist.images.elementAt(
                                        0
                                    ).url
                                    else "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRwZ4mTuUvdD6l60AzmWTIZ341ALx1udRQn3zv5va8czuI5VNApMbGqiIJGSuoe1EhreQY&usqp=CAU", //TODO imagen local o de sv propio
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
                                        text = playlist.name,
                                        style = MaterialTheme.typography.body1
                                    )
                                    Text(
                                        text = "${playlist.tracks.total} song${(if (playlist.tracks.total != 1) "s" else "")}",
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
                onClick = { onCreateRoom() },
                modifier = Modifier
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
