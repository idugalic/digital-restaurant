package com.drestaurant.customer.domain;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CustomerConfiguration {
	
	@Bean
    public CustomerCommandHandler customerCommandHandler(AxonConfiguration axonConfiguration, EventBus eventBus) {
        return new CustomerCommandHandler(axonConfiguration.repository(Customer.class), eventBus);
    }

}
