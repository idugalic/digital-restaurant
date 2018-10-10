package com.drestaurant.order.domain

import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent
import com.drestaurant.customer.domain.api.CustomerOrderCreatedEvent
import com.drestaurant.customer.domain.api.CustomerOrderRejectedEvent
import com.drestaurant.order.domain.api.*
import com.drestaurant.order.domain.api.OrderCreationInitiatedEvent
import com.drestaurant.order.domain.model.OrderDetails
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderRejectedEvent
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle
import org.axonframework.eventhandling.saga.SagaLifecycle.*
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Saga(configurationBean = "orderSagaConfiguration")
class OrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    @Autowired
    @Transient
    private lateinit var eventBus: EventBus
    private lateinit var restaurantId: String
    private lateinit var customerId: String
    private lateinit var orderDetails: OrderDetails
    private lateinit var orderId: String

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderCreationInitiatedEvent) {
        orderId = event.aggregateIdentifier

        val customerOrderId = "customerOrder_$orderId"
        associateWith("customerOrderId", customerOrderId)

        restaurantId = event.orderDetails.restaurantId
        customerId = event.orderDetails.consumerId
        orderDetails = event.orderDetails

        eventBus.publish(GenericEventMessage.asEventMessage<Any>(CustomerOrderCreationRequestedEvent(customerOrderId, event.orderDetails.orderTotal, customerId, event.auditEntry)))
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "customerOrderId")
    fun on(event: CustomerOrderCreatedEvent) = commandGateway.send(MarkOrderAsVerifiedByCustomerCommand(orderId, customerId, event.auditEntry), LoggingCallback.INSTANCE)

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderVerifiedByCustomerEvent) {
        val restaurantOrderId = "restaurantOrder_$orderId"
        associateWith("restaurantOrderId", restaurantOrderId)

        val restaurantLineItems = ArrayList<RestaurantOrderLineItem>()
        for (oli in orderDetails.lineItems) {
            val roli = RestaurantOrderLineItem(oli.quantity, oli.menuItemId, oli.name)
            restaurantLineItems.add(roli)
        }
        val restaurantOrderDetails = RestaurantOrderDetails(restaurantLineItems)

        eventBus.publish(GenericEventMessage.asEventMessage<Any>(RestaurantOrderCreationRequestedEvent(restaurantOrderId, restaurantOrderDetails, restaurantId, event.auditEntry)))
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderCreatedEvent) = commandGateway.send(MarkOrderAsVerifiedByRestaurantCommand(orderId, restaurantId, event.auditEntry), LoggingCallback.INSTANCE)

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderPreparedEvent) = commandGateway.send(MarkOrderAsPreparedCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderPreparedEvent) {
        val courierOrderId = "courierOrder_$orderId"
        associateWith("courierOrderId", courierOrderId)
        eventBus.publish(GenericEventMessage.asEventMessage<Any>(CourierOrderCreationRequestedEvent(courierOrderId, event.auditEntry)))
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "courierOrderId")
    fun on(event: CourierOrderCreatedEvent) = commandGateway.send(MarkOrderAsReadyForDeliveryCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "courierOrderId")
    fun on(event: CourierOrderDeliveredEvent) = commandGateway.send(MarkOrderAsDeliveredCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "customerOrderId")
    fun on(event: CustomerOrderRejectedEvent) = commandGateway.send(MarkOrderAsRejectedCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderRejectedEvent) = commandGateway.send(MarkOrderAsRejectedCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

}
