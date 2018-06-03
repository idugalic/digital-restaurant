package com.drestaurant.order.domain.model

import com.drestaurant.common.domain.model.Money
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

/**
 * @author: idugalic
 */
class OrderDetails : OrderInfo {

    var orderTotal: Money

    constructor(consumerId: String, restaurantId: String, lineItems: List<OrderLineItem>, orderTotal: Money) : super(consumerId, restaurantId, lineItems) {
        this.orderTotal = orderTotal
    }

    constructor(orderInfo: OrderInfo, orderTotal: Money) : super(orderInfo.consumerId, orderInfo.restaurantId, orderInfo.lineItems) {
        this.orderTotal = orderTotal
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    override fun equals(o: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, o)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }

}
