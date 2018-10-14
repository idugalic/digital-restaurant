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

/**
 * Managing invariants (business transaction) of [CustomerOrder] and [Customer] aggregates within 'Customer' bounded context
 *
 * Consider restricting the modifier of this class to internal. It is public because of the Spring configuration: drestaurant-apps/drestaurant-monolith/com.drestaurant.configuration.AxonConfiguration
 */
@Saga(configurationBean = "customerOrderSagaConfiguration")
class CustomerOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    /**
     * Start saga on external/public event [CustomerOrderCreationRequestedEvent]. This event can be published from another component/bounded context
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: CustomerOrderCreationRequestedEvent) = commandGateway.send(CreateCustomerOrderCommand(event.aggregateIdentifier, event.orderTotal, event.customerId, event.auditEntry), LoggingCallback.INSTANCE)

    /**
     * Start saga on internal event [CustomerOrderCreationInitiatedInternalEvent]. This event can be published from this component/bounded context only, as a result of a public [CreateCustomerOrderCommand] command
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: CustomerOrderCreationInitiatedInternalEvent) {
        orderId = event.aggregateIdentifier
        associateWith("orderId", orderId)
        commandGateway.send(ValidateOrderByCustomerInternalCommand(orderId, event.customerId, event.orderTotal, event.auditEntry), LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: CustomerNotFoundForOrderInternalEvent) = commandGateway.send(MarkCustomerOrderAsRejectedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithSuccessByCustomerInternalEvent) = commandGateway.send(MarkCustomerOrderAsCreatedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithErrorByCustomerInternalEvent) = commandGateway.send(MarkCustomerOrderAsRejectedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
