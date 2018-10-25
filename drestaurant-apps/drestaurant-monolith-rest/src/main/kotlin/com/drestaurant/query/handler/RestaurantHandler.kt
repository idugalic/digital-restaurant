package com.drestaurant.query.handler

import com.drestaurant.query.FindAllRestaurantsQuery
import com.drestaurant.query.FindRestaurantQuery
import com.drestaurant.query.model.MenuItemEmbedable
import com.drestaurant.query.model.RestaurantEntity
import com.drestaurant.query.model.RestaurantMenuEmbedable
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.*
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList

@Component
@ProcessingGroup("restaurant")
internal class RestaurantHandler(private val repository: RestaurantRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    @EventHandler
    /* It is possible to allow or prevent some handlers from being replayed/reset */
    @AllowReplay(true)
    fun handle(event: RestaurantCreatedEvent, @SequenceNumber aggregateVersion: Long, replayStatus: ReplayStatus) {

        val menuItems = ArrayList<MenuItemEmbedable>()
        for (item in event.menu.menuItems) {
            val menuItem = MenuItemEmbedable(item.id, item.name, item.price.amount)
            menuItems.add(menuItem)
        }
        val menu = RestaurantMenuEmbedable(menuItems, event.menu.menuVersion)

        val record = RestaurantEntity(event.aggregateIdentifier, aggregateVersion, event.name, menu, Collections.emptyList())
        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantQuery, but only if the restaurant id matches. */
        queryUpdateEmitter.emit(
                FindRestaurantQuery::class.java,
                { query -> query.restaurantId == event.aggregateIdentifier },
                record
        )

        /* sending it to subscription queries of type FindAllRestaurants. */
        queryUpdateEmitter.emit(
                FindAllRestaurantsQuery::class.java,
                { true },
                record
        )
    }

    /* Will be called before replay/reset starts. Do pre-reset logic, like clearing out the Projection table */
    @ResetHandler
    fun onReset() = repository.deleteAll()

    @QueryHandler
    fun handle(query: FindRestaurantQuery): RestaurantEntity = repository.findById(query.restaurantId).orElseThrow { UnsupportedOperationException("Restaurant with id '" + query.restaurantId + "' not found") }

    @QueryHandler
    fun handle(query: FindAllRestaurantsQuery): MutableIterable<RestaurantEntity> = repository.findAll()
}
