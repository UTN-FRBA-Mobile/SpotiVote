package com.example.spotivote.service

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

data class Callbacks(
    val thumbsUp: (String) -> Unit,
    val thumbsDown: (String) -> Unit,
)

class WebSocketListener(private val callbacks: Callbacks) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        callbacks.thumbsUp(text)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
    }
}