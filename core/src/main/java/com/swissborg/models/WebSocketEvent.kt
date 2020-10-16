package com.swissborg.models

import java.nio.ByteBuffer

sealed class WebSocketEvent

object WebSocketNotConnected : WebSocketEvent()

sealed class InitializedWebSocketEvent: WebSocketEvent() {
    abstract val webSocket: WebSocket
}

data class WebSocketConnecting(override val webSocket: WebSocket) : InitializedWebSocketEvent()

sealed class ConnectionEstablishedWebSocketEvent : InitializedWebSocketEvent()
data class WebSocketConnected(override val webSocket: WebSocket) : ConnectionEstablishedWebSocketEvent()
data class WebSocketTextReceived(override val webSocket: WebSocket, val text: String) : ConnectionEstablishedWebSocketEvent()
data class WebSocketBytesReceived(override val webSocket: WebSocket, val bytes: ByteBuffer) : ConnectionEstablishedWebSocketEvent()
