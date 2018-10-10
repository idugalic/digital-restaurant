package com.drestaurant.restaurant.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateNotFoundException
import org.axonframework.commandhandling.model.Repository
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage.asEventMessage

internal open class RestaurantCommandHandler(private val repository: Repository<Restaurant>, private val eventBus: EventBus) {

    @CommandHandler
    fun handle(command: ValidateOrderByRestaurantCommand) = try {
        repository.load(command.restaurantId).execute { it.validateOrder(command.orderId, command.lineItems, command.auditEntry) }
    } catch (exception: AggregateNotFoundException) {
        eventBus.publish(asEventMessage<Any>(RestaurantNotFoundForOrderEvent(command.restaurantId, command.orderId, command.auditEntry)))
    }
}
