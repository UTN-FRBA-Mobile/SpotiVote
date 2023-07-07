package com.example.spotivote.service

import com.example.spotivote.service.dto.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

val okHttpClient = OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build()

val retrofit = Retrofit.Builder().baseUrl("https://api.spotify.com/")
    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()

val spotifyService = retrofit.create(SpotifyService::class.java)

data class TransferPlaybackRequest(val deviceIds: List<String>, val play: Boolean = true)

data class TransferPlaybackResponse(val success: Boolean)

data class PlayRequest(
    val uris: List<String>
)

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
    ): PlaylistTracksResponse

    @GET("v1/users/{user_id}")
    suspend fun getUserById(
        @Path("user_id") userId: String,
        @Header("Authorization") authHeader: String
    ): UserResponse

    @GET("v1/me/player/")
    suspend fun getPlaybackState(
        @Header("Authorization") authHeader: String
    ): PlaybackStateResponse

    @PUT("/v1/me/player/play")
    suspend fun play(
        @Header("Authorization") authHeader: String,
        @Query("device_id") deviceId: String? = null,
        @Body body: PlayRequest
    ): Response<Unit>

    @PUT("/v1/me/player")
    suspend fun transferPlayback(
        @Header("Authorization") authorization: String, @Body body: TransferPlaybackRequest
    ): TransferPlaybackResponse

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

