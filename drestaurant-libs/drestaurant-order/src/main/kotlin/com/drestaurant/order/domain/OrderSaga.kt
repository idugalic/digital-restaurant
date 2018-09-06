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
        this.orderId = event.aggregateIdentifier

        val customerOrderId = "customerOrder_" + this.orderId
        SagaLifecycle.associateWith("customerOrderId", customerOrderId)

        this.restaurantId = event.orderDetails.restaurantId
        this.customerId = event.orderDetails.consumerId
        this.orderDetails = event.orderDetails

        eventBus.publish(GenericEventMessage.asEventMessage<Any>(CustomerOrderCreationRequestedEvent(customerOrderId, event.orderDetails.orderTotal, this.customerId.orEmpty(), event.auditEntry)))
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "customerOrderId")
    fun on(event: CustomerOrderCreatedEvent) {
        val command = MarkOrderAsVerifiedByCustomerCommand(this.orderId, this.customerId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderVerifiedByCustomerEvent) {
        val restaurantOrderId = "restaurantOrder_" + this.orderId
        SagaLifecycle.associateWith("restaurantOrderId", restaurantOrderId)

        val restaurantLineItems = ArrayList<RestaurantOrderLineItem>()
        for (oli in this.orderDetails.lineItems) {
            val roli = RestaurantOrderLineItem(oli.quantity, oli.menuItemId, oli.name)
            restaurantLineItems.add(roli)
        }
        val restaurantOrderDetails = RestaurantOrderDetails(restaurantLineItems)

        eventBus.publish(GenericEventMessage.asEventMessage<Any>(RestaurantOrderCreationRequestedEvent(restaurantOrderId, restaurantOrderDetails, this.restaurantId.orEmpty(), event.auditEntry)))
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderCreatedEvent) {
        val command = MarkOrderAsVerifiedByRestaurantCommand(this.orderId, this.restaurantId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderPreparedEvent) {
        val command = MarkOrderAsPreparedCommand(this.orderId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderPreparedEvent) {
        val courierOrderId = "courierOrder_" + this.orderId
        SagaLifecycle.associateWith("courierOrderId", courierOrderId)

        eventBus.publish(GenericEventMessage.asEventMessage<Any>(CourierOrderCreationRequestedEvent(courierOrderId, event.auditEntry)))
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "courierOrderId")
    fun on(event: CourierOrderCreatedEvent) {
        val command = MarkOrderAsReadyForDeliveryCommand(this.orderId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "courierOrderId")
    fun on(event: CourierOrderDeliveredEvent) {
        val command = MarkOrderAsDeliveredCommand(this.orderId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "customerOrderId")
    fun on(event: CustomerOrderRejectedEvent) {
        val command = MarkOrderAsRejectedCommand(this.orderId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderRejectedEvent) {
        val command = MarkOrderAsRejectedCommand(this.orderId, event.auditEntry)
        commandGateway.send(command, LoggingCallback.INSTANCE)
    }

}
