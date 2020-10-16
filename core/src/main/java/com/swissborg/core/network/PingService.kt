package com.swissborg.core.network

import io.reactivex.rxjava3.core.Single

interface PingService {
    fun ping(): Single<Unit>
}
