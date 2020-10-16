package com.swissborg.models

import kotlin.math.max

data class Orders(
    private val bidOrders: List<Order> = emptyList(),
    private val askOrders: List<Order> = emptyList(),
) {
    val bidOrdersToAskOrders = 0.until(max(bidOrders.size, askOrders.size)).map { bidOrders.getOrNull(it) to askOrders.getOrNull(it) }

    fun withOrder(order: Order): Orders {
        val orders = when {
            order.amount > 0 -> bidOrders
            order.amount < 0 -> askOrders
            else -> return this
        }

        val orderIndex = orders.indexOfFirst { it.price == order.price }
        val newOrders = when {
            order.count > 0 && orderIndex < 0 -> orders.plus(order).sortedBy { it.price }
            order.count > 0 && orderIndex >= 0 -> orders.take(orderIndex) + order + orders.takeLast(orders.size - 1 - orderIndex)
            order.count == 0 && orderIndex >= 0 -> orders.minus(orders[orderIndex])
            else -> return this
        }

        return when {
            order.amount > 0 -> copy(bidOrders = newOrders)
            order.amount < 0 -> copy(askOrders = newOrders)
            else -> return this
        }
    }

}

data class Order(
    val price: Int,
    val amount: Float,
    val count: Int,
)