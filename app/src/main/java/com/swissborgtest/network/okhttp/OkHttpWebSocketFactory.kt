package com.swissborgtest.network.okhttp

import com.swissborg.core.network.WebSocketFactory
import com.swissborg.models.*
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableEmitter
import okhttp3.*
import okhttp3.WebSocket
import okio.ByteString

class OkHttpWebSocketFactory : WebSocketFactory {
    private val client = OkHttpClient()

    override fun connectNew(webSocketRequest: WebSocketRequest): Flowable<WebSocketEvent> = Flowable.create({
        val request = Request.Builder()
            .url(webSocketRequest.url)
            .build()

        val webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                it.doIfNotCancelled(webSocket) {
                    onNext(WebSocketConnected(OkHttpWebSocket(webSocket)))
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                it.doIfNotCancelled(webSocket) {
                    onNext(WebSocketTextReceived(OkHttpWebSocket(webSocket), text))
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                it.doIfNotCancelled(webSocket) {
                    onNext(WebSocketBytesReceived(OkHttpWebSocket(webSocket), bytes.asByteBuffer()))
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                it.doIfNotCancelled(webSocket) {
                    onNext(WebSocketNotConnected)
                    onComplete()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                it.doIfNotCancelled(webSocket) {
                    onNext(WebSocketNotConnected)

                    // For simplicity assuming that web socket only fails because of network lost issue.
                    // this should be onError(t) and there should be additional re-connect logic down the stream
                    onComplete()
                }
            }
        })

        it.onNext(WebSocketConnecting(OkHttpWebSocket(webSocket)))
    }, BackpressureStrategy.BUFFER)

    private inline fun FlowableEmitter<WebSocketEvent>.doIfNotCancelled(webSocket: WebSocket, block: FlowableEmitter<WebSocketEvent>.(WebSocket) -> Unit) {
        if (isCancelled) {
            webSocket.cancel()
        } else {
            block(webSocket)
        }
    }
}