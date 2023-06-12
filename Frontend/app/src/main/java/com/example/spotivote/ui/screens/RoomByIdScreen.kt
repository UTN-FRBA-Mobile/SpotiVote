package com.example.spotivote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.spotivote.model.User
import com.example.spotivote.ui.components.CurrentlyPlaying
import com.example.spotivote.ui.components.NavBar
import com.example.spotivote.ui.components.VoteSection


@Composable
fun RoomByIdScreen(
    accessToken: String, user: User, roomConfig: RoomConfig, onGoToSuggestTrack: () -> Unit
) {
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
                    text = "Room ${roomConfig.name}",
                    style = MaterialTheme.typography.h1,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                CurrentlyPlaying(accessToken = accessToken)

                Spacer(modifier = Modifier.height(24.dp))

                VoteSection(roomConfig = roomConfig, accessToken = accessToken)

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
            }
        }
    }
}
