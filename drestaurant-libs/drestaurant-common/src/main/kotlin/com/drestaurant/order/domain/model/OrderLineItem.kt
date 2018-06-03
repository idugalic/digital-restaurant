package com.drestaurant.order.domain.model

import com.drestaurant.common.domain.model.Money
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author: idugalic
 */
class OrderLineItem(@field:NotNull val menuItemId: String, @field:NotNull val name: String, @field:NotNull @field:Valid val price: Money, @field:NotNull private val quantity: Int?) {

    val total: Money get() = price.multiply(quantity!!)

    fun deltaForChangedQuantity(newQuantity: Int): Money {
        return price.multiply(newQuantity - quantity!!)
    }

    fun getQuantity(): Int {
        return quantity!!
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
