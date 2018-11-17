package com.drestaurant.order.domain.api.model

import com.drestaurant.common.domain.api.model.Money
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import java.util.*
import javax.validation.Valid

/**
 * Order 'details' extends the [OrderInfo] with the total amount
 */
data class OrderDetails(val orderInfo: OrderInfo, val orderTotal: Money) : OrderInfo(orderInfo.consumerId, orderInfo.restaurantId, orderInfo.lineItems)

/**
 * Order 'info' holds the list of [OrderLineItem]s, and reference to customer and restaurant
 */
open class OrderInfo(val consumerId: String, val restaurantId: String, @field:Valid val lineItems: List<OrderLineItem>) {

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

}

/**
 * Order line item is referencing the restaurant menu item that is ordered with specific price and quantity
 */
data class OrderLineItem(val menuItemId: String, val name: String, @field:Valid val price: Money, val quantity: Int) {

    val total: Money get() = price.multiply(quantity)
}

/**
 * @author: idugalic
 */
enum class OrderState {
    CREATE_PENDING,
    VERIFIED_BY_CUSTOMER,
    VERIFIED_BY_RESTAURANT,
    PREPARED,
    READY_FOR_DELIVERY,
    DELIVERED,
    REJECTED,
    CANCEL_PENDING,
    CANCELLED
}

data class OrderId(val identifier: String) {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
}