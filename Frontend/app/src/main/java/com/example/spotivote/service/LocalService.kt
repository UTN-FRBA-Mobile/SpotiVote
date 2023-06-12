package com.example.spotivote.service

import com.example.spotivote.service.dto.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

val localhOkHttpClient = OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build()

val localRetrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:4001/")
    .addConverterFactory(GsonConverterFactory.create()).client(localhOkHttpClient).build()

val localService = localRetrofit.create(LocalService::class.java)

interface LocalService {
    @GET("/playlist/{playlist_id}")
    suspend fun getTracksByPlaylistId(
        @Path("id") playlistId: String,
        @Header("access_token") authorization: String,
    ): PlaylistResponse
}