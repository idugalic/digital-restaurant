package com.drestaurant.configuration

import com.drestaurant.courier.domain.CourierOrderSaga
import com.drestaurant.customer.domain.CustomerOrderSaga
import com.drestaurant.order.domain.OrderSaga
import com.drestaurant.restaurant.domain.RestaurantOrderSaga
import org.axonframework.commandhandling.CommandBus
import org.axonframework.config.SagaConfiguration
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration
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

    /* Saga configuration */
    @Bean
    fun courierOrderSagaConfiguration() = SagaConfiguration.trackingSagaManager<CourierOrderSaga>(CourierOrderSaga::class.java).configureTrackingProcessor({ it -> TrackingEventProcessorConfiguration.forParallelProcessing(1)})
    @Bean
    fun customerOrderSagaConfiguration() = SagaConfiguration.trackingSagaManager<CustomerOrderSaga>(CustomerOrderSaga::class.java).configureTrackingProcessor({ it -> TrackingEventProcessorConfiguration.forParallelProcessing(1)})
    @Bean
    fun restaurantOrderSagaConfiguration() = SagaConfiguration.trackingSagaManager<RestaurantOrderSaga>(RestaurantOrderSaga::class.java).configureTrackingProcessor({ it -> TrackingEventProcessorConfiguration.forParallelProcessing(1)})
    @Bean
    fun orderSagaConfiguration() = SagaConfiguration.trackingSagaManager<OrderSaga>(OrderSaga::class.java).configureTrackingProcessor({ it -> TrackingEventProcessorConfiguration.forParallelProcessing(1)})

}
