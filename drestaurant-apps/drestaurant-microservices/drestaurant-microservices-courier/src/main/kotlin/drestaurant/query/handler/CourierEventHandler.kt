package com.drestaurant.query.handler

import com.drestaurant.courier.domain.api.CourierCreatedEvent
import com.drestaurant.query.model.CourierEntity
import com.drestaurant.query.repository.CourierRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@ProcessingGroup("default")
@Component
internal class CourierEventHandler @Autowired constructor(private val repository: CourierRepository, private val messagingTemplate: SimpMessageSendingOperations) {

    @EventHandler
    fun handle(event: CourierCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        repository.save(CourierEntity(event.aggregateIdentifier, aggregateVersion, event.name.firstName, event.name.lastName, event.maxNumberOfActiveOrders));
        messagingTemplate.convertAndSend("/topic/couriers.updates", event);
    }

}
