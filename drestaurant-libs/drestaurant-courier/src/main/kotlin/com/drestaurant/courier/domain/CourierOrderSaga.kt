package com.drestaurant.courier.domain

import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle.*
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/**
 * Managing invariants (business transaction) of [CourierOrder] and [Courier] aggregates within 'Courier' bounded context
 */
@Saga
@ProcessingGroup("courierordersaga")
internal class CourierOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: CourierOrderAssigningInitiatedInternalEvent) {
        orderId = event.aggregateIdentifier
        associateWith("orderId", orderId)
        commandGateway.send(ValidateOrderByCourierInternalCommand(orderId, event.courierId, event.auditEntry), LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: CourierNotFoundForOrderInternalEvent) = commandGateway.send(MarkCourierOrderAsNotAssignedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderValidatedWithSuccessByCourierInternalEvent) = commandGateway.send(MarkCourierOrderAsAssignedInternalCommand(event.orderId, event.courierId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderValidatedWithErrorByCourierInternalEvent) = commandGateway.send(MarkCourierOrderAsNotAssignedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
