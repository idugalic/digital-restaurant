package com.drestaurant.query.handler

import com.drestaurant.courier.domain.api.CourierCreatedEvent
import com.drestaurant.query.FindAllCouriersQuery
import com.drestaurant.query.FindCourierQuery
import com.drestaurant.query.model.CourierEntity
import com.drestaurant.query.repository.CourierRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
internal class CourierEventHandler @Autowired constructor(private val repository: CourierRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    @EventHandler
    fun handle(event: CourierCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        /* saving the record in our read/query model. */
        val record = CourierEntity(event.aggregateIdentifier, aggregateVersion, event.name.firstName, event.name.lastName, event.maxNumberOfActiveOrders)
        repository.save(record);

        /* sending it to subscription queries of type FindCourierQuery, but only if the courier id matches. */
        queryUpdateEmitter.emit(
                FindCourierQuery::class.java,
                { query -> query.courierId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllCouriers. */
        queryUpdateEmitter.emit(
                FindAllCouriersQuery::class.java,
                { query -> true },
                record
        )
    }

    @QueryHandler
    fun handle(query: FindCourierQuery): CourierEntity {
        return repository.findById(query.courierId).get()
    }

    @QueryHandler
    fun handle(query: FindAllCouriersQuery): MutableIterable<CourierEntity> {
        return repository.findAll()
    }

}

