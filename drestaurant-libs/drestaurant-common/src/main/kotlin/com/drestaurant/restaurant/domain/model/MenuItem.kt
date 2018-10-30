package com.drestaurant.restaurant.domain.model

import com.drestaurant.common.domain.model.Money

/**
 * Item in the restaurant menu
 */
data class MenuItem(val id: String, val name: String, val price: Money)
