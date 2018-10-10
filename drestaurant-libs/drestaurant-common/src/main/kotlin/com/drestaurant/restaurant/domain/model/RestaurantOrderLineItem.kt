package com.drestaurant.restaurant.domain.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

/**
 * Restaurant order line item is referencing the restaurant menu item that is ordered with specific quantity
 */
class RestaurantOrderLineItem(val quantity: Int, val menuItemId: String, val name: String) {

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

}
