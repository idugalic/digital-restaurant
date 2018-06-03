package com.drestaurant.restaurant.domain

import org.axonframework.eventhandling.EventBus
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal open class SpringRestaurantConfiguration {

    open @Bean
    fun restaurantCommandHandler(axonConfiguration: AxonConfiguration, eventBus: EventBus): RestaurantCommandHandler {
        return RestaurantCommandHandler(axonConfiguration.repository(Restaurant::class.java), eventBus)
    }
}
