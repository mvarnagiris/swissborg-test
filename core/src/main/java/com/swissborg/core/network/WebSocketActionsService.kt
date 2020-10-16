package com.swissborg.core.network

import com.swissborg.models.SubscribeMessage
import com.swissborg.models.WebSocketMessage
import com.swissborg.models.WebSocketEvent
import io.reactivex.rxjava3.core.Flowable

interface WebSocketActionsService {
    fun send(message: WebSocketMessage)
    fun receive(): Flowable<WebSocketEvent>
    fun subscribe(subscribeMessage: SubscribeMessage) = receive().also { send(subscribeMessage) }
}
