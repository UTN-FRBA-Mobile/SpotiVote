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
import com.example.spotivote.model.User
import com.example.spotivote.service.spotifyService
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
    val activity = LocalContext.current as Activity

    var accessToken by remember { mutableStateOf("") }
    var roomConfig by remember {
        mutableStateOf(
            RoomConfig("", "", "")
        )
    }

    var user by remember {
        mutableStateOf(
            User("", "")
        )
    }

    suspend fun getUser() {
        val response = spotifyService.getMe(
            "Bearer $accessToken"
        )
        user = User(
            response.display_name, response.images[0].url
        )
    }

    LaunchedEffect(accessToken) {
        if (accessToken != "") {
            getUser()
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(activity) { token ->
                run {
                    accessToken = token
                    navController.navigate("home")
                }
            }
        }

        composable("home") {
            HomeScreen(user, onNavigateToCreateRoom = {
                run {
                    navController.navigate("create-room")
                }
            }, onNavigateToJoinRoom = {
                run {
                    navController.navigate("join-room")
                }
            })
        }

        composable("create-room") {
            CreateRoomScreen(accessToken, user, onCreateRoom = {
                roomConfig = it
                run {
                    navController.navigate("room-by-id")
                }
            })
        }

//        composable("join-room") {
//            CreateRoomScreen(accessToken, onCreateRoom = {
//                roomConfig = it
//                run {
//                    navController.navigate("room-by-id")
//                }
//            })
//        }

        composable("room-by-id") {
            RoomByIdScreen(accessToken, roomConfig, onGoToSuggestTrack = {
                run {
                    navController.navigate("suggest-track")
                }
            })
        }
        composable("suggest-track") {
            SuggestTrackScreen(accessToken, onSuggestTrack = {
                run {
                    navController.navigate("search")
                }
            })
        }
        composable("search") {
            SearchScreen(accessToken, onSearchTrack = {
                run {
                    navController.navigate("")
                }
            })
        }
    }
}