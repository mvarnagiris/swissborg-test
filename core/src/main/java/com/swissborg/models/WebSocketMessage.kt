package com.swissborg.models

interface WebSocketMessage {
    val text: String
}

interface SubscribeMessage : WebSocketMessage
interface SubscribeToTickerMessage : SubscribeMessage
interface SubscribeToOrderBooksMessage : SubscribeMessage
