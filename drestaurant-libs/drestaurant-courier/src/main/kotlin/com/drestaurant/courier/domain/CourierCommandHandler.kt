package com.drestaurant.courier.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage.asEventMessage
import org.axonframework.modelling.command.AggregateNotFoundException
import org.axonframework.modelling.command.Repository

internal open class CourierCommandHandler(private val repository: Repository<Courier>, private val eventBus: EventBus) {

    @CommandHandler
    fun handle(command: ValidateOrderByCourierInternalCommand) = try {
        repository.load(command.courierId.identifier).execute { it.validateOrder(command.targetAggregateIdentifier, command.auditEntry) }
    } catch (exception: AggregateNotFoundException) {
        eventBus.publish(asEventMessage<Any>(CourierNotFoundForOrderInternalEvent(command.courierId, command.targetAggregateIdentifier, command.auditEntry)))
    }
}
