package com.example.spotivote.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QrCodeScannerScreen(onNavigateToJoinRoom: () -> Unit) {
    var code by remember { mutableStateOf("") }
    var isPopupVisible by remember { mutableStateOf(false) }
    var isScanningEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val hasCamPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                isPopupVisible = false
            }
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCamPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }


    if (hasCamPermission) {
        Column(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    CompoundBarcodeView(context).apply {
                        val capture = CaptureManager(context as Activity, this)
                        capture.initializeFromIntent(context.intent, null)
                        this.setStatusText("")
                        capture.decode()
                        this.decodeContinuous { result ->
                            if (isScanningEnabled) {
                                result.text?.let { barCodeOrQr ->
                                    code = barCodeOrQr
                                    Log.d("QrScanner", code)
                                    isPopupVisible = true
                                    isScanningEnabled = false
                                }
                            }
                        }
                        this.resume()
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = code,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .clickable { onNavigateToJoinRoom() }
            )
        }

        if (isPopupVisible) {
            AlertDialog(
                onDismissRequest = { isPopupVisible = false },
                title = { Text(text = "Room invitation") },
                text = { Text(text = code) },
                confirmButton = {
                    Button(
                        onClick = {
                            isPopupVisible = false
                            onNavigateToJoinRoom()
                            coroutineScope.launch {
                                delay(2000)
                                isScanningEnabled = true
                            }
                        }
                    ) {
                        Text(text = "Go to Room X")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            isPopupVisible = false
                            coroutineScope.launch {
                                delay(2000)
                                isScanningEnabled = true
                            }
                        }
                    ) {
                        Text(text = "Close")
                    }
                }
            )
        }
    }
}
