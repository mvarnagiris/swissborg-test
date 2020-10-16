package com.swissborgtest.network.bitfinex

import com.swissborg.core.network.WebSocketActionsService
import com.swissborg.core.ticker.TickerService
import com.swissborg.models.SubscribeToTickerMessage
import com.swissborg.models.WebSocketTextReceived
import com.swissborg.models.Ticker
import com.swissborg.models.WebSocketEvent
import com.swissborgtest.extensions.getInt
import com.swissborgtest.extensions.getString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

class BitfinexTickerService(webSocketActionsService: WebSocketActionsService) : TickerService(webSocketActionsService) {
    override val subscribeMessage: SubscribeToTickerMessage get() = BitfinextSubscribeToTickerMessage
    private var channelId: Int? = null

    override fun map(event: WebSocketEvent): Ticker? {
        if (event !is WebSocketTextReceived) return null

        val json = Json.decodeFromString<JsonElement>(event.text)

        if (json is JsonObject && json.getString("event") == "subscribed" && json.getString("channel") == "ticker") {
            channelId = json.getInt("chanId")
            return null
        }

        if (channelId != null && json is JsonArray && json.size == 11 && json[0].jsonPrimitive.intOrNull == channelId) {
            return Ticker(
                lastPrice = json[7].jsonPrimitive.float,
                volume = json[8].jsonPrimitive.float,
                high = json[9].jsonPrimitive.float,
                low = json[10].jsonPrimitive.float,
            )
        }

        return null
    }

    private object BitfinextSubscribeToTickerMessage : SubscribeToTickerMessage {
        override val text: String
            get() = Json.encodeToString(
                mapOf(
                    "event" to "subscribe",
                    "channel" to "ticker",
                    "pair" to "BTCUSD",
                )
            )
    }
}