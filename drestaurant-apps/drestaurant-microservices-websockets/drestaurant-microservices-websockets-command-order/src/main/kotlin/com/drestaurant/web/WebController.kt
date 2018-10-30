package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.order.domain.api.CreateOrderCommand
import com.drestaurant.order.domain.api.model.OrderInfo
import com.drestaurant.order.domain.api.model.OrderLineItem
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import java.math.BigDecimal
import java.util.*


@Controller
class WebController(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)


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
}

/**
 * A request for creating a Restaurant
 */
data class CreateOrderDTO(val customerId: String?, val restaurantId: String?, val orderItems: List<OrderItemDTO>)

/**
 * An Order item request
 */
data class OrderItemDTO(val id: String, val name: String, val price: BigDecimal, val quantity: Int)
