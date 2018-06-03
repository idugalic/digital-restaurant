package com.drestaurant.courier.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateNotFoundException
import org.axonframework.commandhandling.model.Repository
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage.asEventMessage

internal open class CourierCommandHandler(private val repository: Repository<Courier>, private val eventBus: EventBus) {

    @CommandHandler
    fun handle(command: ValidateOrderByCourierCommand) {
        try {
            val courierAggregate = repository.load(command.courierId)
            courierAggregate.execute { courier -> courier.validateOrder(command.orderId, command.auditEntry) }
        } catch (exception: AggregateNotFoundException) {
            eventBus.publish(asEventMessage<Any>(CourierNotFoundForOrderEvent(command.courierId, command.orderId, command.auditEntry)))
        }

    }

}
