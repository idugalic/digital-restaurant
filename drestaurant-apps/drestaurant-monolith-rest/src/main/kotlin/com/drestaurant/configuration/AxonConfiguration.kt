package com.drestaurant.configuration

import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfiguration {

    @Bean
    fun snapshotterFactoryBean(): SpringAggregateSnapshotterFactoryBean {
        return SpringAggregateSnapshotterFactoryBean()
    }

//    @Bean
//    fun axonHubEventStore(configuration: AxonHubConfiguration, platformConnectionManager: PlatformConnectionManager, serializer: Serializer): AxonHubEventStore {
//        return AxonHubEventStore(configuration, platformConnectionManager, serializer, NoOpEventUpcaster.INSTANCE)
//    }

}
