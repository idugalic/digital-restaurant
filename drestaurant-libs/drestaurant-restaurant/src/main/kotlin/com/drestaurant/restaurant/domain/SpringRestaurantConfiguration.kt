package com.drestaurant.restaurant.domain

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class SpringRestaurantConfiguration {

    @Value("\${axon.snapshot.trigger.treshold.restaurant}")
    private val snapshotTriggerTresholdRestaurant: Int = 100

    @Value("\${axon.snapshot.trigger.treshold.restaurantorder}")
    private val snapshotTriggerTresholdRestaurantOrder: Int = 100

    @Bean("restaurantSnapshotTriggerDefinition")
    fun restaurantSnapshotTriggerDefinition(snapshotter: Snapshotter) = EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdRestaurant)

    @Bean("restaurantOrderSnapshotTriggerDefinition")
    fun restaurantOrderSnapshotTriggerDefinition(snapshotter: Snapshotter) = EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdRestaurantOrder)

}
