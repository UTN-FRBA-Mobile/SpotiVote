package com.example.spotivote.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QrCodeScannerScreen(onNavigateToRoom: (roomId: String) -> Unit, onGoBack: () -> Boolean) {
    var stringFromQrCode by remember { mutableStateOf("") }
    var isPopupVisible by remember { mutableStateOf(false) }
    var isScanningEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
            if (!granted) onGoBack()
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCamPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (hasCamPermission) {
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
                                    stringFromQrCode = barCodeOrQr
                                    Log.d("QrScanner", stringFromQrCode)
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
        }

        if (isPopupVisible) {
            AlertDialog(
                onDismissRequest = { isPopupVisible = false },
                title = { Text(text = "Room invitation") },
                text = { Text(text = stringFromQrCode) },
                confirmButton = {
                    Button(
                        onClick = {
                            isPopupVisible = false
                            onNavigateToRoom(stringFromQrCode.split("/")[3])
                            coroutineScope.launch {
                                delay(2000)
                                isScanningEnabled = true
                            }
                        }
                    ) {
                        Text(text = "Join Room")
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
