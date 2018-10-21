package com.drestaurant.web

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.customer.domain.api.CreateCustomerCommand
import com.drestaurant.query.FindCustomerQuery
import com.drestaurant.query.model.CustomerEntity
import com.drestaurant.query.repository.CustomerRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.math.BigDecimal
import java.net.URI
import java.util.*
import javax.servlet.http.HttpServletResponse


/**
 * Repository REST Controller for handling 'commands' only
 *
 * Sometimes you may want to write a custom handler for a specific resource. To take advantage of Spring Data REST’s settings, message converters, exception handling, and more, we use the @RepositoryRestController annotation instead of a standard Spring MVC @Controller or @RestController
 */
@RepositoryRestController
class CommandController(private val commandGateway: CommandGateway, private val queryGateway: QueryGateway, private val entityLinks: RepositoryEntityLinks) {

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
        queryGateway.subscriptionQuery(FindCustomerQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CustomerEntity>(CustomerEntity::class.java), ResponseTypes.instanceOf<CustomerEntity>(CustomerEntity::class.java))
                .use {
                    val commandResult: String = commandGateway.sendAndWait(command)
                    /* Returning the first update sent to our find customer query. */
                    val customerEntity = it.updates().blockFirst()
                    return ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(CustomerRepository::class.java, customerEntity?.id).href)).build()
                }
    }
}

/**
 * A request for creating a Customer/Consumer
 */
data class CreateCustomerRequest(val firstName: String, val lastName: String, val orderLimit: BigDecimal)
