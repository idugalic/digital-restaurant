package com.drestaurant.configuration

import com.drestaurant.order.domain.OrderSaga
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource
import org.axonframework.commandhandling.CommandBus
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.SagaConfiguration
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

// Check the src/main/java/com/drestaurant/configuration/AMQPMessageSourceConfiguration.java
//    @Bean
//    fun amqpMessageSource(serializer: Serializer): SpringAMQPMessageSource {
//        return object : SpringAMQPMessageSource(serializer) {
//            @RabbitListener(queues = arrayOf("\${spring.application.sagaqueue}"))
//            @Throws(Exception::class)
//            override fun onMessage(message: Message, channel: Channel) {
//                super.onMessage(message, channel)
//            }
//        }
//    }

    @Autowired
    fun registerInterceptors(commandBus: CommandBus, transactionManager: TransactionManager) {
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
    }


    @Bean
    fun snapshotterFactoryBean() = SpringAggregateSnapshotterFactoryBean()

    @Bean
    fun orderSagaConfiguration(amqpMessageSource: SpringAMQPMessageSource) = SagaConfiguration.subscribingSagaManager<OrderSaga>(OrderSaga::class.java, { it -> amqpMessageSource })

}