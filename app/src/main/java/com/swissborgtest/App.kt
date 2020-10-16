package com.swissborgtest

import android.app.Application
import com.swissborgtest.di.FakeDi

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FakeDi.initialize(this)
    }
}