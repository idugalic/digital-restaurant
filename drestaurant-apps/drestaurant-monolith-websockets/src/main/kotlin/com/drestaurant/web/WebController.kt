package com.drestaurant.web

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand
import com.drestaurant.courier.domain.api.CreateCourierCommand
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand
import com.drestaurant.customer.domain.api.CreateCustomerCommand
import com.drestaurant.order.domain.api.CreateOrderCommand
import com.drestaurant.order.domain.model.OrderInfo
import com.drestaurant.order.domain.model.OrderLineItem
import com.drestaurant.query.model.*
import com.drestaurant.query.repository.*
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import com.drestaurant.restaurant.domain.model.MenuItem
import com.drestaurant.restaurant.domain.model.RestaurantMenu
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.firewall.RequestRejectedException
import org.springframework.stereotype.Controller
import java.math.BigDecimal
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
    @MessageMapping("/customers/createcommand")
    fun createCustomer(request: CreateCustomerDTO) = commandGateway.send(CreateCustomerCommand(PersonName(request.firstName, request.lastName), Money(request.orderLimit), auditEntry), LoggingCallback.INSTANCE)

    @SubscribeMapping("/customers")
    fun allCustomers(): Iterable<CustomerEntity> = customerRepository.findAll()

    @SubscribeMapping("/customers/{id}")
    fun getCustomer(@DestinationVariable id: String): CustomerEntity = customerRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // COURIERS
    @MessageMapping(value = ["/couriers/createcommand"])
    fun createCourier(request: CreateCourierDTO) = commandGateway.send(CreateCourierCommand(PersonName(request.firstName, request.lastName), request.maxNumberOfActiveOrders, auditEntry), LoggingCallback.INSTANCE)

    @SubscribeMapping("/couriers")
    fun allCouriers(): Iterable<CourierEntity> = courierRepository.findAll()

    @SubscribeMapping("/couriers/{id}")
    fun getCourier(@DestinationVariable id: String): CourierEntity = courierRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // RESTAURANTS
    @MessageMapping(value = ["/restaurants/createcommand"])
    fun createRestaurant(request: CreateRestaurantDTO) {
        val menuItems = ArrayList<MenuItem>()
        for ((id, name, price) in request.menuItems) {
            val item = MenuItem(id, name, Money(price))
            menuItems.add(item)
        }
        val menu = RestaurantMenu(menuItems, "ver.0")
        val command = CreateRestaurantCommand(request.name, menu, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SubscribeMapping("/restaurants")
    fun allRestaurants(): Iterable<RestaurantEntity> = restaurantRepository.findAll()

    @SubscribeMapping("/restaurants/{id}")
    fun getRestaurant(@DestinationVariable id: String): RestaurantEntity = restaurantRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // ORDERS
    @MessageMapping(value = ["/orders/createcommand"])
    fun createOrder(request: CreateOrderDTO) {
        val lineItems = ArrayList<OrderLineItem>()
        for ((id, name, price, quantity) in request.orderItems) {
            val item = OrderLineItem(id, name, Money(price), quantity)
            lineItems.add(item)
        }
        val orderInfo = OrderInfo(request.customerId!!, request.restaurantId!!, lineItems)
        val command = CreateOrderCommand(orderInfo, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SubscribeMapping("/orders")
    fun allOrders(): Iterable<OrderEntity> = orderRepository.findAll()

    @SubscribeMapping("/orders/{id}")
    fun getOrder(@DestinationVariable id: String): OrderEntity = orderRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // RESTAURANT ORDERS
    @MessageMapping(value = ["/restaurants/orders/markpreparedcommand"])
    fun markRestaurantOrderAsPrepared(id: String) = commandGateway.send(MarkRestaurantOrderAsPreparedCommand(id, auditEntry), LoggingCallback.INSTANCE)

    @SubscribeMapping("/restaurants/orders")
    fun allRestaurantOrders(): Iterable<RestaurantOrderEntity> = restaurantOrderRepository.findAll()

    @SubscribeMapping("/restaurants/orders/{id}")
    fun getRestaurantOrder(@DestinationVariable id: String): RestaurantOrderEntity = restaurantOrderRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }

    // COURIER ORDERS
    @MessageMapping(value = ["/couriers/orders/assigncommand"])
    fun assignOrderToCourier(request: AssignOrderToCourierDTO) = commandGateway.send(AssignCourierOrderToCourierCommand(request.courierOrderId, request.courierId, auditEntry), LoggingCallback.INSTANCE)

    @MessageMapping(value = ["/couriers/orders/markdeliveredcommand"])
    fun markCourierOrderAsDelivered(id: String) = commandGateway.send(MarkCourierOrderAsDeliveredCommand(id, auditEntry), LoggingCallback.INSTANCE)

    @SubscribeMapping("/couriers/orders")
    fun allCourierOrders(): Iterable<CourierOrderEntity> = courierOrderRepository.findAll()

    @SubscribeMapping("/couriers/orders/{id}")
    fun getCourierOrder(@DestinationVariable id: String): CourierOrderEntity = courierOrderRepository.findById(id).orElseThrow { RequestRejectedException("id is null") }
}

/**
 * A request for creating a Courier
 */
data class CreateCourierDTO(val firstName: String, val lastName: String, val maxNumberOfActiveOrders: Int)

/**
 * A request for creating a Customer/Consumer
 */
data class CreateCustomerDTO(val firstName: String, val lastName: String, val orderLimit: BigDecimal)


/**
 * A request for creating a Restaurant
 */
data class CreateOrderDTO(val customerId: String?, val restaurantId: String?, val orderItems: List<OrderItemDTO>)

/**
 * A request for creating a Restaurant
 */
data class CreateRestaurantDTO(val name: String, val menuItems: List<MenuItemDTO>)

/**
 * A Menu item request
 */
data class MenuItemDTO(val id: String, val name: String, val price: BigDecimal)

/**
 * An Order item request
 */
data class OrderItemDTO(val id: String, val name: String, val price: BigDecimal, val quantity: Int)

/**
 * An assign order to courier request
 */
data class AssignOrderToCourierDTO(val courierOrderId: String, val courierId: String)
