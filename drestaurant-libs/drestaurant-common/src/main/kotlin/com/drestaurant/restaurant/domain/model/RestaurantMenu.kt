package com.drestaurant.restaurant.domain.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class RestaurantMenu(@field:NotNull @field:NotEmpty @field:Valid private var menuItems: MutableList<MenuItem>?, val menuVersion: String) {

    fun getMenuItems(): List<MenuItem> {
        return Collections.unmodifiableList(this.menuItems!!)
    }

    fun addMenuItem(menuItem: MenuItem): List<MenuItem> {
        if (this.menuItems == null) this.menuItems = ArrayList()
        this.menuItems!!.add(menuItem)
        return Collections.unmodifiableList(this.menuItems!!)
    }

    fun removeMenuItem(menuItem: MenuItem): List<MenuItem> {
        if (this.menuItems == null) this.menuItems = ArrayList()
        this.menuItems!!.remove(menuItem)
        return Collections.unmodifiableList(this.menuItems!!)
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
