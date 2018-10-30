package com.drestaurant.customer.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage.asEventMessage
import org.axonframework.modelling.command.AggregateNotFoundException
import org.axonframework.modelling.command.Repository

internal open class CustomerCommandHandler(private val repository: Repository<Customer>, private val eventBus: EventBus) {

    @CommandHandler
    fun handle(command: ValidateOrderByCustomerInternalCommand) = try {
        repository.load(command.customerId.identifier).execute { it.validateOrder(command.targetAggregateIdentifier, command.orderTotal, command.auditEntry) }
    } catch (exception: AggregateNotFoundException) {
        eventBus.publish(asEventMessage<Any>(CustomerNotFoundForOrderInternalEvent(command.customerId, command.targetAggregateIdentifier, command.orderTotal, command.auditEntry)))
    }
}
