package com.drestaurant.courier.domain

import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/**
 * Managing invariants (business transaction) of [CourierOrder] and [Courier] aggregates within 'Courier' bounded context
 *
 * Alternatively, you could choose to spawn [CourierOrder] aggregate from [Courier] aggregate directly (Axon provides this functionality), and not use saga.
 * Benefits are that you check invariants internally, and you can do this with less internal (internal to the Kotlin module) events.
 * Consequences are that you will introduce more coupling between this aggregates, which is fine as long as they belong to the same bounded context.
 */
@Saga
@ProcessingGroup("courierordersaga")
internal class CourierOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: CourierOrderAssigningInitiatedInternalEvent) = commandGateway.send(ValidateOrderByCourierInternalCommand(event.aggregateIdentifier, event.courierId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    fun on(event: CourierNotFoundForOrderInternalEvent) = commandGateway.send(MarkCourierOrderAsNotAssignedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    fun on(event: CourierValidatedOrderWithSuccessInternalEvent) = commandGateway.send(MarkCourierOrderAsAssignedInternalCommand(event.orderId, event.aggregateIdentifier, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    fun on(event: CourierValidatedOrderWithErrorInternalEvent) = commandGateway.send(MarkCourierOrderAsNotAssignedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
