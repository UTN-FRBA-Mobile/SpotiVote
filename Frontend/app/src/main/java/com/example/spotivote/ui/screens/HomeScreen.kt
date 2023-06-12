package com.example.spotivote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.spotivote.ui.components.NavBar


@Composable
fun HomeScreen(
    user: User,
    onNavigateToCreateRoom: () -> Unit,
    onNavigateToJoinRoom: () -> Unit,
) {
    // Crear sala o unirse a una
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            NavBar(user = user)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        // ir a crear sala
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

                // TODO: poner estilos de botón secundario
                Button(
                    onClick = {
                        // ir a scannear QR
                        onNavigateToJoinRoom()
                    }, modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(100.dp))
                ) {
                    Text(
                        text = "Scann QR",
                        style = MaterialTheme.typography.button,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}