package com.drestaurant.customer.domain

import org.axonframework.eventhandling.EventBus
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class SpringCustomerConfiguration {


    @Bean
    fun customerCommandHandler(axonConfiguration: AxonConfiguration, eventBus: EventBus): CustomerCommandHandler {
        return CustomerCommandHandler(axonConfiguration.repository(Customer::class.java), eventBus)
    }

}
