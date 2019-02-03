package com.drestaurant.query.handler

import com.drestaurant.query.api.model.*
import com.drestaurant.query.model.RestaurantEntity
import com.drestaurant.query.model.RestaurantMenuEmbedable
import com.drestaurant.query.model.RestaurantOrderEntity
import com.drestaurant.query.model.RestaurantOrderItemEmbedable
import com.drestaurant.query.repository.RestaurantOrderRepository
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderState
import com.drestaurant.restaurant.query.api.FindRestaurantOrderQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.SequenceNumber
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurantorder")
internal class RestaurantOrderHandler(private val repository: RestaurantOrderRepository, private val restaurantRepository: RestaurantRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    private fun convert(record: RestaurantOrderEntity): RestaurantOrderModel = RestaurantOrderModel(record.id, record.aggregateVersion, record.lineItems.map { RestaurantOrderItemModel(it.menuId, it.name, it.quantity) }, convert(record.restaurant!!), record.state)
    private fun convert(record: RestaurantMenuEmbedable): RestaurantMenuModel = RestaurantMenuModel(record.menuItems.map { MenuItemModel(it.menuId, it.name, it.price) }, record.menuVersion)
    private fun convert(record: RestaurantEntity): RestaurantModel = RestaurantModel(record.id, record.aggregateVersion, record.name, convert(record.menu), emptyList())

    @EventHandler
    /* It is possible to allow or prevent some handlers from being replayed/reset */
    @AllowReplay(true)
    fun handle(event: RestaurantOrderCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        val restaurantOrderItems = java.util.ArrayList<RestaurantOrderItemEmbedable>()
        for (item in event.lineItems) {
            val restaurantOrderItem = RestaurantOrderItemEmbedable(item.menuItemId, item.name, item.quantity)
            restaurantOrderItems.add(restaurantOrderItem)
        }
        val restaurantEntity = restaurantRepository.findById(event.aggregateIdentifier.identifier).orElseThrow { UnsupportedOperationException("Restaurant with id '" + event.aggregateIdentifier + "' not found") }
        val record = RestaurantOrderEntity(event.restaurantOrderId.identifier, aggregateVersion, restaurantOrderItems, restaurantEntity, RestaurantOrderState.CREATED)
        repository.save(record)
    }

    @EventHandler
    /* It is possible to allow or prevent some handlers from being replayed/reset */
    @AllowReplay(true)
    fun handle(event: RestaurantOrderPreparedEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier.identifier).orElseThrow { UnsupportedOperationException("Restaurant order with id '" + event.aggregateIdentifier + "' not found") }
        record.state = RestaurantOrderState.PREPARED
        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantOrderQuery, but only if the restaurant order id matches. */
        queryUpdateEmitter.emit(
                FindRestaurantOrderQuery::class.java,
                { query -> query.restaurantOrderId == event.aggregateIdentifier },
                convert(record)
        )
    }

    /* Will be called before replay/reset starts. Do pre-reset logic, like clearing out the Projection table */
    @ResetHandler
    fun onReset() = repository.deleteAll()

    @QueryHandler
    fun handle(query: FindRestaurantOrderQuery): RestaurantOrderModel = convert(repository.findById(query.restaurantOrderId.identifier).orElseThrow { UnsupportedOperationException("Restaurant order with id '" + query.restaurantOrderId + "' not found") })
}
