package com.example.spotivote.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.spotivote.model.User
import com.example.spotivote.service.QrCodeGeneratorHandler
import com.example.spotivote.ui.components.NavBar


@Composable
fun QrCodeGeneratorScreen(user: User, roomId: String) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var stringToQrCode by remember { mutableStateOf("spotivote://room-by-id/$roomId") }

    fun setImageBitmapQrCode() {
        imageBitmap = QrCodeGeneratorHandler.generateQrCode(context, stringToQrCode)
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageBitmap),
                    contentDescription = "QR Code Image",
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .size(300.dp)
                        .fillMaxSize()
                )
                /*
                Spacer(modifier = Modifier.weight(0.7f))
                OutlinedTextField(
                    value = stringToQrCode,
                    onValueChange = { stringToQrCode = it },
                    label = { Text(text = "Text to generate QR") },
                    placeholder = { Text(text = "spotivote://room-by-id/{roomId}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { setImageBitmapQrCode() },
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    enabled = !stringToQrCode.isNullOrEmpty()
                ) {
                    Text(
                        text = "Generate QR Code",
                        style = MaterialTheme.typography.button,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
                 */
            }
        }
    }
}

