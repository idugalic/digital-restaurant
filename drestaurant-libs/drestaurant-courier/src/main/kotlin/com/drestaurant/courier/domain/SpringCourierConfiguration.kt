package com.drestaurant.courier.domain

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
internal class SpringCourierConfiguration {

    @Bean
    fun courierCommandHandler(axonConfiguration: AxonConfiguration, eventBus: EventBus): CourierCommandHandler {
        return CourierCommandHandler(axonConfiguration.repository(Courier::class.java), eventBus)
    }

    @Value("\${axon.snapshot.trigger.treshold.courier}")
    private val snapshotTriggerTresholdCourier: Int = 100

    @Value("\${axon.snapshot.trigger.treshold.courierorder}")
    private val snapshotTriggerTresholdCourierOrder: Int = 100

    @Bean("courierAggregateRepository")
    fun courierRepository(eventStore: EventStore, snapshotter: Snapshotter): EventSourcingRepository<Courier> {
        return EventSourcingRepository<Courier>(
                Courier::class.java,
                eventStore,
                EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdCourier))
    }

    @Bean("courierOrderAggregateRepository")
    fun courierOrderRepository(eventStore: EventStore, snapshotter: Snapshotter): EventSourcingRepository<CourierOrder> {
        return EventSourcingRepository<CourierOrder>(
                CourierOrder::class.java,
                eventStore,
                EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdCourierOrder))
    }

}
