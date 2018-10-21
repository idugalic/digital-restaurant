package com.drestaurant.restaurant.domain

import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle.associateWith
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/**
 * Managing invariants (business transaction) of [RestaurantOrder] and [Restaurant]
 *
 * Consider restricting the modifier of this class to internal. It is public because of the Spring configuration: drestaurant-apps/drestaurant-monolith/com.drestaurant.configuration.AxonConfiguration
 */
@Saga
@ProcessingGroup("restaurantordersaga")
class RestaurantOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    /**
     * Start saga on internal event [RestaurantOrderCreationInitiatedInternalEvent]. This event can be published from this component/bounded context only, as a result of a public [CreateRestaurantOrderCommand] command
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: RestaurantOrderCreationInitiatedInternalEvent) {
        orderId = event.aggregateIdentifier
        associateWith("orderId", orderId)
        commandGateway.send(ValidateOrderByRestaurantInternalCommand(orderId, event.restaurantId, event.orderDetails.lineItems, event.auditEntry), LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: RestaurantNotFoundForOrderInternalEvent) = commandGateway.send(MarkRestaurantOrderAsRejectedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithSuccessByRestaurantInternalEvent) = commandGateway.send(MarkRestaurantOrderAsCreatedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithErrorByRestaurantInternalEvent) = commandGateway.send(MarkRestaurantOrderAsRejectedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
