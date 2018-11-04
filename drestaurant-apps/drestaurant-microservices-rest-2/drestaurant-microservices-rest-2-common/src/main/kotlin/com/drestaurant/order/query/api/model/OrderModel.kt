package com.drestaurant.order.query.api.model

import com.drestaurant.order.domain.api.model.OrderState
import java.io.Serializable
import java.math.BigDecimal

data class OrderModel(val id: String, val aggregateVersion: Long, val lineItems: List<OrderItemModel>, val state: OrderState) : Serializable

data class OrderItemModel(val menuId: String, val name: String, val price: BigDecimal, val quantity: Int) : Serializable

