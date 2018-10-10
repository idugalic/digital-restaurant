package com.drestaurant.web

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import com.drestaurant.restaurant.domain.model.MenuItem
import com.drestaurant.restaurant.domain.model.RestaurantMenu
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
@RequestMapping(value = "/api/command")
class CommandController(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)


    @RequestMapping(value = ["/restaurant/createcommand"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createRestaurant(@RequestBody request: CreateRestaurantRequest, response: HttpServletResponse) {
        val menuItems = ArrayList<MenuItem>()
        for ((id, name, price) in request.menuItems) {
            val item = MenuItem(id, name, Money(price))
            menuItems.add(item)
        }
        val menu = RestaurantMenu(menuItems, "ver.0")
        val command = CreateRestaurantCommand(request.name, menu, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }


    @RequestMapping(value = ["/restaurant/order/{id}/markpreparedcommand"], method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(value = HttpStatus.CREATED)
    fun markRestaurantOrderAsPrepared(@PathVariable id: String, response: HttpServletResponse) = commandGateway.send(MarkRestaurantOrderAsPreparedCommand(id, auditEntry), LoggingCallback.INSTANCE)

}

/**
 * A request for creating a Restaurant
 */
data class CreateRestaurantRequest(val name: String, val menuItems: List<MenuItemRequest>)

/**
 * A Menu item request
 */
data class MenuItemRequest(val id: String, val name: String, val price: BigDecimal)