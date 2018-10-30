package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.query.model.*
import com.drestaurant.query.repository.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.firewall.RequestRejectedException
import org.springframework.stereotype.Controller
import java.util.*


@Controller
class WebController(private val commandGateway: CommandGateway, private val customerRepository: CustomerRepository, private val courierRepository: CourierRepository, private val restaurantRepository: RestaurantRepository, private val orderRepository: OrderRepository, private val restaurantOrderRepository: RestaurantOrderRepository, private val courierOrderRepository: CourierOrderRepository) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)

    // CUSTOMERS
    @SubscribeMapping("/customers")
    fun allCustomers(): Iterable<CustomerEntity> = customerRepository.findAll()

    @SubscribeMapping("/customers/{id}")
    fun getCustomer(@DestinationVariable id: String): CustomerEntity = customerRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // COURIERS
    @SubscribeMapping("/couriers")
    fun allCouriers(): Iterable<CourierEntity> = courierRepository.findAll()

    @SubscribeMapping("/couriers/{id}")
    fun getCourier(@DestinationVariable id: String): CourierEntity = courierRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // RESTAURANTS
    @SubscribeMapping("/restaurants")
    fun allRestaurants(): Iterable<RestaurantEntity> = restaurantRepository.findAll()

    @SubscribeMapping("/restaurants/{id}")
    fun getRestaurant(@DestinationVariable id: String): RestaurantEntity = restaurantRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // ORDERS
    @SubscribeMapping("/orders")
    fun allOrders(): Iterable<OrderEntity> = orderRepository.findAll()

    @SubscribeMapping("/orders/{id}")
    fun getOrder(@DestinationVariable id: String): OrderEntity = orderRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // RESTAURANT ORDERS
    @SubscribeMapping("/restaurants/orders")
    fun allRestaurantOrders(): Iterable<RestaurantOrderEntity> = restaurantOrderRepository.findAll()

    @SubscribeMapping("/restaurants/orders/{id}")
    fun getRestaurantOrder(@DestinationVariable id: String): RestaurantOrderEntity = restaurantOrderRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // COURIER ORDERS
    @SubscribeMapping("/couriers/orders")
    fun allCourierOrders(): Iterable<CourierOrderEntity> = courierOrderRepository.findAll()

    @SubscribeMapping("/couriers/orders/{id}")
    fun getCourierOrder(@DestinationVariable id: String): CourierOrderEntity = courierOrderRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }
}
