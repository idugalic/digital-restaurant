package com.drestaurant.query.handler

import com.drestaurant.query.FindAllCouriersQuery
import com.drestaurant.query.FindAllRestaurantsQuery
import com.drestaurant.query.FindCourierQuery
import com.drestaurant.query.FindRestaurantQuery
import com.drestaurant.query.model.MenuItemEmbedable
import com.drestaurant.query.model.RestaurantEntity
import com.drestaurant.query.model.RestaurantMenuEmbedable
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
internal class RestaurantEventHandler @Autowired constructor(private val repository: RestaurantRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    @EventHandler
    fun handle(event: RestaurantCreatedEvent, @SequenceNumber aggregateVersion: Long) {

        val menuItems = ArrayList<MenuItemEmbedable>()
        for (item in event.menu.menuItems) {
            val menuItem = MenuItemEmbedable(item.id, item.name, item.price.amount)
            menuItems.add(menuItem)
        }
        val menu = RestaurantMenuEmbedable(menuItems, event.menu.menuVersion)

        val record = RestaurantEntity(event.aggregateIdentifier, aggregateVersion, event.name, menu)
        repository.save(record);

        /* sending it to subscription queries of type FindRestaurantQuery, but only if the restaurant id matches. */
        queryUpdateEmitter.emit(
                FindRestaurantQuery::class.java,
                { query -> query.restaurantId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllRestaurants. */
        queryUpdateEmitter.emit(
                FindAllRestaurantsQuery::class.java,
                { query -> true },
                record
        )
    }

}
