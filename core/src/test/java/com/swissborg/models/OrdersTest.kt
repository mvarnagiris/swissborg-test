package com.swissborg.models

import org.junit.Test

class OrdersTest {
    @Test
    fun `when amount is more than 0 and count is more than 0 order will be added to bids and sorted by price`() {
        val orders = Orders()
        val orderSmallPrice = Order(1, 1f, 1)
        val orderBigPrice = Order(2, 1f, 1)

        val resultOrders = orders.withOrder(orderBigPrice).withOrder(orderSmallPrice)

        assert(resultOrders.bidOrdersToAskOrders == listOf(
            orderSmallPrice to null,
            orderBigPrice to null,
        ))
    }

    @Test
    fun `when amount is less than 0 and count is more than 0 order will be added to asks and sorted by price`() {
        val orders = Orders()
        val orderSmallPrice = Order(1, -1f, 1)
        val orderBigPrice = Order(2, -1f, 1)

        val resultOrders = orders.withOrder(orderBigPrice).withOrder(orderSmallPrice)

        assert(resultOrders.bidOrdersToAskOrders == listOf(
            null to orderSmallPrice,
            null to orderBigPrice,
        ))
    }

    @Test
    fun `when amount is more than 0 and count is more than 0 and order for that price already exists, it will be replaced in bids`() {
        val orders = Orders()
        val orderAmount1 = Order(1, 1f, 1)
        val orderAmount2 = Order(1, 2f, 1)

        val resultOrders = orders.withOrder(orderAmount1).withOrder(orderAmount2)

        assert(resultOrders.bidOrdersToAskOrders == listOf(
            orderAmount2 to null,
        ))
    }

    @Test
    fun `when amount is more less 0 and count is more than 0 and order for that price already exists, it will be replaced in asks`() {
        val orders = Orders()
        val orderAmount1 = Order(1, -1f, 1)
        val orderAmount2 = Order(1, -2f, 1)

        val resultOrders = orders.withOrder(orderAmount1).withOrder(orderAmount2)

        assert(resultOrders.bidOrdersToAskOrders == listOf(
            null to orderAmount2,
        ))
    }

    @Test
    fun `when count is 0 order will be removed`() {
        val orders = Orders()
        val bid = Order(1, 1f, 1)
        val ask = Order(1, -1f, 1)

        val resultOrders = orders
            .withOrder(bid)
            .withOrder(ask)
            .withOrder(bid.copy(count = 0))
            .withOrder(ask.copy(count = 0))

        assert(resultOrders.bidOrdersToAskOrders.isEmpty())
    }
}