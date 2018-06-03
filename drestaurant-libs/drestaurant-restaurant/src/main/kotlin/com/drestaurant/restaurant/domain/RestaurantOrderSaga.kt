package com.drestaurant.restaurant.domain

import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
internal class RestaurantOrderSaga {

    @Autowired
    @Transient
    private val commandGateway: CommandGateway? = null
    private var orderId: String? = null

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: RestaurantOrderCreationInitiatedEvent) {
        this.orderId = event.aggregateIdentifier
        associateWith("orderId", this.orderId)
        val command = ValidateOrderByRestaurantCommand(this.orderId!!, event.restaurantId,
                event.orderDetails.getLineItems(), event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: RestaurantNotFoundForOrderEvent) {
        val command = MarkRestaurantOrderAsRejectedCommand(event.orderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderValidatedWithSuccessByRestaurantEvent) {
        val command = MarkRestaurantOrderAsCreatedCommand(event.orderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderValidatedWithErrorByRestaurantEvent) {
        val command = MarkRestaurantOrderAsRejectedCommand(event.orderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

}
