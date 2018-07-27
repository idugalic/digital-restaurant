package com.drestaurant.query.handler

import com.drestaurant.customer.domain.api.CustomerCreatedEvent
import com.drestaurant.query.model.CustomerEntity
import com.drestaurant.query.repository.CustomerRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@ProcessingGroup("customer")
@Component
internal class CustomerEventHandler(private val repository: CustomerRepository, private val messagingTemplate: SimpMessageSendingOperations) {

    @EventHandler
    fun handle(event: CustomerCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        repository.save(CustomerEntity(event.aggregateIdentifier, aggregateVersion, event.name.firstName, event.name.lastName, event.orderLimit.amount));
        messagingTemplate.convertAndSend("/topic/customers.updates", event);
    }

}
