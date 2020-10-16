package com.swissborg.core.orderBooks

import com.swissborg.core.network.WebSocketActionsService
import com.swissborg.models.Order
import com.swissborg.models.Orders
import com.swissborg.models.SubscribeToOrderBooksMessage
import com.swissborg.models.WebSocketEvent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class OrderBooksService(private val webSocketActionsService: WebSocketActionsService) {
    protected abstract val subscribeMessage: SubscribeToOrderBooksMessage

    val orderBooks: Flowable<Orders> by lazy {
        webSocketActionsService.subscribe(subscribeMessage)
            .subscribeOn(Schedulers.io())
            .scan(Orders()) { orders, event ->
                val orderList = map(event) ?: return@scan orders
                orderList.fold(orders) { newOrders, order -> newOrders.withOrder(order) }
            }
    }

    protected abstract fun map(event: WebSocketEvent): List<Order>?
}