package com.swissborgtest.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.swissborgtest.R
import com.swissborgtest.di.FakeDi.mainViewModel
import com.swissborgtest.extensions.viewModels
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels { mainViewModel }
    private val adapter by lazy { OrderBooksAdapter() }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.ticker.observe(viewLifecycleOwner) {
            lastPriceTextView.text = "Last price: ${it.lastPrice}"
            volumeTextView.text = "Volume: ${it.volume}"
            lowTextView.text = "Low: ${it.low}"
            highTextView.text = "High: ${it.high}"
        }

        viewModel.orderBooks.observe(viewLifecycleOwner) {
            adapter.orders = it
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.connect()
    }

    override fun onPause() {
        super.onPause()
        viewModel.disconnect()
    }
}
