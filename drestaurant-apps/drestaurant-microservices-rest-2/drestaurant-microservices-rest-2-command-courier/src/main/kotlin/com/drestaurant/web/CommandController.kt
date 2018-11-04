package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand
import com.drestaurant.courier.domain.api.CreateCourierCommand
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand
import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId
import com.drestaurant.courier.domain.api.model.CourierOrderState
import com.drestaurant.courier.query.api.FindCourierOrderQuery
import com.drestaurant.courier.query.api.FindCourierQuery
import com.drestaurant.courier.query.api.model.CourierModel
import com.drestaurant.courier.query.api.model.CourierOrderModel
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse


/**
 * Repository REST Controller for handling 'commands' only
 *
 */
@RestController
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
        queryGateway.subscriptionQuery(FindCourierQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CourierModel>(CourierModel::class.java), ResponseTypes.instanceOf<CourierModel>(CourierModel::class.java))
                .use {
                    val commandResult: CourierId? = commandGateway.sendAndWait(command)
                    val courierModel = it.updates().blockFirst()
                    return ResponseEntity.ok().build()
                }
    }

    @RequestMapping(value = ["/couriers/{cid}/orders/{coid}/assign"], method = [RequestMethod.PUT], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun assignOrderToCourier(@PathVariable cid: String, @PathVariable coid: String, response: HttpServletResponse): ResponseEntity<CourierOrderModel> {
        val command = AssignCourierOrderToCourierCommand(CourierOrderId(coid), CourierId(cid), auditEntry)
        queryGateway.subscriptionQuery(FindCourierOrderQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CourierOrderModel>(CourierOrderModel::class.java), ResponseTypes.instanceOf<CourierOrderModel>(CourierOrderModel::class.java))
                .use {
                    val commandResult: CourierOrderId? = commandGateway.sendAndWait(command)
                    val courierOrderModel = it.updates().blockFirst()
                    return if (CourierOrderState.ASSIGNED == courierOrderModel?.state) ResponseEntity.ok().build() else ResponseEntity.badRequest().build()
                }
    }

    @RequestMapping(value = ["/couriers/{cid}/orders/{coid}/markdelivered"], method = [RequestMethod.PUT], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun markCourierOrderAsDelivered(@PathVariable cid: String, @PathVariable coid: String, response: HttpServletResponse): ResponseEntity<CourierOrderModel> {
        val command = MarkCourierOrderAsDeliveredCommand(CourierOrderId(coid), auditEntry)
        queryGateway.subscriptionQuery(FindCourierOrderQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CourierOrderModel>(CourierOrderModel::class.java), ResponseTypes.instanceOf<CourierOrderModel>(CourierOrderModel::class.java))
                .use {
                    val commandResult: CourierOrderId? = commandGateway.sendAndWait(command)
                    val courierOrderEntity = it.updates().blockFirst()
                    return if (CourierOrderState.DELIVERED == courierOrderEntity?.state) ResponseEntity.ok().build() else ResponseEntity.badRequest().build()
                }
    }
}

/**
 * A request for creating a Courier
 */
data class CreateCourierRequest(val firstName: String, val lastName: String, val maxNumberOfActiveOrders: Int)
