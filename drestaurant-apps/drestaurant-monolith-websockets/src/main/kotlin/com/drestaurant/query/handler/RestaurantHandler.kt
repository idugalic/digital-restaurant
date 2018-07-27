package com.drestaurant.query.handler

import com.drestaurant.query.model.MenuItemEmbedable
import com.drestaurant.query.model.RestaurantEntity
import com.drestaurant.query.model.RestaurantMenuEmbedable
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList

@Component
internal class RestaurantHandler(private val repository: RestaurantRepository, private val messagingTemplate: SimpMessageSendingOperations) {

    @EventHandler
    fun handle(event: RestaurantCreatedEvent, @SequenceNumber aggregateVersion: Long) {

        val menuItems = ArrayList<MenuItemEmbedable>()
        for (item in event.menu.menuItems) {
            val menuItem = MenuItemEmbedable(item.id, item.name, item.price.amount)
            menuItems.add(menuItem)
        }
        val menu = RestaurantMenuEmbedable(menuItems, event.menu.menuVersion)
        repository.save(RestaurantEntity(event.aggregateIdentifier, aggregateVersion, event.name, menu, Collections.emptyList()))
        broadcastUpdates()
    }

    private fun broadcastUpdates() {
        messagingTemplate.convertAndSend("/topic/restaurants.updates", repository.findAll())
    }
}
