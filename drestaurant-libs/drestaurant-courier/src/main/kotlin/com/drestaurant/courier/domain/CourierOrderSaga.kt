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

/**
 * Managing invariants (business transaction) of [CourierOrder] and [Courier]
 *
 * Consider restricting the modifier of this class to internal. It is public because of the Spring configuration: drestaurant-apps/drestaurant-monolith/com.drestaurant.configuration.AxonConfiguration
 */
@Saga(configurationBean = "courierOrderSagaConfiguration")
class CourierOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: CourierOrderCreationRequestedEvent) {
        orderId = event.aggregateIdentifier
        associateWith("orderId", orderId)
        val command = CreateCourierOrderCommand(orderId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: CourierOrderAssigningInitiatedEvent) = commandGateway.send(ValidateOrderByCourierCommand(orderId, event.courierId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: CourierNotFoundForOrderEvent) = commandGateway.send(MarkCourierOrderAsNotAssignedCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithSuccessByCourierEvent) = commandGateway.send(MarkCourierOrderAsAssignedCommand(event.orderId, event.courierId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithErrorByCourierEvent) = commandGateway.send(MarkCourierOrderAsNotAssignedCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
