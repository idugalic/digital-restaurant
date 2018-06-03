package com.drestaurant.customer.domain

import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
internal class CustomerOrderSaga {

    @Autowired
    @Transient
    private val commandGateway: CommandGateway? = null
    private var orderId: String? = null

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: CustomerOrderCreationInitiatedEvent) {
        this.orderId = event.aggregateIdentifier
        associateWith("orderId", this.orderId)
        val command = ValidateOrderByCustomerCommand(this.orderId!!, event.customerId, event.orderTotal, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: CustomerNotFoundForOrderEvent) {
        val command = MarkCustomerOrderAsRejectedCommand(event.orderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderValidatedWithSuccessByCustomerEvent) {
        val command = MarkCustomerOrderAsCreatedCommand(event.orderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderValidatedWithErrorByCustomerEvent) {
        val command = MarkCustomerOrderAsRejectedCommand(event.orderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }


}
