package com.example.spotivote

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*

enum class DeviceType {
    SMARTPHONE,
    COMPUTER,
    TV,
    SPEAKER
}

fun mapToDeviceType(type: String): DeviceType {
    return when (type.lowercase(Locale.getDefault())) {
        "smartphone" -> DeviceType.SMARTPHONE
        "computer" -> DeviceType.COMPUTER
        "tv" -> DeviceType.TV
        "speaker" -> DeviceType.SPEAKER
        else -> DeviceType.SMARTPHONE
    }
}

data class Device(val name: String, val type: DeviceType, val id: String)

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
fun CreateRoomScreen(accessToken: String) {
    var name by remember { mutableStateOf("") }
    var playlists by remember { mutableStateOf<List<PlaylistItem>>(emptyList()) }
    var devices by remember { mutableStateOf<List<Device>>(emptyList()) }

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

            Column() {
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

            Column() {
                Text(text = "Your devices", style = MaterialTheme.typography.h2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Reiciendis illum praesentium.",
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(6.dp))
                        .background(color = Color(0xFF404040))
                        .padding(8.dp)
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

            Column() {
                Text(text = "Your playlists", style = MaterialTheme.typography.h2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Reiciendis illum praesentium.",
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(6.dp))
                        .background(color = Color(0xFF404040))
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        playlists.forEach { playlist ->
                            Text(text = playlist.name)
                            Spacer(modifier = Modifier.width(16.dp))
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
                    text = "Next",
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }

}
