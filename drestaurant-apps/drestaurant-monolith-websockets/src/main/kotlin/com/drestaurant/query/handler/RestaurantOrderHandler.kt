package com.drestaurant.query.handler

import com.drestaurant.query.model.RestaurantOrderEntity
import com.drestaurant.query.model.RestaurantOrderItemEmbedable
import com.drestaurant.query.repository.RestaurantOrderRepository
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.model.RestaurantOrderState
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurantorder")
internal class RestaurantOrderHandler(private val repository: RestaurantOrderRepository, private val restaurantRepository: RestaurantRepository, private val messagingTemplate: SimpMessageSendingOperations) {

    @EventHandler
    @AllowReplay(true)
    fun handle(event: RestaurantOrderCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        val restaurantOrderItems = java.util.ArrayList<RestaurantOrderItemEmbedable>()
        for (item in event.lineItems) {
            val restaurantOrderItem = RestaurantOrderItemEmbedable(item.menuItemId, item.name, item.quantity)
            restaurantOrderItems.add(restaurantOrderItem)
        }
        val restaurantEntity = restaurantRepository.findById(event.restaurantId).get()
        val record = RestaurantOrderEntity(event.aggregateIdentifier, aggregateVersion, restaurantOrderItems, restaurantEntity, RestaurantOrderState.CREATED)
        repository.save(record)
        broadcastUpdates()
    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: RestaurantOrderPreparedEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier).get()
        record.state = RestaurantOrderState.PREPARED
        repository.save(record)
        broadcastUpdates()
    }

    @ResetHandler // Will be called before replay/reset starts. Do pre-reset logic, like clearing out the Projection table
    fun onReset() {
        repository.deleteAll()
    }

    private fun broadcastUpdates() {
        messagingTemplate.convertAndSend("/topic/restaurants/orders.updates", repository.findAll())
    }

}
