package com.drestaurant.restaurant.domain

import org.axonframework.eventhandling.EventBus
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
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

    @Bean("restaurantSnapshotTriggerDefinition")
    fun restaurantSnapshotTriggerDefinition(snapshotter: Snapshotter): SnapshotTriggerDefinition {
        return EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdRestaurant)
    }

    @Bean("restaurantOrderSnapshotTriggerDefinition")
    fun restaurantOrderSnapshotTriggerDefinition(snapshotter: Snapshotter): SnapshotTriggerDefinition {
        return EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdRestaurantOrder)
    }

}
