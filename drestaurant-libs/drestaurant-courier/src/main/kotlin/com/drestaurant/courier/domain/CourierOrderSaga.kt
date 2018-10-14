package com.drestaurant.courier.domain

import com.drestaurant.courier.domain.api.CreateCourierOrderCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/**
 * Managing invariants (business transaction) of [CourierOrder] and [Courier] aggregates within 'Courier' bounded context
 *
 * Consider restricting the modifier of this class to internal. It is public because of the Spring configuration: drestaurant-apps/drestaurant-monolith/com.drestaurant.configuration.AxonConfiguration
 */
@Saga(configurationBean = "courierOrderSagaConfiguration")
class CourierOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    /**
     * Start saga on internal event [CourierOrderAssigningInitiatedInternalEvent]. This event can be published from this component/bounded context only, as a result of a public [CreateCourierOrderCommand] command
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: CourierOrderAssigningInitiatedInternalEvent) {
        orderId = event.aggregateIdentifier
        associateWith("orderId", orderId)
        commandGateway.send(ValidateOrderByCourierInternalCommand(orderId, event.courierId, event.auditEntry), LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: CourierNotFoundForOrderInternalEvent) = commandGateway.send(MarkCourierOrderAsNotAssignedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithSuccessByCourierInternalEvent) = commandGateway.send(MarkCourierOrderAsAssignedInternalCommand(event.orderId, event.courierId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithErrorByCourierInternalEvent) = commandGateway.send(MarkCourierOrderAsNotAssignedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
