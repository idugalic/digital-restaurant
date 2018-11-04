package com.drestaurant.query.api.model

import com.drestaurant.restaurant.domain.api.model.RestaurantOrderState

data class RestaurantOrderModel(val id: String, val aggregateVersion: Long, val lineItems: List<RestaurantOrderItemModel>, val restaurant: RestaurantModel, val state: RestaurantOrderState)

data class RestaurantOrderItemModel(val menuId: String, val name: String, val quantity: Int)