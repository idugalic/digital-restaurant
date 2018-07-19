package com.drestaurant.web

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand
import com.drestaurant.courier.domain.api.CreateCourierCommand
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.beans.factory.annotation.Autowired
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
@RequestMapping(value = "/api/command/courier")
class CommandController @Autowired constructor(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)


    @RequestMapping(value = "/createcommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createCourier(@RequestBody request: CreateCourierRequest, response: HttpServletResponse) {
        val command = CreateCourierCommand(request.name, request.maxNumberOfActiveOrders, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }


    @RequestMapping(value = "/{cid}/order/{oid}/assigncommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun assignOrderToCourier(@PathVariable cid: String, @PathVariable oid: String, response: HttpServletResponse) {
        val command = AssignCourierOrderToCourierCommand(oid, cid, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @RequestMapping(value = "/order/{id}/markdeliveredcommand", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun markCourierOrderAsDelivered(@PathVariable id: String, response: HttpServletResponse) {
        val command = MarkCourierOrderAsDeliveredCommand(id, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }
}

/**
 * A request for creating a Courier
 */
data class CreateCourierRequest(val name: PersonName, val maxNumberOfActiveOrders: Int)
