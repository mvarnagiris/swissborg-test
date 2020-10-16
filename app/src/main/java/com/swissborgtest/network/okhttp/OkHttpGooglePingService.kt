package com.swissborgtest.network.okhttp

import com.swissborg.core.network.PingService
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class OkHttpGooglePingService : PingService {
    private val okHttpClient = OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).build()

    override fun ping(): Single<Unit> = Single.create<Unit> {
        val request = Request.Builder().url("https://www.google.com").head().build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) = it.onSuccess(Unit)
            override fun onFailure(call: Call, e: IOException) = it.onError(e)
        })
    }.subscribeOn(Schedulers.io())
}