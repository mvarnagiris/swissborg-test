package com.swissborg.core.network

import com.swissborg.models.*
import com.swissborg.models.NetworkState.NetworkConnected
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.BackpressureStrategy.LATEST
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class WebSocketService(
    private val webSocketRequest: WebSocketRequest,
    private val webSocketFactory: WebSocketFactory,
    networkStateSource: NetworkStateSource
) : WebSocketStateService, WebSocketActionsService {

    private val webSocketEventsSubject = BehaviorSubject.createDefault<Flowable<WebSocketEvent>>(Flowable.just(WebSocketNotConnected))
    private val disconnectTrigger = PublishSubject.create<Unit>()
    private val subscribeMessages = AtomicReference<Set<SubscribeMessage>>(emptySet())
    private val shouldBeConnected = AtomicBoolean(false)

    init {
        resendAllSubscribeMessagesWhenWebSocketConnects()
        reconnectWebSocketWhenNetworkIsAvailable(networkStateSource)
    }

    private fun resendAllSubscribeMessagesWhenWebSocketConnects() {
        webSocketEventsSubject
            .toFlowable(LATEST)
            .switchMap { it }
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it is WebSocketConnected) {
                    subscribeMessages.get().forEach { message -> it.webSocket.send(message.text) }
                }
            }
    }

    private fun reconnectWebSocketWhenNetworkIsAvailable(networkStateSource: NetworkStateSource) {
        networkStateSource.getNetworkState()
            .distinctUntilChanged()
            .filter { it == NetworkConnected }
            .subscribeOn(Schedulers.io())
            .subscribe { if (shouldBeConnected.get()) connect() }
    }

    override fun connect() {
        webSocketEventsSubject
            .toFlowable(LATEST)
            .flatMap { it }
            .firstOrError()
            .subscribeOn(Schedulers.single())
            .subscribe { currentWebSocketEvent ->
                if (currentWebSocketEvent !is WebSocketNotConnected) return@subscribe

                val webSocketEvents = webSocketFactory.connectNew(webSocketRequest).replay(1).autoConnect()

                shouldBeConnected.set(true)
                disconnectWebSocketOnTrigger(webSocketEvents)
                webSocketEventsSubject.onNext(webSocketEvents)
            }
    }

    private fun disconnectWebSocketOnTrigger(webSocketEvents: @NonNull Flowable<WebSocketEvent>) {
        disconnectTrigger
            .toFlowable(LATEST)
            .firstOrError()
            .flatMapMaybe { webSocketEvents.filter { it is InitializedWebSocketEvent }.map { (it as InitializedWebSocketEvent).webSocket }.firstElement() }
            .subscribeOn(Schedulers.io())
            .subscribe { it.disconnect() }
    }

    override fun receive(): Flowable<WebSocketEvent> = webSocketEventsSubject
        .toFlowable(LATEST)
        .switchMap { it }

    override fun send(message: WebSocketMessage) {
        webSocketEventsSubject
            .toFlowable(LATEST)
            .switchMap { it }
            .doOnNext {
                if (it is InitializedWebSocketEvent) {
                    it.webSocket.send(message.text)

                    if (message is SubscribeMessage) {
                        subscribeMessages.updateAndGet { messages -> messages + message }
                    }
                }
            }
            .takeUntil { it is InitializedWebSocketEvent }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun disconnect() {
        shouldBeConnected.set(false)
        webSocketEventsSubject.onNext(Flowable.just(WebSocketNotConnected))
        disconnectTrigger.onNext(Unit)
    }
}
