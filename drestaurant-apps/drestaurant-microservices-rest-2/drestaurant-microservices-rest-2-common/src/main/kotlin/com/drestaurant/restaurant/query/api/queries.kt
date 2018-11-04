package com.drestaurant.restaurant.query.api

import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId

data class FindRestaurantQuery(val restaurantId: RestaurantId)
data class FindRestaurantOrderQuery(val restaurantOrderId: RestaurantOrderId)
