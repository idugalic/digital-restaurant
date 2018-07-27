package com.drestaurant.query.model

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class CustomerEntity(@Id var id: String, var aggregateVersion: Long, var firstName: String, var lastName: String, var orderLimit: BigDecimal)
