package com.drestaurant.customer.domain

import com.drestaurant.customer.domain.api.CreateCustomerOrderCommand
import com.drestaurant.order.domain.api.CustomerOrderCreationRequestedEvent
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga(configurationBean = "customerOrderSagaConfiguration")
class CustomerOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: CustomerOrderCreationRequestedEvent) {
        orderId = event.aggregateIdentifier
        associateWith("orderId", orderId)
        val command = CreateCustomerOrderCommand(orderId, event.orderTotal, event.customerId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: CustomerOrderCreationInitiatedEvent) = commandGateway.send(ValidateOrderByCustomerCommand(orderId, event.customerId, event.orderTotal, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: CustomerNotFoundForOrderEvent) = commandGateway.send(MarkCustomerOrderAsRejectedCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithSuccessByCustomerEvent) = commandGateway.send(MarkCustomerOrderAsCreatedCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithErrorByCustomerEvent) = commandGateway.send(MarkCustomerOrderAsRejectedCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
