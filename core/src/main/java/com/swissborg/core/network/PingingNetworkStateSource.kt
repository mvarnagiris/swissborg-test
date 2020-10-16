package com.swissborg.core.network

import com.swissborg.models.NetworkState
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class PingingNetworkStateSource(
    private val networkStateSource: NetworkStateSource,
    private val pingService: PingService
) : NetworkStateSource {

    override fun getNetworkState(): Flowable<NetworkState> = networkStateSource.getNetworkState()
        .switchMapSingle { networkState ->
            if (networkState == NetworkState.NetworkConnected) {
                pingService.ping()
                    .subscribeOn(Schedulers.io())
                    .map { NetworkState.NetworkConnected }
                    .retryEvery100MillisecondsUntilPingIsSuccessfulOrNetworkDisconnects()
                    .onErrorReturnItem(NetworkState.NetworkNotConnected)
            } else {
                Single.just(networkState)
            }
        }
        .distinctUntilChanged()

    private fun Single<NetworkState>.retryEvery100MillisecondsUntilPingIsSuccessfulOrNetworkDisconnects() = retryWhen { errors ->
        errors
            .switchMapSingle { networkStateSource.getNetworkState().firstOrError() }
            .switchMapSingle {
                if (it == NetworkState.NetworkConnected) Single.timer(100, TimeUnit.MILLISECONDS)
                else Single.just(NetworkState.NetworkNotConnected)
            }
    }
}
