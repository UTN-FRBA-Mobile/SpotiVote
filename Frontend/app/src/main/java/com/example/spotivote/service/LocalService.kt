package com.example.spotivote.service

import com.example.spotivote.service.dto.local.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

val localhOkHttpClient = OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build()

val localIpV4 = "192.168.0.3" // Set local IPv4
val localRetrofit = Retrofit.Builder().baseUrl("http://$localIpV4:4001/")
    .addConverterFactory(GsonConverterFactory.create()).client(localhOkHttpClient).build()

val localService = localRetrofit.create(LocalService::class.java)

data class DeviceTokenRequest(val deviceToken: String, val userId: String)

data class CreateRoomRequest(
    val name: String,
    val deviceId: String,
    val basePlaylistId: String,
    val owner: String,
    val accessToken: String
)

data class AlbumImage(val url: String)
data class Artist(val name: String)
data class Album(val name: String, val images: List<AlbumImage>)
data class CandidateTrack(
    val id: String,
    val name: String,
    val album: Album,
    val artists: List<Artist>,
)

data class Candidate(
    val addedBy: String,
    val track: CandidateTrack,
    val votes: List<String>,
)

data class RoomResponse(
    val name: String,
    val owner: String,
    val playlistId: String,
    val deviceId: String,
    val _id: String,
    val basePlaylistId: String,
    val candidates: List<Candidate>,
    val currentTrack: Candidate
)

data class AddCandidateRequest(
    val trackId: String,
    val userId: String,
)

data class VoteRequest(
    val trackId: String,
    val userId: String,
)

interface LocalService {
    @GET("playlist/{id}")
    suspend fun getTracksByPlaylistId(
        @Path("id") playlistId: String,
        @Header("access_token") authorization: String,
    ): PlaylistResponse

    @GET("playlist/{playlistId}/song/{songId}/")
    suspend fun getSong(
        @Path("playlistId") playlistId: String,
        @Path("songId") songId: String,
        @Header("access_token") authorization: String,
    ): SongResponse

    @POST("playlist/deviceToken")
    suspend fun postDeviceToken(
        @Body body: DeviceTokenRequest,
    )

    @POST("rooms")
    suspend fun createRoom(
        @Body body: CreateRoomRequest,
    ): RoomResponse

    @GET("rooms/{id}")
    suspend fun getRoom(
        @Path("id") id: String,
    ): RoomResponse

    @POST("rooms/{id}/candidates")
    suspend fun addCandidate(
        @Path("id") id: String,
        @Body body: AddCandidateRequest,
    ): RoomResponse

    @PATCH("rooms/{id}/votes")
    suspend fun vote(
        @Path("id") id: String,
        @Body body: VoteRequest,
    ): RoomResponse
}