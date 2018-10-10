package com.drestaurant.web

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand
import com.drestaurant.courier.domain.api.CreateCourierCommand
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand
import com.drestaurant.courier.domain.model.CourierOrderState
import com.drestaurant.query.FindCourierOrderQuery
import com.drestaurant.query.FindCourierQuery
import com.drestaurant.query.model.CourierEntity
import com.drestaurant.query.model.CourierOrderEntity
import com.drestaurant.query.repository.CourierRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.responsetypes.ResponseTypes
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
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


    @RequestMapping(value = ["/couriers"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createCourier(@RequestBody request: CreateCourierRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val command = CreateCourierCommand(PersonName(request.firstName, request.lastName), request.maxNumberOfActiveOrders, auditEntry)
        queryGateway.subscriptionQuery(FindCourierQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CourierEntity>(CourierEntity::class.java), ResponseTypes.instanceOf<CourierEntity>(CourierEntity::class.java))
                .use {
                    val commandResult: String = commandGateway.sendAndWait(command)
                    val courierEntity = it.updates().blockFirst()
                    return ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(CourierRepository::class.java, courierEntity?.id).href)).build()
                }
    }

    @RequestMapping(value = ["/couriers/{cid}/orders/{coid}/assign"], method = [RequestMethod.PUT], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun assignOrderToCourier(@PathVariable cid: String, @PathVariable coid: String, response: HttpServletResponse): ResponseEntity<CourierOrderEntity> {
        val command = AssignCourierOrderToCourierCommand(coid, cid, auditEntry)
        queryGateway.subscriptionQuery(FindCourierOrderQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CourierOrderEntity>(CourierOrderEntity::class.java), ResponseTypes.instanceOf<CourierOrderEntity>(CourierOrderEntity::class.java))
                .use {
                    val commandResult: String? = commandGateway.sendAndWait(command)
                    val courierOrderEntity = it.updates().blockFirst()
                    return if (CourierOrderState.ASSIGNED == courierOrderEntity?.state) ResponseEntity.ok().build() else ResponseEntity.badRequest().build()
                }
    }

    @RequestMapping(value = ["/couriers/{cid}/orders/{coid}/markdelivered"], method = [RequestMethod.PUT], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun markCourierOrderAsDelivered(@PathVariable cid: String, @PathVariable coid: String, response: HttpServletResponse): ResponseEntity<CourierOrderEntity> {
        val command = MarkCourierOrderAsDeliveredCommand(coid, auditEntry)
        queryGateway.subscriptionQuery(FindCourierOrderQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CourierOrderEntity>(CourierOrderEntity::class.java), ResponseTypes.instanceOf<CourierOrderEntity>(CourierOrderEntity::class.java))
                .use {
                    val commandResult: String? = commandGateway.sendAndWait(command)
                    val courierOrderEntity = it.updates().blockFirst()
                    return if (CourierOrderState.DELIVERED == courierOrderEntity?.state) ResponseEntity.ok().build() else ResponseEntity.badRequest().build()
                }
    }
}

/**
 * A request for creating a Courier
 */
data class CreateCourierRequest(val firstName: String, val lastName: String, val maxNumberOfActiveOrders: Int)
