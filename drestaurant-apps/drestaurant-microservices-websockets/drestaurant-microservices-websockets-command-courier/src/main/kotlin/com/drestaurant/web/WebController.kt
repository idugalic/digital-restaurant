package com.drestaurant.web

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand
import com.drestaurant.courier.domain.api.CreateCourierCommand
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import java.util.*


@Controller
class WebController(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)


    // COURIERS

    @MessageMapping(value = "/couriers/createcommand")
    fun createCourier(request: CreateCourierDTO) {
        val command = CreateCourierCommand(PersonName(request.firstName, request.lastName), request.maxNumberOfActiveOrders, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    // COURIER ORDERS

    @MessageMapping(value = "/couriers/orders/assigncommand")
    fun assignOrderToCourier(request: AssignOrderToCourierDTO) {
        val command = AssignCourierOrderToCourierCommand(request.courierOrderId, request.courierId, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @MessageMapping(value = "/couriers/orders/markdeliveredcommand")
    fun markCourierOrderAsDelivered(id: String) {
        val command = MarkCourierOrderAsDeliveredCommand(id, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

}

/**
 * A request for creating a Courier
 */
data class CreateCourierDTO(val firstName: String, val lastName: String, val maxNumberOfActiveOrders: Int)

/**
 * An assign order to courier request
 */
data class AssignOrderToCourierDTO(val courierOrderId: String, val courierId: String)
