package com.swissborgtest.network.bitfinex

import com.swissborg.core.network.WebSocketActionsService
import com.swissborg.core.orderBooks.OrderBooksService
import com.swissborg.models.Order
import com.swissborg.models.SubscribeToOrderBooksMessage
import com.swissborg.models.WebSocketEvent
import com.swissborg.models.WebSocketTextReceived
import com.swissborgtest.extensions.getInt
import com.swissborgtest.extensions.getString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

class BitfinexOrdersService(webSocketActionsService: WebSocketActionsService) : OrderBooksService(webSocketActionsService) {
    override val subscribeMessage: SubscribeToOrderBooksMessage get() = BitfinextSubscribeToOrdersMessage
    private var channelId: Int? = null
    private var length: Int? = null

    override fun map(event: WebSocketEvent): List<Order>? {
        if (event !is WebSocketTextReceived) return null

        val json = Json.decodeFromString<JsonElement>(event.text)

        if (json is JsonObject && json.getString("event") == "subscribed" && json.getString("channel") == "book") {
            channelId = json.getInt("chanId")
            length = json.getInt("len")
            return null
        }

        if (channelId != null && json is JsonArray && json[0].jsonPrimitive.intOrNull == channelId) {
            return when (json.size) {
                2 -> parseSnapshot(json)
                4 -> parseUpdate(json)
                else -> null
            }
        }

        return null
    }

    private fun parseSnapshot(json: JsonArray) = json[1].jsonArray.map { it.jsonArray.toOrder(indexOffset = 0) }
    private fun parseUpdate(json: JsonArray) = listOf(json.toOrder(indexOffset = 1))

    private fun JsonArray.toOrder(indexOffset: Int) = Order(
        price = get(indexOffset).jsonPrimitive.int,
        count = get(indexOffset + 1).jsonPrimitive.int,
        amount = get(indexOffset + 2).jsonPrimitive.float,
    )

    private object BitfinextSubscribeToOrdersMessage : SubscribeToOrderBooksMessage {
        override val text: String
            get() = Json.encodeToString(
                mapOf(
                    "event" to "subscribe",
                    "channel" to "book",
                    "pair" to "BTCUSD",
                    "freq" to "F1"
                )
            )
    }
}
