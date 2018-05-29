package com.drestaurant.courier.domain;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CourierConfiguration {
	
	@Bean
    public CourierCommandHandler courierCommandHandler(AxonConfiguration axonConfiguration, EventBus eventBus) {
        return new CourierCommandHandler(axonConfiguration.repository(Courier.class), eventBus);
    }

}
