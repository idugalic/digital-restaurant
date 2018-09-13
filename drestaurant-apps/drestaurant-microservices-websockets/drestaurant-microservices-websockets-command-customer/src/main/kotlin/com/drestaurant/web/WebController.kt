package com.drestaurant.web

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.customer.domain.api.CreateCustomerCommand
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

    // CUSTOMERS

    @MessageMapping("/customers/createcommand")
    fun createCustomer(request: CreateCustomerDTO) {
        val orderLimit = Money(request.orderLimit)
        val command = CreateCustomerCommand(PersonName(request.firstName, request.lastName), orderLimit, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }
}


/**
 * A request for creating a Customer/Consumer
 */
data class CreateCustomerDTO(val firstName: String, val lastName: String, val orderLimit: BigDecimal)
