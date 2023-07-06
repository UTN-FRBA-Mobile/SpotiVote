package com.example.spotivote.service

import com.example.spotivote.service.dto.local.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

val localhOkHttpClient = OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build()

val localIpV4 = "192.168.0.11" // Set local IPv4
val localRetrofit = Retrofit.Builder().baseUrl("http://$localIpV4:4001/")
    .addConverterFactory(GsonConverterFactory.create()).client(localhOkHttpClient).build()

val localService = localRetrofit.create(LocalService::class.java)

data class DeviceTokenRequest(val deviceToken: String, val userId: String)

interface LocalService {
    @GET("playlist/{id}")
    suspend fun getTracksByPlaylistId(
        @Path("id") playlistId: String,
        @Header("access_token") authorization: String,
    ): PlaylistResponse

    @POST("playlist/deviceToken")
    suspend fun postDeviceToken(
        @Body body: DeviceTokenRequest,
    )
}