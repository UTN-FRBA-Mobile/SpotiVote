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

interface LocalService {
    @GET("device-token")
    suspend fun getAllDeviceTokens(): List<DeviceTokenResponse>

    @POST("device-token")
    suspend fun postDeviceToken(
        @Body body: DeviceTokenRequest,
    )

    @GET("rooms")
    suspend fun getRooms(): List<RoomResponse>

    @POST("rooms")
    suspend fun createRoom(
        @Body body: CreateRoomRequest,
    ): RoomResponse

    @POST("rooms/{id}/join")
    suspend fun joinRoom(
        @Path("id") id: String,
        @Body body: JoinRoomRequest,
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