package com.example.spotivote.service.firebase

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.spotivote.service.DeviceTokenRequest
import com.example.spotivote.service.localService
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
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
    val notification: Map<String, Any>,
    @SerializedName("data")
    val data: Map<String, String>
)

interface FCMService {
    @Headers("Content-Type: application/json")
    @POST("fcm/send")
    suspend fun sendNotification(
        @Header("Authorization") authorization: String,
        @Body body: NotificationRequest
    ): Response<ResponseBody>
}

fun registerToken(context: Context) {
    Firebase.messaging.token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }
        val token = task.result
        Log.d(ContentValues.TAG, "FCM Registration token: $token")

        MyPreferences.setFirebaseToken(context, token)
    }
}

suspend fun registerTokenDB(deviceToken: String?, userId: String){
    Log.d(ContentValues.TAG, "DeviceToken: $deviceToken")
    if (!deviceToken.isNullOrEmpty()) {
        val reqDeviceToken = DeviceTokenRequest(deviceToken, userId)
        try {
            localService.postDeviceToken(reqDeviceToken)
        } catch (e: Exception) {
            Log.e("Backend Error", "Local service backend error", e)
        }
    }
}

fun sendNotificationToUser(deviceToken: String?, message: String, roomId: String): Boolean {
    // val hasNotificationPermission = MyPreferences.getNotificationPermission(context)
    var isSuccessful = false

    Log.d("Device Token", "deviceToken: $deviceToken")
    if (deviceToken != null) {
        val apiKey =
            "AAAAiQfH7c0:APA91bFAhyYr1gaE1mz-1O-qZOumIuXFpBeJ756yFK5CbcJC8SjHJguKTX2h4ZxsN3HU8a4XIv9DlyQmgP3VPnFkxPje445xTij2yUFnnsAeVfM43k5Ezpa9qMbhVoIVepOot44SOpKg" // TODO: obtenerla de un .env
        val notificationRequest = NotificationRequest(
            to = deviceToken,
            notification = mapOf("title" to "Room invitation!", "body" to message),
            data = mapOf("roomId" to roomId)
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = fcmService.sendNotification(
                    authorization = "key=$apiKey",
                    body = notificationRequest
                )
                Log.d(
                    "Push Notification",
                    "Response isSuccessful notification: ${response.isSuccessful}"
                )
                isSuccessful = response.isSuccessful
            } catch (e: IOException) {
                Log.e("Push Notification", "Error notification IOException: $e")
            }
        }
    }
    return isSuccessful
}