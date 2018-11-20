package com.drestaurant.restaurant.domain

import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/**
 * Managing invariants (business transaction) of [RestaurantOrder] and [Restaurant]
 *
 * Alternatively, you could choose to spawn [RestaurantOrder] aggregate from [Restaurant] aggregate directly (Axon provides this functionality), and not use saga.
 * Benefits are that you check invariants internally, and you can do this with less internal (internal to the Kotlin module) events.
 * Consequences are that you will introduce more coupling between this aggregates, which is fine as long as they belong to the same bounded context.
 */
@Saga
@ProcessingGroup("restaurantordersaga")
class RestaurantOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: RestaurantOrderCreationInitiatedInternalEvent) = commandGateway.send(ValidateOrderByRestaurantInternalCommand(event.aggregateIdentifier, event.restaurantId, event.orderDetails.lineItems, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    internal fun on(event: RestaurantNotFoundForOrderInternalEvent) = commandGateway.send(MarkRestaurantOrderAsRejectedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    internal fun on(event: RestaurantValidatedOrderWithSuccessInternalEvent) = commandGateway.send(MarkRestaurantOrderAsCreatedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId", keyName = "aggregateIdentifier")
    internal fun on(event: RestaurantValidatedOrderWithErrorInternalEvent) = commandGateway.send(MarkRestaurantOrderAsRejectedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
