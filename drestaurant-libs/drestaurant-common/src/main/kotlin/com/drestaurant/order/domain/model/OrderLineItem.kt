package com.drestaurant.order.domain.model

import com.drestaurant.common.domain.model.Money
import javax.validation.Valid

/**
 * Order line item is referencing the restaurant menu item that is ordered with specific price and quantity
 */
data class OrderLineItem(val menuItemId: String, val name: String, @field:Valid val price: Money, val quantity: Int) {

    val total: Money get() = price.multiply(quantity)
}
