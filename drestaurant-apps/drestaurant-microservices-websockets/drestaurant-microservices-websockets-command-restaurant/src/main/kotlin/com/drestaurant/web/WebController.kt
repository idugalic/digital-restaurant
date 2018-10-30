package com.drestaurant.web

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import com.drestaurant.restaurant.domain.api.model.MenuItem
import com.drestaurant.restaurant.domain.api.model.RestaurantMenu
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import java.math.BigDecimal
import java.util.*


@Controller
class WebController(private val commandGateway: CommandGateway) {

    private val currentUser: String
        get() = if (SecurityContextHolder.getContext().authentication != null) {
            SecurityContextHolder.getContext().authentication.name
        } else "TEST"

    private val auditEntry: AuditEntry
        get() = AuditEntry(currentUser, Calendar.getInstance().time)

    @MessageMapping(value = ["/restaurants/createcommand"])
    fun createRestaurant(request: CreateRestaurantDTO) {
        val menuItems = ArrayList<MenuItem>()
        for ((id, name, price) in request.menuItems) {
            val item = MenuItem(id, name, Money(price))
            menuItems.add(item)
        }
        val menu = RestaurantMenu(menuItems, "ver.0")
        val command = CreateRestaurantCommand(request.name, menu, auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @MessageMapping(value = ["/restaurants/orders/markpreparedcommand"])
    fun markRestaurantOrderAsPrepared(id: String) = commandGateway.send(MarkRestaurantOrderAsPreparedCommand(RestaurantOrderId(id), auditEntry), LoggingCallback.INSTANCE)
}

/**
 * A request for creating a Restaurant
 */
data class CreateRestaurantDTO(val name: String, val menuItems: List<MenuItemDTO>)

/**
 * A Menu item request
 */
data class MenuItemDTO(val id: String, val name: String, val price: BigDecimal)
