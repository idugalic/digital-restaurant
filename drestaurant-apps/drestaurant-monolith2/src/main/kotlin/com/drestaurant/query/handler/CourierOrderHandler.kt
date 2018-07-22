package com.drestaurant.query.handler

import com.drestaurant.courier.domain.api.CourierOrderAssignedEvent
import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent
import com.drestaurant.courier.domain.model.CourierOrderState
import com.drestaurant.query.FindAllCourierOrdersQuery
import com.drestaurant.query.FindAllRestaurantOrdersQuery
import com.drestaurant.query.FindCourierOrderQuery
import com.drestaurant.query.FindRestaurantOrderQuery
import com.drestaurant.query.model.CourierOrderEntity
import com.drestaurant.query.model.RestaurantOrderEntity
import com.drestaurant.query.model.RestaurantOrderItemEmbedable
import com.drestaurant.query.repository.CourierOrderRepository
import com.drestaurant.query.repository.CourierRepository
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
internal class CourierOrderHandler @Autowired constructor(private val repository: CourierOrderRepository, private val courierRepository: CourierRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    @EventHandler
    fun handle(event: CourierOrderCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        val record = CourierOrderEntity(event.aggregateIdentifier, aggregateVersion, null, CourierOrderState.CREATED)
        repository.save(record)
    }

    @EventHandler
    fun handle(event: CourierOrderAssignedEvent, @SequenceNumber aggregateVersion: Long) {
        val courierEntity = courierRepository.findById(event.courierId).get()
        val record = repository.findById(event.aggregateIdentifier).get()
        record.state = CourierOrderState.ASSIGNED
        record.courier = courierEntity
        repository.save(record)

        /* sending it to subscription queries of type FindCourierOrderQuery, but only if the courier order id matches. */
        queryUpdateEmitter.emit(
                FindCourierOrderQuery::class.java,
                { query -> query.courierOrderId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllCourierOrders. */
        queryUpdateEmitter.emit(
                FindAllCourierOrdersQuery::class.java,
                { query -> true },
                record
        )
    }

    @EventHandler
    fun handle(event: CourierOrderDeliveredEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier).get()
        record.state = CourierOrderState.DELIVERED
        repository.save(record)

        /* sending it to subscription queries of type FindCourierOrderQuery, but only if the courier order id matches. */
        queryUpdateEmitter.emit(
                FindCourierOrderQuery::class.java,
                { query -> query.courierOrderId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllCourierOrders. */
        queryUpdateEmitter.emit(
                FindAllCourierOrdersQuery::class.java,
                { query -> true },
                record
        )
    }

    @QueryHandler
    fun handle(query: FindCourierOrderQuery): CourierOrderEntity {
        return repository.findById(query.courierOrderId).get()
    }

    @QueryHandler
    fun handle(query: FindAllCourierOrdersQuery): MutableIterable<CourierOrderEntity> {
        return repository.findAll()
    }

}
