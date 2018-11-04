package com.drestaurant.customer.query.api.model

import java.io.Serializable
import java.math.BigDecimal

data class CustomerModel(val id: String, val aggregateVersion: Long, val firstName: String, val lastName: String, val orderLimit: BigDecimal) : Serializable
