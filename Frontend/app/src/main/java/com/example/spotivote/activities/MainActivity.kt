package com.example.spotivote.activities

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotivote.model.User
import com.example.spotivote.service.DeviceTokenRequest
import com.example.spotivote.service.firebase.*
import com.example.spotivote.service.localService
import com.example.spotivote.service.spotifyService
import com.example.spotivote.ui.screens.*
import com.example.spotivote.ui.theme.SpotivoteTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

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
}

// Root screen with navigation
@Composable
fun App() {
    val navController = rememberNavController()
    val activity = LocalContext.current as Activity
    var accessToken by remember { mutableStateOf("") }
    var roomConfig by remember {
        mutableStateOf(RoomConfig("", "", "", activity))
    }
    var user by remember { mutableStateOf(User("", "", "")) }

    suspend fun getUser() {
        val response = spotifyService.getMe("Bearer $accessToken")
        user = User(response.id, response.display_name, response.images[0].url)
    }

    suspend fun registerToken(context: Context, userId: String) {
        var token = ""
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            token = task.result
            Log.d(ContentValues.TAG, "FCM Registration token: $token")

            MyPreferences.setFirebaseToken(context, token)
        }
        if (token.isNotEmpty()) {
            val reqDeviceToken = DeviceTokenRequest(token, userId)
            localService.postDeviceToken(reqDeviceToken)
        }
    }

    LaunchedEffect(accessToken) {
        if (accessToken != "") getUser()
        registerToken(activity, user.id)
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