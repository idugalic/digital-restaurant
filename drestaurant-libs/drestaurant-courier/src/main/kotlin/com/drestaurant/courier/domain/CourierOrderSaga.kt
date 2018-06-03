package com.drestaurant.courier.domain

import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
internal class CourierOrderSaga {

    @Autowired
    @Transient
    private val commandGateway: CommandGateway? = null
    private var orderId: String? = null

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: CourierOrderAssigningInitiatedEvent) {
        this.orderId = event.aggregateIdentifier
        associateWith("orderId", this.orderId)
        val command = ValidateOrderByCourierCommand(this.orderId!!, event.courierId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: CourierNotFoundForOrderEvent) {
        val command = MarkCourierOrderAsNotAssignedCommand(event.orderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderValidatedWithSuccessByCourierEvent) {
        val command = MarkCourierOrderAsAssignedCommand(event.orderId, event.courierId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderValidatedWithErrorByCourierEvent) {
        val command = MarkCourierOrderAsNotAssignedCommand(event.orderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }


}
