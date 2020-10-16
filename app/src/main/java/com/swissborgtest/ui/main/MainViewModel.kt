package com.swissborgtest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.swissborg.core.network.WebSocketStateService
import com.swissborg.core.orderBooks.OrderBooksService
import com.swissborg.core.ticker.TickerService

class MainViewModel(
    private val webSocketStateService: WebSocketStateService,
    private val tickerService: TickerService,
    private val orderBooksService: OrderBooksService,
) : ViewModel() {

    val ticker by lazy { tickerService.ticker.toLiveData() }
    val orderBooks by lazy  { orderBooksService.orderBooks.toLiveData() }

    fun connect() {
        webSocketStateService.connect()
    }

    fun disconnect() {
        webSocketStateService.disconnect()
    }
}
