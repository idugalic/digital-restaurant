package com.drestaurant.customer.domain

import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/**
 * Managing invariants (business transaction) of [CustomerOrder] and [Customer] aggregates within 'Customer' bounded context
 */
@Saga
@ProcessingGroup("customerordersaga")
internal class CustomerOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway


    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: CustomerOrderCreationInitiatedInternalEvent) = commandGateway.send(ValidateOrderByCustomerInternalCommand(event.aggregateIdentifier, event.customerId, event.orderTotal, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    fun on(event: CustomerNotFoundForOrderInternalEvent) = commandGateway.send(MarkCustomerOrderAsRejectedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    fun on(event: CustomerValidatedOrderWithSuccessInternalEvent) = commandGateway.send(MarkCustomerOrderAsCreatedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    fun on(event: CustomerValidatedOrderWithErrorInternalEvent) = commandGateway.send(MarkCustomerOrderAsRejectedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
