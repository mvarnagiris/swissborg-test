package com.swissborg.core.network

import com.swissborg.models.WebSocketRequest
import com.swissborg.models.WebSocketEvent
import io.reactivex.rxjava3.core.Flowable

interface WebSocketFactory {
    fun connectNew(webSocketRequest: WebSocketRequest): Flowable<WebSocketEvent>
}
