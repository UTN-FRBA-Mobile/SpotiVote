package com.example.spotivote.activities

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotivote.model.User
import com.example.spotivote.service.AddCandidateRequest
import com.example.spotivote.service.CreateRoomRequest
import com.example.spotivote.service.connectSpotifyAppRemote
import com.example.spotivote.service.firebase.MyPreferences
import com.example.spotivote.service.firebase.registerToken
import com.example.spotivote.service.firebase.registerTokenDB
import com.example.spotivote.service.localService
import com.example.spotivote.service.spotifyAppRemote
import com.example.spotivote.service.spotifyService
import com.example.spotivote.ui.screens.CreateRoomScreen
import com.example.spotivote.ui.screens.HomeScreen
import com.example.spotivote.ui.screens.LoginScreen
import com.example.spotivote.ui.screens.QrCodeScannerScreen
import com.example.spotivote.ui.screens.RoomByIdScreen
import com.example.spotivote.ui.screens.SearchScreen
import com.example.spotivote.ui.screens.SuggestTrackScreen
import com.example.spotivote.ui.theme.SpotivoteTheme
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.launch


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
    var deviceToken: String? by remember { mutableStateOf("") }

    var accessToken by remember { mutableStateOf("") }
    var user by remember { mutableStateOf(User("", "", "")) }

    val coroutineScope = rememberCoroutineScope()

    suspend fun getUser() {
        val response = spotifyService.getMe("Bearer $accessToken")
        Log.d("MainActivity", "getUser: $response")
        if (response.images.isNotEmpty()) {
            user = User(response.id, response.display_name, response.images[0].url)
        } else {
            user = User(response.id, response.display_name, "")
        }
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
            }, onNavigateToQrCodeScanner = {
                run {
                    navController.navigate("qr-code-scanner")
                }
            }, onNavigateToRoom = {
                run {
                    navController.navigate("room-by-id/$it")
                }
            })
        }

        composable("qr-code-scanner") {
            QrCodeScannerScreen(onNavigateToJoinRoom = {
                run {
                    navController.navigate("room-by-id")
                }
            })
        }

        composable("create-room") {
            CreateRoomScreen(accessToken, user, onCreateRoom = {
                coroutineScope.launch {
                    val response = localService.createRoom(
                        CreateRoomRequest(
                            it.name,
                            it.device,
                            it.playlistId,
                            user.id,
                            accessToken
                        )
                    )
                    navController.navigate("room-by-id/${response._id}")
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

        composable("room-by-id/{roomId}") {
            val roomId = it.arguments?.getString("roomId") ?: ""
            RoomByIdScreen(accessToken, user, roomId, onGoToSuggestTrack = {
                run {
                    navController.navigate("suggest-track/$roomId")
                }
            })
        }

        composable("suggest-track/{roomId}") {
            val roomId = it.arguments?.getString("roomId") ?: ""
            SuggestTrackScreen(
                accessToken = accessToken,
                user = user,
                onSuggestTrack = { trackId, userId ->
                    coroutineScope.launch {
                        val response =
                            localService.addCandidate(roomId, AddCandidateRequest(trackId, userId))
                        navController.navigate("room-by-id/${roomId}")
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