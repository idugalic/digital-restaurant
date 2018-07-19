package com.drestaurant.customer.domain

import com.drestaurant.customer.domain.api.CreateCustomerOrderCommand
import com.drestaurant.order.domain.api.CustomerOrderCreationRequestedEvent
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class CustomerOrderEventHandler @Autowired constructor(private val commandGateway: CommandGateway) {

    @EventHandler
    fun handle(event: CustomerOrderCreationRequestedEvent) {
        val command = CreateCustomerOrderCommand(event.aggregateIdentifier, event.orderTotal, event.customerId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }
}
