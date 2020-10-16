package com.swissborgtest.di

import android.content.Context
import com.swissborg.core.network.PingingNetworkStateSource
import com.swissborgtest.network.AndroidNetworkStateSource
import com.swissborgtest.network.bitfinex.BitfinexOrdersService
import com.swissborgtest.network.bitfinex.BitfinexTickerService
import com.swissborgtest.network.bitfinex.BitfinexWebSocketService
import com.swissborgtest.network.okhttp.OkHttpGooglePingService
import com.swissborgtest.network.okhttp.OkHttpWebSocketFactory
import com.swissborgtest.ui.main.MainViewModel

object FakeDi {
    private var internalAppContext: Context? = null

    fun initialize(context: Context) {
        internalAppContext = context.applicationContext
    }

    private val appContext: Context get() = internalAppContext!!

    private val networkStateSource by lazy { PingingNetworkStateSource(AndroidNetworkStateSource(appContext), OkHttpGooglePingService()) }
    private val webSocketFactory by lazy { OkHttpWebSocketFactory() }
    private val webSocketService by lazy { BitfinexWebSocketService(webSocketFactory, networkStateSource) }
    private val webSocketStateService get() = webSocketService
    private val tickerService get() = BitfinexTickerService(webSocketService)
    private val ordersService get() = BitfinexOrdersService(webSocketService)
    val mainViewModel get() = MainViewModel(webSocketStateService, tickerService, ordersService)
}