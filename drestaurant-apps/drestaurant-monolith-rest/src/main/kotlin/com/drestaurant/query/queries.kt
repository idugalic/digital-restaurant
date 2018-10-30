package com.drestaurant.query

import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.order.domain.api.model.OrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId

internal class FindCourierQuery(val courierId: CourierId)
internal class FindAllCouriersQuery
internal class FindCustomerQuery(val customerId: CustomerId)
internal class FindAllCustomersQuery
internal class FindRestaurantQuery(val restaurantId: RestaurantId)
internal class FindAllRestaurantsQuery
internal class FindOrderQuery(val orderId: OrderId)
internal class FindAllOrdersQuery
internal class FindRestaurantOrderQuery(val restaurantOrderId: RestaurantOrderId)
internal class FindAllRestaurantOrdersQuery
internal class FindCourierOrderQuery(val courierOrderId: CourierOrderId)
internal class FindAllCourierOrdersQuery