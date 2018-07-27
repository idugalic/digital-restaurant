package com.drestaurant.query.handler

import com.drestaurant.customer.domain.api.CustomerCreatedEvent
import com.drestaurant.query.FindAllCustomersQuery
import com.drestaurant.query.FindCustomerQuery
import com.drestaurant.query.model.CustomerEntity
import com.drestaurant.query.repository.CustomerRepository
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
internal class CustomerHandler(private val repository: CustomerRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    @EventHandler
    fun handle(event: CustomerCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        /* saving the record in our read/query model. */
        val record = CustomerEntity(event.aggregateIdentifier, aggregateVersion, event.name.firstName, event.name.lastName, event.orderLimit.amount)
        repository.save(record)

        /* sending it to subscription queries of type FindCustomerQuery, but only if the customer id matches. */
        queryUpdateEmitter.emit(
                FindCustomerQuery::class.java,
                { query -> query.customerId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllCustomers. */
        queryUpdateEmitter.emit(
                FindAllCustomersQuery::class.java,
                { query -> true },
                record
        )
    }

    @QueryHandler
    fun handle(query: FindCustomerQuery): CustomerEntity {
        return repository.findById(query.customerId).get()
    }

    @QueryHandler
    fun handle(query: FindAllCustomersQuery): MutableIterable<CustomerEntity> {
        return repository.findAll()
    }

}
