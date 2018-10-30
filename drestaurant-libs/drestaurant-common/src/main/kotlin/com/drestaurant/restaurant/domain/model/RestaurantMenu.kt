package com.drestaurant.restaurant.domain.model

import javax.validation.Valid
import javax.validation.constraints.NotEmpty

/**
 * Restaurant menu holds a list of [MenuItem]s. It can have multiple versions
 */
data class RestaurantMenu(@field:NotEmpty @field:Valid val menuItems: MutableList<MenuItem>, val menuVersion: String)
