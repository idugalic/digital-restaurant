package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand
import com.drestaurant.courier.domain.api.CreateCourierCommand
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse

/**
 * REST Controller for handling 'commands'
 */
@RestController
@RequestMapping(value = ["/api/command/courier"])
class CommandController(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)


    @RequestMapping(value = ["/createcommand"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createCourier(@RequestBody request: CreateCourierRequest, response: HttpServletResponse) = commandGateway.send(CreateCourierCommand(PersonName(request.firstName, request.lastName), request.maxNumberOfActiveOrders, auditEntry), LoggingCallback.INSTANCE)


    @RequestMapping(value = ["/{cid}/order/{oid}/assigncommand"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun assignOrderToCourier(@PathVariable cid: String, @PathVariable oid: String, response: HttpServletResponse) = commandGateway.send(AssignCourierOrderToCourierCommand(oid, cid, auditEntry), LoggingCallback.INSTANCE)

    @RequestMapping(value = ["/order/{id}/markdeliveredcommand"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun markCourierOrderAsDelivered(@PathVariable id: String, response: HttpServletResponse) = commandGateway.send(MarkCourierOrderAsDeliveredCommand(id, auditEntry), LoggingCallback.INSTANCE)
}

/**
 * A request for creating a Courier
 */
data class CreateCourierRequest(val firstName: String, val lastName: String, val maxNumberOfActiveOrders: Int)
