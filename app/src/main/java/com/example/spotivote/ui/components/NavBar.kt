package com.example.spotivote.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.model.User

@Composable
fun NavBar(user: User) {
    TopAppBar() {
        Row {
            AsyncImage(
                model = user.imageUri,
                contentDescription = "Playlist Image",
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .size(50.dp)
                    .fillMaxSize()
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = user.displayName)
        }
    }
}