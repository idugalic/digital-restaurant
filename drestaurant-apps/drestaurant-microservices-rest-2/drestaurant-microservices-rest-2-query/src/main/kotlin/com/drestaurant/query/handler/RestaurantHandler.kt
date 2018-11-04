package com.drestaurant.query.handler

import com.drestaurant.query.api.model.MenuItemModel
import com.drestaurant.query.api.model.RestaurantMenuModel
import com.drestaurant.query.api.model.RestaurantModel
import com.drestaurant.query.model.MenuItemEmbedable
import com.drestaurant.query.model.RestaurantEntity
import com.drestaurant.query.model.RestaurantMenuEmbedable
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import com.drestaurant.restaurant.query.api.FindRestaurantQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.*
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurant")
internal class RestaurantHandler(private val repository: RestaurantRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    private fun convert(record: RestaurantMenuEmbedable): RestaurantMenuModel = RestaurantMenuModel(record.menuItems.map { MenuItemModel(it.menuId, it.name, it.price) }, record.menuVersion)
    private fun convert(record: RestaurantEntity): RestaurantModel = RestaurantModel(record.id, record.aggregateVersion, record.name, convert(record.menu), emptyList())

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

        val record = RestaurantEntity(event.aggregateIdentifier.identifier, aggregateVersion, event.name, menu, emptyList())
        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantQuery, but only if the restaurant id matches. */
        queryUpdateEmitter.emit(
                FindRestaurantQuery::class.java,
                { query -> query.restaurantId == event.aggregateIdentifier },
                convert(record)
        )
    }

    /* Will be called before replay/reset starts. Do pre-reset logic, like clearing out the Projection table */
    @ResetHandler
    fun onReset() = repository.deleteAll()

    @QueryHandler
    fun handle(query: FindRestaurantQuery): RestaurantModel = convert(repository.findById(query.restaurantId.identifier).orElseThrow { UnsupportedOperationException("Restaurant with id '" + query.restaurantId + "' not found") })
}
