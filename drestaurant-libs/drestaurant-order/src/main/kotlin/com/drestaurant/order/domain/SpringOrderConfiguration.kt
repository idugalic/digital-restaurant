package com.drestaurant.order.domain

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.eventsourcing.eventstore.EventStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class SpringOrderConfiguration {

    @Value("\${axon.snapshot.trigger.treshold.order}")
    private val snapshotTriggerTresholdOrder: Int = 100


    // Say to Axon that your Aggregate should snasphot, by overridong the behavior. Put the name to the bean
    // orderAggregateRepository , Axon create automaticaly a bean with AGGREGATE_NAME + Repository
    @Bean("orderAggregateRepository")
    fun orderRepository(eventStore: EventStore, snapshotter: Snapshotter): EventSourcingRepository<Order> {
        return EventSourcingRepository<Order>(
                Order::class.java,
                eventStore,
                EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdOrder))
    }
}
