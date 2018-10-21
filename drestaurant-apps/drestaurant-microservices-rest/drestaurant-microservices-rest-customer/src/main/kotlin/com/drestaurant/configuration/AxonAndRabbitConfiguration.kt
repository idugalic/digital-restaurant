package com.drestaurant.configuration

import org.axonframework.commandhandling.CommandBus
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableRabbit
class AxonAndRabbitConfiguration {

    // ########## RabbitMQ ###########

    @Value("\${spring.rabbitmq.hostname}")
    private val hostname: String = "localhost"

    @Value("\${spring.rabbitmq.username}")
    private val username: String = "guest"

    @Value("\${spring.rabbitmq.password}")
    private val password: String = "guest"

    @Value("\${axon.amqp.exchange}")
    private val exchangeName: String = "Axon.EventBus"

    @Value("\${spring.application.sagaqueue}")
    private val sagaQueueName: String? = null

    @Bean
    fun connectionFactory(): ConnectionFactory {
        val connectionFactory = CachingConnectionFactory(hostname)
        connectionFactory.username = username
        connectionFactory.setPassword(password)
        return connectionFactory
    }

    @Bean
    fun sagaQueue() = Queue(sagaQueueName, true)

    @Bean
    fun eventBusExchange() = FanoutExchange(exchangeName, true, false)

    @Bean
    fun sagaBinding() = Binding(sagaQueueName, Binding.DestinationType.QUEUE, exchangeName, "*.*", null)

    @Autowired
    fun rabbitAdmin(admin: AmqpAdmin, eventBusExchange: FanoutExchange) {
        admin.declareExchange(eventBusExchange)
        admin.declareQueue(sagaQueue())
        admin.declareBinding(sagaBinding())
    }

    // ######### Axon #########
    @Autowired
    fun registerInterceptors(commandBus: CommandBus) {
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
    }

    @Bean
    fun snapshotterFactoryBean() = SpringAggregateSnapshotterFactoryBean()
}
