package com.drestaurant.order.query.api

import com.drestaurant.order.domain.api.model.OrderId

data class FindOrderQuery(val orderId: OrderId)
