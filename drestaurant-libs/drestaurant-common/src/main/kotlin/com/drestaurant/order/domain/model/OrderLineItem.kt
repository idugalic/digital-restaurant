package com.drestaurant.order.domain.model

import com.drestaurant.common.domain.model.Money
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Order line item is referencing the restaurant menu item that is ordered with specific price and quantity
 */
class OrderLineItem(val menuItemId: String, val name: String, @field:Valid val price: Money, val quantity: Int) {

    val total: Money get() = price.multiply(quantity)

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
