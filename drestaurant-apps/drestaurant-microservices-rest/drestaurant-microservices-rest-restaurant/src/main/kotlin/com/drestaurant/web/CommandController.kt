package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.query.FindRestaurantOrderQuery
import com.drestaurant.query.FindRestaurantQuery
import com.drestaurant.query.model.RestaurantEntity
import com.drestaurant.query.model.RestaurantOrderEntity
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import com.drestaurant.restaurant.domain.api.model.*
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
                    val commandResult: RestaurantId? = commandGateway.sendAndWait(command)
                    val restaurantEntity = it.updates().blockFirst()
                    return ResponseEntity.created(URI.create(entityLinks.linkToSingleResource(RestaurantRepository::class.java, restaurantEntity?.id).href)).build()
                }
    }

    @RequestMapping(value = ["/restaurants/{rid}/orders/{roid}/markprepared"], method = [RequestMethod.PUT], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun markRestaurantOrderAsPrepared(@PathVariable rid: String, @PathVariable roid: String, response: HttpServletResponse): ResponseEntity<RestaurantOrderEntity> {
        val command = MarkRestaurantOrderAsPreparedCommand(RestaurantOrderId(roid), auditEntry)
        queryGateway.subscriptionQuery(FindRestaurantOrderQuery(command.targetAggregateIdentifier), ResponseTypes.instanceOf<RestaurantOrderEntity>(RestaurantOrderEntity::class.java), ResponseTypes.instanceOf<RestaurantOrderEntity>(RestaurantOrderEntity::class.java))
                .use {
                    val commandResult: RestaurantOrderId? = commandGateway.sendAndWait(command)
                    val restaurantOrderEntity = it.updates().blockFirst()
                    return if (RestaurantOrderState.PREPARED == restaurantOrderEntity?.state) ResponseEntity.ok().build() else ResponseEntity.badRequest().build()
                }
    }
}

/**
 * A request for creating a Restaurant
 */
data class CreateRestaurantRequest(val name: String, val menuItems: List<MenuItemRequest>)

/**
 * A Menu item request
 */
data class MenuItemRequest(val id: String, val name: String, val price: BigDecimal)