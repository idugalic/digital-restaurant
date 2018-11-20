package com.drestaurant.customer.domain.api.model

import java.io.Serializable
import java.util.*

enum class CustomerOrderState {
    CREATE_PENDING, CREATED, REJECTED, DELIVERED, CANCEL_PENDING, CANCELLED
}

/**
 * Customer identifier value object
 *
 * @property identifier identifier
 */
data class CustomerId(val identifier: String) : Serializable {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
}

/**
 * Customer order identifier value object
 *
 * @property identifier identifier
 */
data class CustomerOrderId(val identifier: String) {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = identifier
}
