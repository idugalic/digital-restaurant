package com.drestaurant.common.domain.api.model

import java.math.BigDecimal
import java.util.*

/**
 * Audit entry holds the information of 'who' and 'when' performed the commands/actions
 */
data class AuditEntry(val who: String, val `when`: Date)

/**
 * First name and last name
 */
data class PersonName(val firstName: String, val lastName: String)

/**
 * A simple abstraction of the 'money' concept
 */
data class Money(val amount: BigDecimal) {

    fun add(delta: Money): Money {
        return Money(amount.add(delta.amount))
    }

    fun isGreaterThanOrEqual(other: Money): Boolean {
        return amount >= other.amount
    }

    fun multiply(x: Int): Money {
        return Money(amount.multiply(BigDecimal(x)))
    }
}
