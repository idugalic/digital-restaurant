package com.drestaurant.query.handler

import com.drestaurant.courier.domain.api.CourierCreatedEvent
import com.drestaurant.courier.query.api.FindCourierQuery
import com.drestaurant.courier.query.api.model.CourierModel
import com.drestaurant.query.model.CourierEntity
import com.drestaurant.query.repository.CourierRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.SequenceNumber
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component


@Component
@ProcessingGroup("courier")
internal class CourierHandler(private val repository: CourierRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    private fun convert(record: CourierEntity): CourierModel = CourierModel(record.id, record.aggregateVersion, record.firstName, record.lastName, record.maxNumberOfActiveOrders, emptyList())

    @EventHandler
    /* It is possible to allow or prevent some handlers from being replayed/reset */
    @AllowReplay(true)
    fun handle(event: CourierCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        /* saving the record in our read/query model. */
        val record = CourierEntity(event.aggregateIdentifier.identifier, aggregateVersion, event.name.firstName, event.name.lastName, event.maxNumberOfActiveOrders, emptyList())
        repository.save(record)

        /* sending it to subscription queries of type FindCourierQuery, but only if the courier id matches. */
        queryUpdateEmitter.emit(
                FindCourierQuery::class.java,
                { query -> query.courierId == event.aggregateIdentifier },
                convert(record)
        )
    }

    /* Will be called before replay/reset starts. Do pre-reset logic, like clearing out the Projection table */
    @ResetHandler
    fun onReset() = repository.deleteAll()

    @QueryHandler
    fun handle(query: FindCourierQuery): CourierModel = convert(repository.findById(query.courierId.identifier).orElseThrow { UnsupportedOperationException("Courier with id '" + query.courierId + "' not found") })
}

