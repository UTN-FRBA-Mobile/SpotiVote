package com.example.spotivote.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotivote.ui.screens.*
import com.example.spotivote.ui.theme.SpotivoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotivoteTheme {
                App()
            }
        }
    }
}

// Root screen with navigation
@Composable
fun App() {
    val navController = rememberNavController()
    var roomName by remember { mutableStateOf("X") }
    var accessToken by remember { mutableStateOf("") }
    val activity = LocalContext.current as Activity

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            LoginScreen(
                activity,
                onLogin = { token ->
                    run {
                        accessToken = token
                        navController.navigate("room")
                    }
                }
            )
        }
        composable("room") {
            CreateRoomScreen(accessToken,
                onCreateRoom = {
                    run {
                        navController.navigate("room-by-id")
                    }
                }
            )
        }
        composable("room-by-id") {
            RoomByIdScreen(accessToken, roomName,
                onGoToSuggestTrack = {
                    run {
                        navController.navigate("suggest-track")
                    }
                }
            )
        }
        composable("suggest-track") {
            SuggestTrackScreen(accessToken,
                onSuggestTrack = {
                    run {
                        navController.navigate("search")
                    }
                }
            )
        }
        composable("search") {
            SearchScreen(accessToken,
                onSearchTrack = {
                    run {
                        navController.navigate("")
                    }
                }
            )
        }
    }
}
