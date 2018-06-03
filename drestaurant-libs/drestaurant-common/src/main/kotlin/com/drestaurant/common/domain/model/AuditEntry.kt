package com.drestaurant.common.domain.model

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import java.util.*

/**
 *
 * A class which encapsulates 'who' and 'when' has perform some action
 *
 * @author: idugalic
 */
class AuditEntry {

    var who: String? = null
    var `when`: Date? = null

    constructor() {

    }

    constructor(who: String, `when`: Date = Date()) : super() {
        this.who = who
        this.`when` = `when`
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
