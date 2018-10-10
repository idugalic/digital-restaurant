package com.drestaurant.order.domain

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class SpringOrderConfiguration {

    @Value("\${axon.snapshot.trigger.treshold.order}")
    private val snapshotTriggerTresholdOrder: Int = 100

    @Bean("orderSnapshotTriggerDefinition")
    fun orderSnapshotTriggerDefinition(snapshotter: Snapshotter) = EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdOrder)
}
