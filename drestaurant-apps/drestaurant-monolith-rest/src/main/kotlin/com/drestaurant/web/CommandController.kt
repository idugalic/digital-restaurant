package com.drestaurant.web

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand
import com.drestaurant.courier.domain.api.CreateCourierCommand
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand
import com.drestaurant.courier.domain.model.CourierOrderState
import com.drestaurant.customer.domain.api.CreateCustomerCommand
import com.drestaurant.order.domain.api.CreateOrderCommand
import com.drestaurant.order.domain.model.OrderInfo
import com.drestaurant.order.domain.model.OrderLineItem
import com.drestaurant.order.domain.model.OrderState
import com.drestaurant.query.*
import com.drestaurant.query.model.*
import com.drestaurant.query.repository.CourierRepository
import com.drestaurant.query.repository.CustomerRepository
import com.drestaurant.query.repository.OrderRepository
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import com.drestaurant.restaurant.domain.model.MenuItem
import com.drestaurant.restaurant.domain.model.RestaurantMenu
import com.drestaurant.restaurant.domain.model.RestaurantOrderState
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PathVariable
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
        val command = CreateCustomerCommand(PersonName(request.firstName, request.lastName), Money(request.orderLimit), auditEntry)
        queryGateway.subscriptionQuery(FindCustomerQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<CustomerEntity>(CustomerEntity::class.java), ResponseTypes.instanceOf<CustomerEntity>(CustomerEntity::class.java))
                .use {
                    val commandResult: String = commandGateway.sendAndWait(command)
                    /* Returning the first update sent to our find customer query. */
                    val customerEntity = it.updates().blockFirst()
                    return ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(CustomerRepository::class.java, customerEntity?.id).href)).build()
                }
    }

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

    @RequestMapping(value = ["/restaurants"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRestaurant(@RequestBody request: CreateRestaurantRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val menuItems = ArrayList<MenuItem>()
        for ((id, name, price) in request.menuItems) {
            val item = MenuItem(id, name, Money(price))
            menuItems.add(item)
        }
        val menu = RestaurantMenu(menuItems, "ver.0")
        val command = CreateRestaurantCommand(request.name, menu, auditEntry)
        queryGateway.subscriptionQuery(FindRestaurantQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<RestaurantEntity>(RestaurantEntity::class.java), ResponseTypes.instanceOf<RestaurantEntity>(RestaurantEntity::class.java))
                .use {
                    val commandResult: String = commandGateway.sendAndWait(command)
                    val restaurantEntity = it.updates().blockFirst()
                    return ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(RestaurantRepository::class.java, restaurantEntity?.id).href)).build()
                }
    }

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
                    val commandResult: String = commandGateway.sendAndWait(command)
                    val orderEntity: OrderEntity? = it.updates().filter { OrderState.VERIFIED_BY_RESTAURANT == it.state || OrderState.REJECTED == it.state }.blockFirst()
                    return if (OrderState.VERIFIED_BY_RESTAURANT == orderEntity?.state) ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(OrderRepository::class.java, orderEntity.id).href)).build() else ResponseEntity.badRequest().build()
                }
    }

    @RequestMapping(value = ["/restaurants/{rid}/orders/{roid}/markprepared"], method = [RequestMethod.PUT], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun markRestaurantOrderAsPrepared(@PathVariable rid: String, @PathVariable roid: String, response: HttpServletResponse): ResponseEntity<RestaurantOrderEntity> {
        val command = MarkRestaurantOrderAsPreparedCommand(roid, auditEntry)
        queryGateway.subscriptionQuery(FindRestaurantOrderQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<RestaurantOrderEntity>(RestaurantOrderEntity::class.java), ResponseTypes.instanceOf<RestaurantOrderEntity>(RestaurantOrderEntity::class.java))
                .use {
                    val commandResult: String? = commandGateway.sendAndWait(command)
                    val restaurantOrderEntity = it.updates().blockFirst()
                    return if (RestaurantOrderState.PREPARED == restaurantOrderEntity?.state) ResponseEntity.ok().build() else ResponseEntity.badRequest().build()
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

/**
 * A request for creating a Customer/Consumer
 */
data class CreateCustomerRequest(val firstName: String, val lastName: String, val orderLimit: BigDecimal)

/**
 * A request for creating an Order
 */
data class CreateOrderRequest(val customerId: String, val restaurantId: String, val orderItems: List<OrderItemRequest>)

/**
 * A request for creating a Restaurant
 */
data class CreateRestaurantRequest(val name: String, val menuItems: List<MenuItemRequest>)

/**
 * A Menu item request
 */
data class MenuItemRequest(val id: String, val name: String, val price: BigDecimal)

/**
 * An Order item request
 */
data class OrderItemRequest(val id: String, val name: String, val price: BigDecimal, val quantity: Int)
