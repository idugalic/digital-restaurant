package com.drestaurant.courier.query.api.model

import java.io.Serializable

data class CourierModel(val id: String, val aggregateVersion: Long, val firstName: String, val lastName: String, val maxNumberOfActiveOrders: Int, val orders: List<CourierOrderModel>) : Serializable

