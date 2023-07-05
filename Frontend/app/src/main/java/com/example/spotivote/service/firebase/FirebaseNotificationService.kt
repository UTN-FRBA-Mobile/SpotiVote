package com.example.spotivote.service.firebase
import android.util.Log
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.IOException

val okHttpClient = OkHttpClient()

val retrofit = Retrofit.Builder()
    .baseUrl("https://fcm.googleapis.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

val fcmService = retrofit.create(FCMService::class.java)

data class NotificationRequest(
    @SerializedName("to")
    val to: String,
    @SerializedName("notification")
    val notification: Map<String, Any>
)

interface FCMService {
    @Headers("Content-Type: application/json")
    @POST("fcm/send")
    suspend fun sendNotification(
        @Header("Authorization") authorization: String,
        @Body body: NotificationRequest
    ): Response<ResponseBody>
}

fun sendNotificationToUser(deviceToken: String, message: String) {
    val apiKey = "AAAAiQfH7c0:APA91bFAhyYr1gaE1mz-1O-qZOumIuXFpBeJ756yFK5CbcJC8SjHJguKTX2h4ZxsN3HU8a4XIv9DlyQmgP3VPnFkxPje445xTij2yUFnnsAeVfM43k5Ezpa9qMbhVoIVepOot44SOpKg" // TODO: obtenerla de un .env

    val notificationRequest = NotificationRequest(
        to = deviceToken,
        notification = mapOf("title" to "Test notification...", "body" to message)
    )

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = fcmService.sendNotification(
                authorization = "key=$apiKey",
                body = notificationRequest
            )

            if (response.isSuccessful) {
                Log.d(TAG, "success response: " + response.message())
            } else {
                Log.e(TAG, "error response: " + response.message())
            }
        } catch (e: IOException) {
            Log.e(TAG, "error IOException: $e")
        }
    }
}
