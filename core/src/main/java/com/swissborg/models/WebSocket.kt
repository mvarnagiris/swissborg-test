package com.swissborg.models

interface WebSocket {
    fun send(text: String)
    fun disconnect()
}
