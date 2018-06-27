package com.drestaurant.query.handler

import com.drestaurant.query.model.MenuItemEmbedable
import com.drestaurant.query.model.RestaurantEntity
import com.drestaurant.query.model.RestaurantMenuEmbedable
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import com.drestaurant.restaurant.domain.model.MenuItem
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.ArrayList

@ProcessingGroup("default")
@Component
internal class RestaurantEventHandler @Autowired constructor(private val repository: RestaurantRepository) {

    @EventHandler
    fun handle(event: RestaurantCreatedEvent, @SequenceNumber aggregateVersion: Long) {

        val menuItems = ArrayList<MenuItemEmbedable>()
        for (item in event.menu.menuItems) {
            val menuItem = MenuItemEmbedable(item.id, item.name, item.price.amount)
            menuItems.add(menuItem)
        }
        val menu = RestaurantMenuEmbedable(menuItems, event.menu.menuVersion);
        repository.save(RestaurantEntity(event.aggregateIdentifier, aggregateVersion, event.name, menu))
    }

}
