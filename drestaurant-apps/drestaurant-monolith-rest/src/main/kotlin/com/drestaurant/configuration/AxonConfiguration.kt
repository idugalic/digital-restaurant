package com.drestaurant.configuration

import io.axoniq.axonhub.client.AxonHubConfiguration
import io.axoniq.axonhub.client.PlatformConnectionManager
import io.axoniq.axonhub.client.event.axon.AxonHubEventStore
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.upcasting.event.EventUpcaster
import org.axonframework.serialization.upcasting.event.NoOpEventUpcaster
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfiguration {

    @Bean
    fun snapshotterFactoryBean(): SpringAggregateSnapshotterFactoryBean {
        return SpringAggregateSnapshotterFactoryBean()
    }

    @Bean
    fun axonHubEventStore(configuration: AxonHubConfiguration, platformConnectionManager: PlatformConnectionManager, serializer: Serializer): AxonHubEventStore {
        return AxonHubEventStore(configuration, platformConnectionManager, serializer, NoOpEventUpcaster.INSTANCE)
    }

}
