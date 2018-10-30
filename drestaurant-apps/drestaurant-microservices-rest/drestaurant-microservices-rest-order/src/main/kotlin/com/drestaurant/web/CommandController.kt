package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.order.domain.api.CreateOrderCommand
import com.drestaurant.order.domain.api.model.OrderId
import com.drestaurant.order.domain.api.model.OrderInfo
import com.drestaurant.order.domain.api.model.OrderLineItem
import com.drestaurant.order.domain.api.model.OrderState
import com.drestaurant.query.FindOrderQuery
import com.drestaurant.query.model.OrderEntity
import com.drestaurant.query.repository.OrderRepository
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


    @RequestMapping(value = ["/orders"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createOrder(@RequestBody request: CreateOrderRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val lineItems = ArrayList<OrderLineItem>()
        for ((id, name, price, quantity) in request.orderItems) {
            val item = OrderLineItem(id, name, Money(price), quantity)
            lineItems.add(item)
        }
        val orderInfo = OrderInfo(request.customerId, request.restaurantId, lineItems)
        val command = CreateOrderCommand(orderInfo, auditEntry)
        queryGateway.subscriptionQuery(FindOrderQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<OrderEntity>(OrderEntity::class.java), ResponseTypes.instanceOf<OrderEntity>(OrderEntity::class.java))
                .use {
                    val commandResult: OrderId? = commandGateway.sendAndWait(command)
                    val orderEntity: OrderEntity? = it.updates().filter { OrderState.VERIFIED_BY_RESTAURANT == it.state || OrderState.REJECTED == it.state }.blockFirst()
                    return if (OrderState.VERIFIED_BY_RESTAURANT == orderEntity?.state) ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(OrderRepository::class.java, orderEntity.id).href)).build() else ResponseEntity.badRequest().build()
                }
    }
}


/**
 * A request for creating an Order
 */
data class CreateOrderRequest(val customerId: String, val restaurantId: String, val orderItems: List<OrderItemRequest>)

/**
 * An Order item request
 */
data class OrderItemRequest(val id: String, val name: String, val price: BigDecimal, val quantity: Int)
