package com.example.spotivote.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import com.example.spotivote.model.User
import com.example.spotivote.service.dto.local.RoomResponse
import com.example.spotivote.service.localService
import com.example.spotivote.ui.components.NavBar
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    user: User,
    onNavigateToCreateRoom: () -> Unit,
    onNavigateToQrCodeScanner: () -> Unit,
    onNavigateToRoom: (roomId: String) -> Unit,
) {
    var userRooms by remember { mutableStateOf<List<RoomResponse>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    suspend fun getUserRooms() {
        try {
            val rooms = localService.getRooms()
            userRooms = rooms.filter { room ->
                room.users.any { it.id == user.id }
            }
        } catch (e: Exception) {
            Log.e("Backend Error", "Local service backend error", e)
        }
    }

    LaunchedEffect(user) {
        getUserRooms()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            NavBar(user = user)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        vertical = 24.dp, horizontal = 12.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column {
                    Text(
                        text = "Your Rooms",
                        style = MaterialTheme.typography.h1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Here you can see the rooms you are in",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(6.dp))
                            .height(400.dp)
                            .background(color = Color(0xFF404040))
                    ) {
                        LazyColumn {
                            items(items = userRooms) { room ->
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { onNavigateToRoom(room._id) }
                                        .padding(horizontal = 12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 16.dp)
                                            .weight(1f),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(text = room.name)
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                }
                            }
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable {
                                            coroutineScope.launch {
                                                getUserRooms()
                                            }
                                        }
                                        .padding(horizontal = 12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 16.dp, bottom = 16.dp)
                                            .weight(1f), contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(text = "Refresh rooms")
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        onNavigateToCreateRoom()
                    }, modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(100.dp))
                ) {
                    Text(
                        text = "Create room",
                        style = MaterialTheme.typography.button,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Scan QR",
                    style = MaterialTheme.typography.button,
                    color = Color.Green,
                    modifier = Modifier
                        .clickable { onNavigateToQrCodeScanner() }
                        .padding(horizontal = 24.dp))
            }
        }
    }
}