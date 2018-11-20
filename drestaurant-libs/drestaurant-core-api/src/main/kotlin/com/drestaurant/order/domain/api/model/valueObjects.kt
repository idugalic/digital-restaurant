package com.drestaurant.order.domain.api.model

import com.drestaurant.common.domain.api.model.Money
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import java.util.*
import javax.validation.Valid

/**
 * Order 'details' value object, extends the [OrderInfo] with the total amount
 *
 * @property orderInfo basic information about the order
 * @property orderTotal total price amount of the order
 */
data class OrderDetails(val orderInfo: OrderInfo, val orderTotal: Money) : OrderInfo(orderInfo.consumerId, orderInfo.restaurantId, orderInfo.lineItems)

/**
 * Order 'info' holds the list of [OrderLineItem]s, and reference to customer and restaurant
 *
 * @property consumerId identifier of the customer/consumer
 * @property restaurantId identifier of the restaurant
 * @property lineItems list of the order line items
 */
open class OrderInfo(val consumerId: String, val restaurantId: String, @field:Valid val lineItems: List<OrderLineItem>) {

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

}

/**
 * Order line item is referencing the restaurant menu item that is ordered with specific price and quantity
 *
 * @property menuItemId reference to a menu item
 * @property name name of the order line item
 * @property price price of the line item
 * @property quantity quantity of the line item
 */
data class OrderLineItem(val menuItemId: String, val name: String, @field:Valid val price: Money, val quantity: Int) {

    val total: Money get() = price.multiply(quantity)
}

/**
 * Enum of order states
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

/**
 * Order identifier value object
 *
 * @property identifier identifier
 */
data class OrderId(val identifier: String) {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
}