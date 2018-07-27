package com.drestaurant.restaurant.domain

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
internal class SpringRestaurantConfiguration {

    @Bean
    fun restaurantCommandHandler(axonConfiguration: AxonConfiguration, eventBus: EventBus): RestaurantCommandHandler {
        return RestaurantCommandHandler(axonConfiguration.repository(Restaurant::class.java), eventBus)
    }

    @Value("\${axon.snapshot.trigger.treshold.restaurant}")
    private val snapshotTriggerTresholdRestaurant: Int = 100

    @Value("\${axon.snapshot.trigger.treshold.restaurantorder}")
    private val snapshotTriggerTresholdRestaurantOrder: Int = 100

    @Bean("restaurantAggregateRepository")
    fun orderRepository(eventStore: EventStore, snapshotter: Snapshotter): EventSourcingRepository<Restaurant> {
        return EventSourcingRepository<Restaurant>(
                Restaurant::class.java,
                eventStore,
                EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdRestaurant))
    }

    @Bean("restaurantOrderAggregateRepository")
    fun restaurantOrderRepository(eventStore: EventStore, snapshotter: Snapshotter): EventSourcingRepository<RestaurantOrder> {
        return EventSourcingRepository<RestaurantOrder>(
                RestaurantOrder::class.java,
                eventStore,
                EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdRestaurantOrder))
    }

}
