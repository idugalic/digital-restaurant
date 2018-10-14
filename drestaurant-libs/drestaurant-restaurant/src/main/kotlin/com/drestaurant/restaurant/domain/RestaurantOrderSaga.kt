package com.drestaurant.restaurant.domain

import com.drestaurant.order.domain.api.RestaurantOrderCreationRequestedEvent
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/**
 * Managing invariants (business transaction) of [RestaurantOrder] and [Restaurant]
 *
 * Consider restricting the modifier of this class to internal. It is public because of the Spring configuration: drestaurant-apps/drestaurant-monolith/com.drestaurant.configuration.AxonConfiguration
 */
@Saga(configurationBean = "restaurantOrderSagaConfiguration")
class RestaurantOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: RestaurantOrderCreationRequestedEvent) {
        orderId = event.aggregateIdentifier
        associateWith("orderId", orderId)
        val command = CreateRestaurantOrderCommand(event.aggregateIdentifier, event.orderDetails, event.restaurantId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: RestaurantOrderCreationInitiatedEvent) = commandGateway.send(ValidateOrderByRestaurantCommand(orderId, event.restaurantId, event.orderDetails.lineItems, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: RestaurantNotFoundForOrderEvent) = commandGateway.send(MarkRestaurantOrderAsRejectedCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithSuccessByRestaurantEvent) = commandGateway.send(MarkRestaurantOrderAsCreatedCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithErrorByRestaurantEvent) = commandGateway.send(MarkRestaurantOrderAsRejectedCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

}
