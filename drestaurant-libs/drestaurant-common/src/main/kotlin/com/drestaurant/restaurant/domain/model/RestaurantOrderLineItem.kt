package com.drestaurant.restaurant.domain.model

/**
 * Restaurant order line item is referencing the restaurant menu item that is ordered with specific quantity
 */
data class RestaurantOrderLineItem(val quantity: Int, val menuItemId: String, val name: String)
