package com.drestaurant.configuration

import com.drestaurant.order.domain.OrderSaga
import org.axonframework.commandhandling.CommandBus
import org.axonframework.config.SagaConfiguration
import org.axonframework.kafka.eventhandling.DefaultKafkaMessageConverter
import org.axonframework.kafka.eventhandling.consumer.KafkaMessageSource
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.serialization.Serializer
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfiguration {

    @Autowired
    fun registerInterceptors(commandBus: CommandBus) {
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
    }

    @Bean
    fun snapshotterFactoryBean() = SpringAggregateSnapshotterFactoryBean()

    // https://github.com/AxonFramework/AxonFramework/issues/710
    @Bean
    fun kafkaMessageConverter(eventSerializer: Serializer) = DefaultKafkaMessageConverter(eventSerializer)

    @Bean
    fun orderSagaConfiguration(kafkaMessageSource: KafkaMessageSource) = SagaConfiguration.trackingSagaManager<OrderSaga>(OrderSaga::class.java) { kafkaMessageSource }


}

