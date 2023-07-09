package com.example.spotivote.service

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONException
import java.net.URISyntaxException

object SocketIOService {
    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            val opts = IO.Options()
            opts.transports = arrayOf(WebSocket.NAME)

            mSocket = IO.socket("http://10.0.2.2:8055", opts) // Url option: "http://10.0.2.2:8055"
            Log.i("WebSocketListener", "setSocket WebSocketListener")
        } catch (e: URISyntaxException) {
            Log.e("WebSocketListener", "WebSocketListener: $e")
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
        Log.i("WebSocketListener", "connect WebSocketListener")
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
        Log.i("WebSocketListener", "disconnect WebSocketListener")
    }

    fun setupNotifyListener() {
        try {
            mSocket.on(Socket.EVENT_DISCONNECT) { args ->
                Log.d("WebSocketListener", "=== notifylistener disconnected!")
            }.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.d("WebSocketListener", "=== notifylistener error: ${args[0]}")
            }.on("identity") { args ->
                try {
                    val data = args[0].toString()
//                    val songId = data.split("&")[0].split("=")[1]
//                    val likes = data.split("&")[1].split("=")[1].toInt()
//                    Log.d(
//                        "WebSocketListener",
//                        "\n=== Identity event received: songId=$songId, likes=$likes"
//                    )
                    Log.d("IdentityData", data)
                    /*
                    runOnUiThread {
                        // The is where you execute the actions after you receive the data (In Activity)
                    }
                    */
                } catch (e: JSONException) {
                    Log.d("WebSocketListener", "=== notifylistener error: ", e)
                }
            }.emit("eventName", "variable")
            // mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.d("WebSocketListener", "=== notifylistener error: ", e)
        }
    }
}