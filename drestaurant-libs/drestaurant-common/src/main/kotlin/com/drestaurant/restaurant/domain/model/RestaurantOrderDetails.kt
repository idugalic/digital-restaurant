package com.drestaurant.restaurant.domain.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import java.util.*

class RestaurantOrderDetails(private val lineItems: List<RestaurantOrderLineItem>) {

    fun getLineItems(): List<RestaurantOrderLineItem> {
        return Collections.unmodifiableList(lineItems)
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