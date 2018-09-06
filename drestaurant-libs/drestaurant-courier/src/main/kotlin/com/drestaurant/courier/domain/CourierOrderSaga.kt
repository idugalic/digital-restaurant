package com.drestaurant.courier.domain

import com.drestaurant.courier.domain.api.CreateCourierOrderCommand
import com.drestaurant.order.domain.api.CourierOrderCreationRequestedEvent
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga(configurationBean = "courierOrderSagaConfiguration")
class CourierOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun handle(event: CourierOrderCreationRequestedEvent) {
        val command = CreateCourierOrderCommand(event.aggregateIdentifier, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: CourierOrderAssigningInitiatedEvent) {
        this.orderId = event.aggregateIdentifier
        associateWith("orderId", this.orderId)
        val command = ValidateOrderByCourierCommand(this.orderId, event.courierId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: CourierNotFoundForOrderEvent) {
        val command = MarkCourierOrderAsNotAssignedCommand(event.orderId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithSuccessByCourierEvent) {
        val command = MarkCourierOrderAsAssignedCommand(event.orderId, event.courierId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithErrorByCourierEvent) {
        val command = MarkCourierOrderAsNotAssignedCommand(event.orderId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }


}
