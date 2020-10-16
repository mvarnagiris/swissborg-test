package com.swissborg.core.network

import com.swissborg.models.NetworkState
import io.reactivex.rxjava3.core.Flowable

interface NetworkStateSource {
    fun getNetworkState(): Flowable<NetworkState>
}
