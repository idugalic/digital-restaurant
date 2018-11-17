package com.drestaurant.restaurant.domain.api.model

import com.drestaurant.common.domain.api.model.Money
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

/**
 * Item in the restaurant menu
 */
data class MenuItem(val id: String, val name: String, val price: Money)

/**
 * Restaurant menu holds a list of [MenuItem]s. It can have multiple versions
 */
data class RestaurantMenu(@field:NotEmpty @field:Valid val menuItems: MutableList<MenuItem>, val menuVersion: String)

/**
 * Restaurant order 'details' holds the list of [RestaurantOrderLineItem]s
 */
data class RestaurantOrderDetails(val lineItems: List<RestaurantOrderLineItem>)

/**
 * Restaurant order line item is referencing the restaurant menu item that is ordered with specific quantity
 */
data class RestaurantOrderLineItem(val quantity: Int, val menuItemId: String, val name: String)

enum class RestaurantOrderState {
    CREATE_PENDING, CREATED, REJECTED, PREPARED, CANCEL_PENDING, CANCELLED
}

enum class RestaurantState {
    OPEN,
    CLOSED
}

data class RestaurantId(val identifier: String) {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
}

data class RestaurantOrderId(val identifier: String) {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
}