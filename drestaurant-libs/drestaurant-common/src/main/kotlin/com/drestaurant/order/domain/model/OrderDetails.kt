package com.drestaurant.order.domain.model

import com.drestaurant.common.domain.model.Money
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

/**
 * Order 'details' extends the [OrderInfo] with the total amount
 */
class OrderDetails(orderInfo: OrderInfo, val orderTotal: Money) : OrderInfo(orderInfo.consumerId, orderInfo.restaurantId, orderInfo.lineItems) {

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }
}
