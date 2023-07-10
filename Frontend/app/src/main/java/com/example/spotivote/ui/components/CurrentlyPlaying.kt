package com.example.spotivote.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.service.dto.local.Candidate


@Composable
fun CurrentlyPlaying(
    candidate: Candidate
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
                Row() {
                    AsyncImage(
                        model = candidate.track.album.images[0].url,
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
                            text = candidate.track.name, style = MaterialTheme.typography.body1
                        )
                        Text(
                            text = candidate.track.artists.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                Row {
                    AsyncImage(
                        model = candidate.addedBy.profileImage,
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
                        text = candidate.addedBy.displayName, style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}
