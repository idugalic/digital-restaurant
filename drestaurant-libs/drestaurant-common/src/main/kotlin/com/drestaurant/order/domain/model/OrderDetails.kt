package com.drestaurant.order.domain.model

import com.drestaurant.common.domain.model.Money

/**
 * Order 'details' extends the [OrderInfo] with the total amount
 */
data class OrderDetails(val orderInfo: OrderInfo, val orderTotal: Money) : OrderInfo(orderInfo.consumerId, orderInfo.restaurantId, orderInfo.lineItems)
