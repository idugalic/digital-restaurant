package com.drestaurant.restaurant.domain

import com.drestaurant.order.domain.api.RestaurantOrderCreationRequestedEvent
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/**
 * Managing invariants (business transaction) of [RestaurantOrder] and [Restaurant]
 *
 * Consider restricting the modifier of this class to internal. It is public because of the Spring configuration: drestaurant-apps/drestaurant-monolith/com.drestaurant.configuration.AxonConfiguration
 */
@Saga(configurationBean = "restaurantOrderSagaConfiguration")
class RestaurantOrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    /**
     * Start saga on external/public event [RestaurantOrderCreationRequestedEvent]. This event can be published from another component/bounded context
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: RestaurantOrderCreationRequestedEvent) = commandGateway.send(CreateRestaurantOrderCommand(event.aggregateIdentifier, event.orderDetails, event.restaurantId, event.auditEntry), LoggingCallback.INSTANCE)

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
