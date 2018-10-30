package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.customer.domain.api.CreateCustomerCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
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
@RequestMapping(value = ["/api/command/customer"])
class CommandController(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)

    @RequestMapping(value = ["/createcommand"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createCustomer(@RequestBody request: CreateCustomerRequest, response: HttpServletResponse) = commandGateway.send(CreateCustomerCommand(PersonName(request.firstName, request.lastName), Money(request.orderLimit), auditEntry), LoggingCallback.INSTANCE)
}

/**
 * A request for creating a Customer/Consumer
 */
data class CreateCustomerRequest(val firstName: String, val lastName: String, val orderLimit: BigDecimal)
