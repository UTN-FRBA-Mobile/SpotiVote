package com.example.spotivote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

val okHttpClient = OkHttpClient.Builder().build()

val retrofit = Retrofit.Builder().baseUrl("https://api.spotify.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(
        okHttpClient
    )
    .build()

val spotifyService = retrofit.create(SpotifyService::class.java)

interface SpotifyService {

    @GET("/v1/me/playlists")
    suspend fun getMyPlaylists(
        @Header("Authorization") authorization: String,
    ): PlaylistResponse

    @GET("/v1/me")
    suspend fun getMe(
        @Header("Authorization") authorization: String,
    ): UserResponse

    @GET("v1/me/player/devices")
    suspend fun getAvailableDevices(@Header("Authorization") authHeader: String): AvailableDevicesResponse
}

data class UserResponse(
    val display_name: String
)

data class PlaylistResponse(
    val items: List<PlaylistItem>
)

data class PlaylistItem(
    val name: String, val href: String
)

data class AvailableDevicesResponse(
    val devices: List<SpotifyDevice>
)

data class SpotifyDevice(
    val id: String,
    val is_active: Boolean,
    val is_private_session: Boolean,
    val is_restricted: Boolean,
    val name: String,
    val type: String,
    val volume_percent: Int
)
