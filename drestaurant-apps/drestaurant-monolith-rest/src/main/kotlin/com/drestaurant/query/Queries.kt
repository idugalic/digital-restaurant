package com.drestaurant.query

internal class FindCourierQuery(val courierId: String)
internal class FindAllCouriersQuery()
internal class FindCustomerQuery(val customerId: String)
internal class FindAllCustomersQuery()
internal class FindRestaurantQuery(val restaurantId: String)
internal class FindAllRestaurantsQuery()
internal class FindOrderQuery(val orderId: String)
internal class FindAllOrdersQuery()
internal class FindRestaurantOrderQuery(val restaurantOrderId: String)
internal class FindAllRestaurantOrdersQuery()
internal class FindCourierOrderQuery(val courierOrderId: String)
internal class FindAllCourierOrdersQuery()