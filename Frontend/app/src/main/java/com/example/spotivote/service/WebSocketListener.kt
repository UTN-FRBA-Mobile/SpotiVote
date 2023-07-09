package com.example.spotivote.service

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

data class Callbacks(
    val onRefetch: () -> Unit,
)

class WebSocketListener(private val callbacks: Callbacks) : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.i("WebSocketListener", "onOpen")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.i("WebSocketListener", text)
        when (text) {
            "refetch" -> callbacks.onRefetch()
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
    }
}