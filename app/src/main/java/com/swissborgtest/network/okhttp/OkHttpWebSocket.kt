package com.swissborgtest.network.okhttp

import com.swissborg.models.WebSocket

data class OkHttpWebSocket(private val webSocket: okhttp3.WebSocket) : WebSocket {
    override fun send(text: String) {
        webSocket.send(text)
    }

    override fun disconnect() {
        webSocket.cancel()
    }
}