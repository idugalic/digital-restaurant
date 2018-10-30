package com.drestaurant.restaurant.domain.model

/**
 * Restaurant order 'details' holds the list of [RestaurantOrderLineItem]s
 */
data class RestaurantOrderDetails(val lineItems: List<RestaurantOrderLineItem>)
