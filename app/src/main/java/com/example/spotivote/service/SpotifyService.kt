package com.example.spotivote.service

import com.example.spotivote.service.dto.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

val okHttpClient = OkHttpClient
    .Builder()
    .readTimeout(10, TimeUnit.SECONDS)
    .build()

val retrofit = Retrofit.Builder().baseUrl("https://api.spotify.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
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
    suspend fun getAvailableDevices(
        @Header("Authorization") authHeader: String
    ): AvailableDevicesResponse

    @GET("v1/me/player/currently-playing")
    suspend fun getCurrentlyPlaying(
        @Header("Authorization") authHeader: String
    ): Response<CurrentlyPlayingResponse>


    @GET("v1/playlists/{playlist_id}/tracks")
    suspend fun getTracksByPlaylistId(
        @Path("playlist_id") playlistId: String,
        @Header("Authorization") authHeader: String
    ): Response<PlaylistTracksResponse>

    @GET("v1/users/{user_id}")
    suspend fun getUserById(
        @Path("user_id") userId: String,
        @Header("Authorization") authHeader: String
    ): UserResponse

    @GET("v1/me/top/{type}")
    suspend fun getUserTopItems(
        @Path("type") type: String = "tracks",
        @Query("limit") limit: Int,
        @Header("Authorization") authHeader: String
    ): UserTopItemsResponse

    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") authorization: String,
        @Query("type") type: String = "track,artist",
        @Query("q") query: String
    ): SearchResultResponse
}

