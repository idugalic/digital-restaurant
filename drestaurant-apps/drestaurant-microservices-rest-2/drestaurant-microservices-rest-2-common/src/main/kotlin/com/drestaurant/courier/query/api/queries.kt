package com.drestaurant.courier.query.api

import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId

data class FindCourierQuery(val courierId: CourierId)
data class FindCourierOrderQuery(val courierOrderId: CourierOrderId)
