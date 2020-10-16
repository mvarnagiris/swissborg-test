package com.swissborgtest.ui.main

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swissborg.models.Order
import com.swissborg.models.Orders
import com.swissborgtest.R
import com.swissborgtest.extensions.inflate
import com.swissborgtest.ui.main.OrderBooksAdapter.OrderBookViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.order_item_view.view.*
import kotlin.math.absoluteValue

class OrderBooksAdapter : RecyclerView.Adapter<OrderBookViewHolder>() {
    var orders = Orders()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = orders.bidOrdersToAskOrders.size
    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = OrderBookViewHolder(parent.inflate(R.layout.order_item_view))
    override fun onBindViewHolder(holder: OrderBookViewHolder, position: Int) {
        val (bidOrder, askOrder) = orders.bidOrdersToAskOrders[position]
        holder.bind(bidOrder, askOrder)
    }

    class OrderBookViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(bidOrder: Order?, askOrder: Order?) {
            containerView.bidAmountTextView.text = bidOrder?.amount?.toString()
            containerView.bidPriceTextView.text = bidOrder?.price?.toString()
            containerView.askAmountTextView.text = askOrder?.amount?.absoluteValue?.toString()
            containerView.askPriceTextView.text = askOrder?.price?.toString()
        }
    }
}