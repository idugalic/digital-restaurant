package com.drestaurant.configuration

import org.axonframework.kafka.eventhandling.DefaultKafkaMessageConverter
import org.axonframework.serialization.Serializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfiguration {

    // https://github.com/AxonFramework/AxonFramework/issues/710
    @Bean
    fun kafkaMessageConverter(eventSerializer: Serializer) = DefaultKafkaMessageConverter(eventSerializer)
}