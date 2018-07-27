package com.drestaurant.query.handler

import com.drestaurant.courier.domain.api.CourierOrderAssignedEvent
import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent
import com.drestaurant.courier.domain.api.CourierOrderNotAssignedEvent
import com.drestaurant.courier.domain.model.CourierOrderState
import com.drestaurant.query.model.CourierOrderEntity
import com.drestaurant.query.repository.CourierOrderRepository
import com.drestaurant.query.repository.CourierRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
internal class CourierOrderHandler(private val repository: CourierOrderRepository, private val courierRepository: CourierRepository, private val messagingTemplate: SimpMessageSendingOperations) {

    @EventHandler
    fun handle(event: CourierOrderCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        val record = CourierOrderEntity(event.aggregateIdentifier, aggregateVersion, null, CourierOrderState.CREATED)
        repository.save(record)
        broadcastUpdates()
    }

    @EventHandler
    fun handle(event: CourierOrderAssignedEvent, @SequenceNumber aggregateVersion: Long) {
        val courierEntity = courierRepository.findById(event.courierId).get()
        val record = repository.findById(event.aggregateIdentifier).get()
        record.state = CourierOrderState.ASSIGNED
        record.courier = courierEntity
        repository.save(record)
        broadcastUpdates()
    }

    @EventHandler
    fun handle(event: CourierOrderNotAssignedEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier).get()
        //record.state = CourierOrderState.CREATED
        //repository.save(record)
        broadcastUpdates()
    }

    @EventHandler
    fun handle(event: CourierOrderDeliveredEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier).get()
        record.state = CourierOrderState.DELIVERED
        repository.save(record)
        broadcastUpdates()
    }

    private fun broadcastUpdates() {
        messagingTemplate.convertAndSend("/topic/couriers/orders.updates", repository.findAll())
    }

}
