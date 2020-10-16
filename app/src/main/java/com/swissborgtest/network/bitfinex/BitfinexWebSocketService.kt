package com.swissborgtest.network.bitfinex

import com.swissborg.core.network.*
import com.swissborg.models.WebSocketEvent
import com.swissborg.models.WebSocketMessage
import com.swissborg.models.WebSocketRequest
import io.reactivex.rxjava3.core.Flowable

class BitfinexWebSocketService(webSocketFactory: WebSocketFactory, networkStateSource: NetworkStateSource) : WebSocketStateService, WebSocketActionsService {
    private val webSocketService = WebSocketService(WebSocketRequest("wss://api-pub.bitfinex.com/ws/"), webSocketFactory, networkStateSource)

    override fun connect() = webSocketService.connect()
    override fun disconnect() = webSocketService.disconnect()
    override fun send(message: WebSocketMessage) = webSocketService.send(message)
    override fun receive(): Flowable<WebSocketEvent> = webSocketService.receive()
}