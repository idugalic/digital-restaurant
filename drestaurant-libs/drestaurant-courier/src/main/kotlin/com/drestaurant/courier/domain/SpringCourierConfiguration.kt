package com.drestaurant.courier.domain

import org.axonframework.eventhandling.EventBus
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class SpringCourierConfiguration {

    @Bean
    fun courierCommandHandler(axonConfiguration: AxonConfiguration, eventBus: EventBus): CourierCommandHandler {
        return CourierCommandHandler(axonConfiguration.repository(Courier::class.java), eventBus)
    }

}
