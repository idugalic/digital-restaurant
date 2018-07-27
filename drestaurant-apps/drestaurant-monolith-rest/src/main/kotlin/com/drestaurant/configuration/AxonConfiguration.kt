package com.drestaurant.configuration

import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.monitoring.NoOpMessageMonitor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfiguration {

    @Bean
    fun commandBus(transactionManager: TransactionManager): CommandBus {
        val commandBus = SimpleCommandBus(transactionManager, NoOpMessageMonitor.INSTANCE)
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
        return commandBus
    }
}
