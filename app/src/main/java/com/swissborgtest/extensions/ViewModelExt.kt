package com.swissborgtest.extensions

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModels(crossinline factory: () -> VM): Lazy<VM> {
    @Suppress("UNCHECKED_CAST") val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return viewModels { viewModelFactory }
}