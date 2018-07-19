package com.drestaurant.courier.domain

import com.drestaurant.courier.domain.api.CreateCourierOrderCommand
import com.drestaurant.order.domain.api.CourierOrderCreationRequestedEvent
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class CourierOrderEventHandler @Autowired constructor(private val commandGateway: CommandGateway) {

    @EventHandler
    fun handle(event: CourierOrderCreationRequestedEvent) {
        val command = CreateCourierOrderCommand(event.aggregateIdentifier, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }
}
