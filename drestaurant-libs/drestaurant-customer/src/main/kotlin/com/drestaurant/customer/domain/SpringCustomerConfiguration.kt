package com.drestaurant.customer.domain

import org.axonframework.eventhandling.EventBus
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class SpringCustomerConfiguration {

    @Bean
    fun customerCommandHandler(axonConfiguration: AxonConfiguration, eventBus: EventBus): CustomerCommandHandler {
        return CustomerCommandHandler(axonConfiguration.repository(Customer::class.java), eventBus)
    }

    @Value("\${axon.snapshot.trigger.treshold.customer}")
    private val snapshotTriggerTresholdCustomer: Int = 100

    @Value("\${axon.snapshot.trigger.treshold.customerorder}")
    private val snapshotTriggerTresholdCustomerOrder: Int = 100

    @Bean("customerAggregateRepository")
    fun orderRepository(eventStore: EventStore, snapshotter: Snapshotter): EventSourcingRepository<Customer> {
        return EventSourcingRepository<Customer>(
                Customer::class.java,
                eventStore,
                EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdCustomer))
    }

    @Bean("customerOrderAggregateRepository")
    fun customerOrderRepository(eventStore: EventStore, snapshotter: Snapshotter): EventSourcingRepository<CustomerOrder> {
        return EventSourcingRepository<CustomerOrder>(
                CustomerOrder::class.java,
                eventStore,
                EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdCustomerOrder))
    }

}
