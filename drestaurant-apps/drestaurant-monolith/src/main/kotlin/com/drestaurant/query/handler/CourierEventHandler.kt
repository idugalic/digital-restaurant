package com.drestaurant.query.handler

import com.drestaurant.courier.domain.api.CourierCreatedEvent
import com.drestaurant.query.model.CourierEntity
import com.drestaurant.query.repository.CourierRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
internal class CourierEventHandler(private val repository: CourierRepository, private val messagingTemplate: SimpMessageSendingOperations) {

    @EventHandler
    fun handle(event: CourierCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        repository.save(CourierEntity(event.aggregateIdentifier, aggregateVersion, event.name.firstName, event.name.lastName, event.maxNumberOfActiveOrders));
        messagingTemplate.convertAndSend("/topic/couriers.updates", event);
    }

}
