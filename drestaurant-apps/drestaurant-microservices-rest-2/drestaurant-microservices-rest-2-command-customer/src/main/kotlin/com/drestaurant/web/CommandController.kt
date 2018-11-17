package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.customer.domain.api.CreateCustomerCommand
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.query.api.FindCustomerQuery
import com.drestaurant.customer.query.api.model.CustomerModel
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*
import javax.servlet.http.HttpServletResponse


/**
 * Repository REST Controller for handling 'commands' only
 *
 */
@RestController
class CommandController(private val commandGateway: CommandGateway, private val queryGateway: QueryGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)

    @RequestMapping(value = ["/customers"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createCustomer(@RequestBody request: CreateCustomerRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val orderLimit = Money(request.orderLimit)
        val command = CreateCustomerCommand(PersonName(request.firstName, request.lastName), orderLimit, auditEntry)
        queryGateway.subscriptionQuery(FindCustomerQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CustomerModel>(CustomerModel::class.java), ResponseTypes.instanceOf<CustomerModel>(CustomerModel::class.java))
                .use {
                    val commandResult: CustomerId? = commandGateway.sendAndWait(command)
                    /* Returning the first update sent to our find customer query. */
                    val customerEntity = it.updates().blockFirst()
                    return ResponseEntity.ok().build()
                }
    }
}

/**
 * A request for creating a Customer/Consumer
 */
data class CreateCustomerRequest(val firstName: String, val lastName: String, val orderLimit: BigDecimal)
