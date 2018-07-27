package com.drestaurant.query.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class CourierEntity(@Id var id: String, var aggregateVersion: Long, var firstName: String, var lastName: String, var maxNumberOfActiveOrders: Int, @OneToMany(mappedBy = "courier") var orders: List<CourierOrderEntity>)

