package com.example.spotivote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spotivote.model.User


@Composable
fun NavBar(user: User, userPoints: Number? = null) {
    TopAppBar(
        modifier = Modifier.background(color = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 6.dp, vertical = 6.dp
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.imageUri,
                contentDescription = "User Profile Image",
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .size(36.dp)
                    .fillMaxSize()
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = user.displayName)
            if (userPoints != null) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = userPoints.toString())
            }
        }
    }
}