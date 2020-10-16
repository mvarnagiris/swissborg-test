package com.swissborg.extensions

import io.reactivex.rxjava3.core.Flowable

fun <T, R> Flowable<T>.mapNotNull(map: (T) -> R?): Flowable<R> = concatMap {
    val result = map(it)
    if (result == null) Flowable.empty()
    else Flowable.just(result)
}
