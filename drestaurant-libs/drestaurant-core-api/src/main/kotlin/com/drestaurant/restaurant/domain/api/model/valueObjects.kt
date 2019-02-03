package com.drestaurant.restaurant.domain.api.model

import com.drestaurant.common.domain.api.model.Money
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

/**
 * Item in the restaurant menu
 *
 * @property id identifier of a menu item
 * @property name name of the menu item
 * @property price price of the menu item
 */
data class MenuItem(val id: String, val name: String, val price: Money)

/**
 * Restaurant menu holds a list of [MenuItem]s. It can have multiple versions
 *
 * @property menuItems restaurant order menu items
 * @property menuVersion version of the menu
 */
data class RestaurantMenu(@field:NotEmpty @field:Valid val menuItems: MutableList<MenuItem>, val menuVersion: String)

/**
 * Restaurant order 'details' holds the list of [RestaurantOrderLineItem]s
 *
 * @property lineItems line items of the restaurant order
 */
data class RestaurantOrderDetails(val lineItems: List<RestaurantOrderLineItem>)

/**
 * Restaurant order line item is referencing the restaurant menu item that is ordered with specific quantity
 *
 * @property quantity quantity of the restaurant order line item
 * @property menuItemId reference to a menu item
 * @property name name of the menu item
 */
data class RestaurantOrderLineItem(val quantity: Int, val menuItemId: String, val name: String)

/**
 * Enum of restaurant order states
 */
enum class RestaurantOrderState {
    CREATED, PREPARED, CANCELLED
}

/**
 * Enum of restaurant states
 */
enum class RestaurantState {
    OPEN,
    CLOSED
}

/**
 * Restaurant identifier value object
 *
 * @property identifier identifier
 */
data class RestaurantId(val identifier: String) {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
}

/**
 * Restaurant order identifier value object
 *
 * @property identifier identifier
 */
data class RestaurantOrderId(val identifier: String) {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
}
