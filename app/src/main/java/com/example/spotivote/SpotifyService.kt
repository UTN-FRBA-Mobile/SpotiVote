package com.example.spotivote

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
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
    ): PlaylistTracksResponse

    @GET("v1/users/{user_id}")
    suspend fun getUserById(
        @Path("user_id") userId: String,
        @Header("Authorization") authHeader: String
    ): UserResponse
}

data class UserResponse(
    val display_name: String,
    val images: List<Image>
)

data class PlaylistResponse(
    val items: List<PlaylistItem>
)

data class PlaylistItem(
    val name: String,
    val href: String,
    val tracks: PlaylistItemTrack,
    val images: List<Image>
)

data class Image(val url: String)

data class PlaylistItemTrack(
    val total: Int,
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

data class CurrentlyPlayingResponse(
    val context: CurrentlyPlayingContext,
    val item: TrackItem,
)

data class CurrentlyPlayingContext(val href: String)
data class TrackItem(val album: TrackItemAlbum, val name: String, val id: String)
data class TrackItemAlbum(val artists: List<TrackItemAlbumArtist>, val images: List<Image>)
data class TrackItemAlbumArtist(val name: String)

data class PlaylistTracksResponse(
    val items: List<PlaylistTracksItem>,
)

data class PlaylistTracksItem(val added_by: AddedBy, val track: TrackItem)
data class AddedBy(val id: String)
