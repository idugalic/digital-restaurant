package com.drestaurant.order.domain.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author: idugalic
 */
open class OrderInfo(val consumerId: String, val restaurantId: String, @field:Valid val lineItems: List<OrderLineItem>) {

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
