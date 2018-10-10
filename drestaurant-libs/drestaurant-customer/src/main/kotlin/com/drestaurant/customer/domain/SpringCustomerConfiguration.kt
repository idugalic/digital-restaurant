package com.drestaurant.customer.domain

import org.axonframework.eventhandling.EventBus
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class SpringCustomerConfiguration {

    @Bean
    fun customerCommandHandler(axonConfiguration: AxonConfiguration, eventBus: EventBus) = CustomerCommandHandler(axonConfiguration.repository(Customer::class.java), eventBus)

    @Value("\${axon.snapshot.trigger.treshold.customer}")
    private val snapshotTriggerTresholdCustomer: Int = 100

    @Value("\${axon.snapshot.trigger.treshold.customerorder}")
    private val snapshotTriggerTresholdCustomerOrder: Int = 100

    @Bean("customerSnapshotTriggerDefinition")
    fun customerSnapshotTriggerDefinition(snapshotter: Snapshotter) = EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdCustomer)

    @Bean("customerOrderSnapshotTriggerDefinition")
    fun customerOrderSnapshotTriggerDefinition(snapshotter: Snapshotter) = EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerTresholdCustomerOrder)

}
