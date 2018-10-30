package com.drestaurant.query.handler

import com.drestaurant.query.FindAllRestaurantOrdersQuery
import com.drestaurant.query.FindRestaurantOrderQuery
import com.drestaurant.query.model.RestaurantOrderEntity
import com.drestaurant.query.model.RestaurantOrderItemEmbedable
import com.drestaurant.query.repository.RestaurantOrderRepository
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderState
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.SequenceNumber
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurant")
internal class RestaurantOrderHandler(private val repository: RestaurantOrderRepository, private val restaurantRepository: RestaurantRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    @EventHandler
    @AllowReplay(false)
    fun handle(event: RestaurantOrderCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        val restaurantOrderItems = java.util.ArrayList<RestaurantOrderItemEmbedable>()
        for (item in event.lineItems) {
            val restaurantOrderItem = RestaurantOrderItemEmbedable(item.menuItemId, item.name, item.quantity)
            restaurantOrderItems.add(restaurantOrderItem)
        }
        val restaurantEntity = restaurantRepository.findById(event.restaurantId).orElseThrow { UnsupportedOperationException("Restaurant with id '${event.restaurantId}' not found") }
        val record = RestaurantOrderEntity(event.aggregateIdentifier, aggregateVersion, restaurantOrderItems, restaurantEntity, RestaurantOrderState.CREATED)
        repository.save(record)
    }

    @EventHandler
    @AllowReplay(false)
    fun handle(event: RestaurantOrderPreparedEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier).orElseThrow { UnsupportedOperationException("Restaurant order with id '${event.aggregateIdentifier}' not found") }
        record.state = RestaurantOrderState.PREPARED
        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantOrderQuery, but only if the restaurant order id matches. */
        queryUpdateEmitter.emit(
                FindRestaurantOrderQuery::class.java,
                { query -> query.restaurantOrderId == event.aggregateIdentifier },
                record
        )

        /* sending it to subscription queries of type FindAllRestaurantOrders. */
        queryUpdateEmitter.emit(
                FindAllRestaurantOrdersQuery::class.java,
                { true },
                record
        )
    }

    @QueryHandler
    fun handle(query: FindRestaurantOrderQuery): RestaurantOrderEntity = repository.findById(query.restaurantOrderId).orElseThrow { UnsupportedOperationException("Restaurant order with id '${query.restaurantOrderId}' not found") }

    @QueryHandler
    fun handle(query: FindAllRestaurantOrdersQuery): MutableIterable<RestaurantOrderEntity> = repository.findAll()

}
