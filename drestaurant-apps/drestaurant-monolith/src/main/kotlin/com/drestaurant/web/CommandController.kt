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
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import com.drestaurant.restaurant.domain.model.MenuItem
import com.drestaurant.restaurant.domain.model.RestaurantMenu
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*
import javax.servlet.http.HttpServletResponse

/**
 * REST Controller for handling 'commands'
 */
@RestController
@RequestMapping(value = "/api/command")
class CommandController(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)

    @RequestMapping(value = "/customer/createcommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createCustomer(@RequestBody request: CreateCustomerRequest, response: HttpServletResponse) {
        val orderLimit = Money(request.orderLimit)
        val command = CreateCustomerCommand(PersonName(request.firstName, request.lastName), orderLimit, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @RequestMapping(value = "/courier/createcommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createCourier(@RequestBody request: CreateCourierRequest, response: HttpServletResponse) {
        val command = CreateCourierCommand(PersonName(request.firstName, request.lastName), request.maxNumberOfActiveOrders, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @RequestMapping(value = "/restaurant/createcommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createRestaurant(@RequestBody request: CreateRestaurantRequest, response: HttpServletResponse) {
        val menuItems = ArrayList<MenuItem>()
        for ((id, name, price) in request.menuItems) {
            val item = MenuItem(id, name, Money(price))
            menuItems.add(item)
        }
        val menu = RestaurantMenu(menuItems, "ver.0")
        val command = CreateRestaurantCommand(request.name, menu, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @RequestMapping(value = "/order/createcommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createOrder(@RequestBody request: CreateOrderRequest, response: HttpServletResponse) {
        val lineItems = ArrayList<OrderLineItem>()
        for ((id, name, price, quantity) in request.orderItems) {
            val item = OrderLineItem(id, name, Money(price), quantity)
            lineItems.add(item)
        }
        val orderInfo = OrderInfo(request.customerId!!, request.restaurantId!!, lineItems)
        val command = CreateOrderCommand(orderInfo, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @RequestMapping(value = "/restaurant/order/{id}/markpreparedcommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun markRestaurantOrderAsPrepared(@PathVariable id: String, response: HttpServletResponse) {
        val command = MarkRestaurantOrderAsPreparedCommand(id, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @RequestMapping(value = "/courier/{cid}/order/{oid}/assigncommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun assignOrderToCourier(@PathVariable cid: String, @PathVariable oid: String, response: HttpServletResponse) {
        val command = AssignCourierOrderToCourierCommand(oid, cid, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @RequestMapping(value = "/courier/order/{id}/markdeliveredcommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun markCourierOrderAsDelivered(@PathVariable id: String, response: HttpServletResponse) {
        val command = MarkCourierOrderAsDeliveredCommand(id, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }
}

/**
 * A request for creating a Courier
 */
data class CreateCourierRequest(val firstName: String, val lastName: String, val maxNumberOfActiveOrders: Int)

/**
 * A request for creating a Customer/Consumer
 */
data class CreateCustomerRequest(val firstName: String, val lastName: String, val orderLimit: BigDecimal)


/**
 * A request for creating a Restaurant
 */
data class CreateOrderRequest(val customerId: String?, val restaurantId: String?, val orderItems: List<OrderItemRequest>)

/**
 * A request for creating a Restaurant
 */
data class CreateRestaurantRequest(val name: String, val menuItems: List<MenuItemRequest>)

/**
 * A Menu item request
 */
data class MenuItemRequest(val id: String, val name: String, val price: BigDecimal)

/**
 * An Order item request
 */
data class OrderItemRequest(val id: String, val name: String, val price: BigDecimal, val quantity: Int)
