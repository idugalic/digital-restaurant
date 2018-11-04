package com.drestaurant.query.api.model

import java.io.Serializable
import java.math.BigDecimal

data class RestaurantModel(val id: String, val aggregateVersion: Long, val name: String, val menu: RestaurantMenuModel, val orders: List<RestaurantOrderModel>) : Serializable

data class RestaurantMenuModel(val menuItems: List<MenuItemModel>, val menuVersion: String) : Serializable

data class MenuItemModel(val menuId: String, val name: String, val price: BigDecimal) : Serializable