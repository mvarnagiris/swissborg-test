package com.swissborg.core.ticker

import com.swissborg.core.network.WebSocketActionsService
import com.swissborg.extensions.mapNotNull
import com.swissborg.models.SubscribeToTickerMessage
import com.swissborg.models.Ticker
import com.swissborg.models.WebSocketEvent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class TickerService(private val webSocketActionsService: WebSocketActionsService) {
    protected abstract val subscribeMessage: SubscribeToTickerMessage

    val ticker: Flowable<Ticker> by lazy { webSocketActionsService.subscribe(subscribeMessage).subscribeOn(Schedulers.io()).mapNotNull { map(it) } }

    protected abstract fun map(event: WebSocketEvent): Ticker?
}
