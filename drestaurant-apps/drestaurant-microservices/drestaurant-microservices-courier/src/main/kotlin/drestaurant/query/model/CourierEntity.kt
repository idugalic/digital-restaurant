package com.drestaurant.query.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class CourierEntity(@Id var id: String, var aggregateVersion: Long, var firstName: String, var lastName: String, var maxNumberOfActiveOrders: Int)

