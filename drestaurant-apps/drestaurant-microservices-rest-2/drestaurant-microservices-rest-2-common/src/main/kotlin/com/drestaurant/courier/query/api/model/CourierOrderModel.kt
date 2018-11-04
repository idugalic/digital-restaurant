package com.drestaurant.courier.query.api.model

import com.drestaurant.courier.domain.api.model.CourierOrderState
import java.io.Serializable

class CourierOrderModel(val id: String, val aggregateVersion: Long, val courier: CourierModel, val state: CourierOrderState) : Serializable
