package com.example.spotivote.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.spotivote.model.User
import com.example.spotivote.service.QrCodeGeneratorHandler
import com.example.spotivote.ui.components.NavBar


@Composable
fun QrCodeGeneratorScreen(user: User, roomId: String, onGoBack: () -> Boolean) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val stringToQrCode by remember { mutableStateOf("spotivote://room-by-id/$roomId") }

    fun setImageBitmapQrCode() {
        imageBitmap = QrCodeGeneratorHandler.generateQrCode(stringToQrCode)
    }
    setImageBitmapQrCode()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            NavBar(user = user)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Join Room",
                    style = MaterialTheme.typography.h1,
                    modifier = Modifier.fillMaxWidth()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "Scan the QR Code",
                        style = MaterialTheme.typography.h2
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(6.dp))
                            .background(color = Color(0xFF404040))
                            .padding(20.dp)
                            .aspectRatio(1f)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageBitmap),
                            contentDescription = "QR Code Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { onGoBack() },
                        modifier = Modifier
                            .height(48.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(100.dp)),
                    ) {
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.button,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
