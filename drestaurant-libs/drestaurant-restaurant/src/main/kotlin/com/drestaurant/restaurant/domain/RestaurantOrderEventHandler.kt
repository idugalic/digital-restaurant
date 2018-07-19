package com.drestaurant.restaurant.domain

import com.drestaurant.order.domain.api.RestaurantOrderCreationRequestedEvent
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class RestaurantOrderEventHandler @Autowired constructor(private val commandGateway: CommandGateway) {

    @EventHandler
    fun handle(event: RestaurantOrderCreationRequestedEvent) {
        val command = CreateRestaurantOrderCommand(event.aggregateIdentifier, event.orderDetails, event.restaurantId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }
}
