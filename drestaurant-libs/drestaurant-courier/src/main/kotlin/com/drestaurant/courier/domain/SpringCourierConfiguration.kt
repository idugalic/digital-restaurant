package com.drestaurant.courier.domain

import org.axonframework.eventhandling.EventBus
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class SpringCourierConfiguration {

    @Bean
    fun courierCommandHandler(axonConfiguration: AxonConfiguration, eventBus: EventBus) = CourierCommandHandler(axonConfiguration.repository(Courier::class.java), eventBus)

    @Value("\${axon.snapshot.trigger.treshold.courier}")
    private val snapshotTriggerTresholdCourier: Int = 100

    @Value("\${axon.snapshot.trigger.treshold.courierorder}")
    private val snapshotTriggerTresholdCourierOrder: Int = 100


    @Bean("courierSnapshotTriggerDefinition")
    fun courierSnapshotTriggerDefinition(snapshotter: Snapshotter) = EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdCourier)

    @Bean("courierOrderSnapshotTriggerDefinition")
    fun courierOrderSnapshotTriggerDefinition(snapshotter: Snapshotter) = EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdCourierOrder)
}
