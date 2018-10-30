package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.order.domain.api.CreateOrderCommand
import com.drestaurant.order.domain.api.model.OrderInfo
import com.drestaurant.order.domain.api.model.OrderLineItem
import org.axonframework.commandhandling.gateway.CommandGateway
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
@RequestMapping(value = ["/api/command/order"])
class CommandController(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)


    @RequestMapping(value = ["/createcommand"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createOrder(@RequestBody request: CreateOrderRequest, response: HttpServletResponse) {
        val lineItems = ArrayList<OrderLineItem>()
        for ((id, name, price, quantity) in request.orderItems) {
            val item = OrderLineItem(id, name, Money(price), quantity)
            lineItems.add(item)
        }
        val orderInfo = OrderInfo(request.customerId!!, request.restaurantId!!, lineItems)
        val command = CreateOrderCommand(orderInfo, auditEntry)
        commandGateway.sendAndWait<CreateOrderCommand>(command)
    }
}


/**
 * A request for creating a Restaurant
 */
data class CreateOrderRequest(val customerId: String?, val restaurantId: String?, val orderItems: List<OrderItemRequest>)

/**
 * An Order item request
 */
data class OrderItemRequest(val id: String, val name: String, val price: BigDecimal, val quantity: Int)
