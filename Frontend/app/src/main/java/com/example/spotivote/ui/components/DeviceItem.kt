package com.example.spotivote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spotivote.model.Device
import com.example.spotivote.ui.screens.deviceTypeIcon
import com.example.spotivote.ui.screens.deviceTypeName

@Composable
fun DeviceItem(device: Device, onClick: () -> Unit, selected: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick)
            .background(
                color = if (selected) Color(0xFF303030) else Color.Transparent,
            )
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
                modifier = Modifier.align(Alignment.Center),
                color = if (selected) Color.Green else Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = device.name,
            style = MaterialTheme.typography.body2,
            color = if (selected) Color.Green else Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = deviceTypeName(device.type),
            style = MaterialTheme.typography.caption,
        )
    }
}