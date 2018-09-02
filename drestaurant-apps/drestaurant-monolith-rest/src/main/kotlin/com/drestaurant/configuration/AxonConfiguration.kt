package com.drestaurant.configuration

import org.axonframework.commandhandling.CommandBus
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AxonConfiguration {

    /* Register a command interceptor */
    @Autowired
    fun registerInterceptors(commandBus: CommandBus) {
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
    }

    @Bean
    fun snapshotterFactoryBean() = SpringAggregateSnapshotterFactoryBean()

//    @Bean
//    fun axonHubEventStore(configuration: AxonHubConfiguration, platformConnectionManager: PlatformConnectionManager, serializer: Serializer): AxonHubEventStore {
//        return AxonHubEventStore(configuration, platformConnectionManager, serializer, NoOpEventUpcaster.INSTANCE)
//    }
}
