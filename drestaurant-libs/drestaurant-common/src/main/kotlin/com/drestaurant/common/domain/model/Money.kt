package com.drestaurant.common.domain.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import java.math.BigDecimal
import javax.validation.constraints.NotNull

/**
 *
 * @author: idugalic
 */
class Money {

    @NotNull
    var amount: BigDecimal? = null
        private set

    constructor(amount: BigDecimal) {
        this.amount = amount
    }

    constructor(s: String) {
        this.amount = BigDecimal(s)
    }

    constructor(i: Int) {
        this.amount = BigDecimal(i)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o)
            return true

        if (o == null || javaClass != o.javaClass)
            return false

        val money = o as Money?

        return EqualsBuilder().append(amount, money!!.amount).isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37).append(amount).toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this).append("amount", amount).toString()
    }

    fun add(delta: Money): Money {
        return Money(amount!!.add(delta.amount!!))
    }

    fun isGreaterThanOrEqual(other: Money): Boolean {
        return amount!!.compareTo(other.amount!!) >= 0
    }

    fun asString(): String {
        return amount!!.toPlainString()
    }

    fun multiply(x: Int): Money {
        return Money(amount!!.multiply(BigDecimal(x)))
    }
}
