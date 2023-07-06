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
import com.example.spotivote.service.connectSpotifyAppRemote
import com.example.spotivote.service.firebase.*
import com.example.spotivote.service.spotifyAppRemote
import com.example.spotivote.service.spotifyService
import com.example.spotivote.ui.screens.*
import com.example.spotivote.ui.theme.SpotivoteTheme
import com.spotify.android.appremote.api.SpotifyAppRemote

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotivoteTheme {
                App()
            }
        }

        // reloadButton.setOnClickListener { setFirebaseTokenInView() }
        // subscribeButton.setOnClickListener { subscribeToTopic() }
    }

    override fun onStart() {
        super.onStart()
        connectSpotifyAppRemote(this)
        registerToken(this)
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}

// Root screen with navigation
@Composable
fun App() {
    val navController = rememberNavController()
    val activity = LocalContext.current as Activity
    var accessToken by remember { mutableStateOf("") }
    var deviceToken: String? by remember { mutableStateOf("") }
    var roomConfig by remember {
        mutableStateOf(RoomConfig("", "", "", activity))
    }
    var user by remember { mutableStateOf(User("", "", "")) }

    suspend fun getUser() {
        val response = spotifyService.getMe("Bearer $accessToken")
        user = User(response.id, response.display_name, response.images[0].url)
    }

    LaunchedEffect(accessToken) {
        if (accessToken != "") {
            getUser()
            deviceToken = MyPreferences.getFirebaseToken(activity)
            registerTokenDB(deviceToken, user.id)
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
            RoomByIdScreen(accessToken, user, roomConfig, onGoToSuggestTrack = {
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