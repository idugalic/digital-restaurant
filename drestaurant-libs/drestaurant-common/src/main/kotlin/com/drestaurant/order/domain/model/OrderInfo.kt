package com.drestaurant.order.domain.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import javax.validation.Valid
import javax.validation.constraints.NotNull


/**
 * Order 'info' holds the list of [OrderLineItem]s, and reference to customer and restaurant
 */
open class OrderInfo(val consumerId: String, val restaurantId: String, @field:Valid val lineItems: List<OrderLineItem>) {

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

}
