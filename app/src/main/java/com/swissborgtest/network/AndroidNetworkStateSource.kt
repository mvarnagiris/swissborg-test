package com.swissborgtest.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import androidx.core.content.getSystemService
import com.swissborg.core.network.NetworkStateSource
import com.swissborg.models.NetworkState
import com.swissborg.models.NetworkState.NetworkConnected
import com.swissborg.models.NetworkState.NetworkNotConnected
import io.reactivex.rxjava3.core.BackpressureStrategy.LATEST
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.io.Closeable

class AndroidNetworkStateSource(context: Context) : NetworkStateSource, Closeable {

    private val currentNetworkState = BehaviorSubject.createDefault(NetworkNotConnected)
    private val connectivityManager = context.getSystemService<ConnectivityManager>()
    private val networkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) = currentNetworkState.onNext(NetworkConnected)
        override fun onLost(network: Network) = currentNetworkState.onNext(NetworkNotConnected)
        override fun onUnavailable() = currentNetworkState.onNext(NetworkNotConnected)
    }

    init {
        if (connectivityManager != null) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            currentNetworkState.onNext(NetworkConnected)
        }
    }

    override fun getNetworkState(): Flowable<NetworkState> = currentNetworkState
        .toFlowable(LATEST)
        .distinctUntilChanged()

    override fun close() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }
}
