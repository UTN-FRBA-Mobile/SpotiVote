package com.example.spotivote.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.spotivote.service.firebase.MyPreferences
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


val CLIENT_ID = "a89b435943d8478b92992b6d235fe8ac"
val REDIRECT_URI = "com.example.spotivote://callback"

private fun launchSpotifyLogin(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
    val builder = AuthorizationRequest.Builder(
        CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI
    )
    builder.setScopes(
        arrayOf(
            "user-read-playback-state",
            "user-read-private",
            "user-read-email",
            "user-modify-playback-state",
            "user-read-currently-playing",
            "playlist-read-private",
            "user-top-read",
            "playlist-modify-public",
            "playlist-modify-private"
        )
    )
    val request = builder.build()

    val intent = AuthorizationClient.createLoginActivityIntent(
        activity, request
    )
    launcher.launch(intent)
}

@Composable
fun LoginScreen(activity: Activity, onLogin: (String) -> Unit) {
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            MyPreferences.setNotificationPermission(activity, isGranted)
        }
    )
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = AuthorizationClient.getResponse(result.resultCode, result.data)
        if (response.type == AuthorizationResponse.Type.TOKEN) {
            onLogin(response.accessToken)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),

            ) {
            Circle(Modifier.size(256.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("Spotivote", style = MaterialTheme.typography.h2)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Music voting for everyone", style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { launchSpotifyLogin(activity, launcher) },
                modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(100.dp))
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}


@Composable
fun Circle(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(256.dp)
                .clip(shape = CircleShape)
                .background(color = MaterialTheme.colors.primary),
            contentAlignment = Alignment.Center
        ) {}
    }
}