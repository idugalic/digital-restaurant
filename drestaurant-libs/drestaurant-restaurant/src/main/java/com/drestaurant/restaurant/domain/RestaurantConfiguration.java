package com.drestaurant.restaurant.domain;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RestaurantConfiguration {
	
	@Bean
    public RestaurantCommandHandler restaurantCommandHandler(AxonConfiguration axonConfiguration, EventBus eventBus) {
        return new RestaurantCommandHandler(axonConfiguration.repository(Restaurant.class), eventBus);
    }

}
