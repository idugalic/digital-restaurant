package com.drestaurant.restaurant.domain.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

/**
 * Restaurant menu holds a list of [MenuItem]s. It can have multiple versions
 */
class RestaurantMenu(@field:NotEmpty @field:Valid val menuItems: MutableList<MenuItem>, val menuVersion: String) {

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
