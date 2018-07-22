package com.drestaurant.query.handler

import com.drestaurant.query.FindAllRestaurantOrdersQuery
import com.drestaurant.query.FindRestaurantOrderQuery
import com.drestaurant.query.model.RestaurantOrderEntity
import com.drestaurant.query.model.RestaurantOrderItemEmbedable
import com.drestaurant.query.repository.RestaurantOrderRepository
import com.drestaurant.query.repository.RestaurantRepository
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.model.RestaurantOrderState
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class RestaurantOrderHandler @Autowired constructor(private val repository: RestaurantOrderRepository, private val restaurantRepository: RestaurantRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    @EventHandler
    fun handle(event: RestaurantOrderCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        val restaurantOrderItems = java.util.ArrayList<RestaurantOrderItemEmbedable>()
        for (item in event.lineItems) {
            val restaurantOrderItem = RestaurantOrderItemEmbedable(item.menuItemId, item.name, item.quantity)
            restaurantOrderItems.add(restaurantOrderItem)
        }
        val restaurantEntity = restaurantRepository.findById(event.restaurantId).get()
        val record = RestaurantOrderEntity(event.aggregateIdentifier, aggregateVersion, restaurantOrderItems, restaurantEntity, RestaurantOrderState.CREATED)
        repository.save(record)
    }

    @EventHandler
    fun handle(event: RestaurantOrderPreparedEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier).get()
        record.state = RestaurantOrderState.PREPARED
        repository.save(record)

        /* sending it to subscription queries of type FindRestaurantOrderQuery, but only if the restaurant id matches. */
        queryUpdateEmitter.emit(
                FindRestaurantOrderQuery::class.java,
                { query -> query.restaurantOrderId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllRestaurantOrders. */
        queryUpdateEmitter.emit(
                FindAllRestaurantOrdersQuery::class.java,
                { query -> true },
                record
        )
    }

    @QueryHandler
    fun handle(query: FindRestaurantOrderQuery): RestaurantOrderEntity {
        return repository.findById(query.restaurantOrderId).get()
    }

    @QueryHandler
    fun handle(query: FindAllRestaurantOrdersQuery): MutableIterable<RestaurantOrderEntity> {
        return repository.findAll()
    }

}
