package com.drestaurant.order.domain

import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent
import com.drestaurant.courier.domain.api.CreateCourierOrderCommand
import com.drestaurant.customer.domain.api.CreateCustomerOrderCommand
import com.drestaurant.customer.domain.api.CustomerOrderCreatedEvent
import com.drestaurant.customer.domain.api.CustomerOrderRejectedEvent
import com.drestaurant.customer.domain.api.MarkCustomerOrderAsDeliveredCommand
import com.drestaurant.order.domain.api.OrderCreationInitiatedEvent
import com.drestaurant.order.domain.api.OrderDeliveredEvent
import com.drestaurant.order.domain.api.OrderPreparedEvent
import com.drestaurant.order.domain.api.OrderVerifiedByCustomerEvent
import com.drestaurant.order.domain.model.OrderDetails
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderRejectedEvent
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Saga
class OrderSaga {

    @Autowired
    @Transient
    private val commandGateway: CommandGateway? = null
    private var restaurantId: String? = null
    private var customerId: String? = null
    private var orderDetails: OrderDetails? = null
    private var orderId: String? = null

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderCreationInitiatedEvent) {
        this.orderId = event.aggregateIdentifier

        val customerOrderId = "customerOrder_" + this.orderId!!
        SagaLifecycle.associateWith("customerOrderId", customerOrderId)

        this.restaurantId = event.orderDetails.restaurantId
        this.customerId = event.orderDetails.consumerId
        this.orderDetails = event.orderDetails

        val command = CreateCustomerOrderCommand(customerOrderId, event.orderDetails.orderTotal, this.customerId.orEmpty(), event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "customerOrderId")
    fun on(event: CustomerOrderCreatedEvent) {
        val command = MarkOrderAsVerifiedByCustomerCommand(this.orderId!!, this.customerId!!, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderVerifiedByCustomerEvent) {
        val restaurantOrderId = "restaurantOrder_" + this.orderId!!
        SagaLifecycle.associateWith("restaurantOrderId", restaurantOrderId)

        val restaurantLineItems = ArrayList<RestaurantOrderLineItem>()
        for (oli in this.orderDetails!!.lineItems) {
            val roli = RestaurantOrderLineItem(oli.getQuantity(), oli.menuItemId, oli.name)
            restaurantLineItems.add(roli)
        }
        val restaurantOrderDetails = RestaurantOrderDetails(restaurantLineItems)

        val command = CreateRestaurantOrderCommand(restaurantOrderId, restaurantOrderDetails, this.restaurantId.orEmpty(), event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderCreatedEvent) {
        val command = MarkOrderAsVerifiedByRestaurantCommand(this.orderId!!, this.restaurantId!!, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderPreparedEvent) {
        val command = MarkOrderAsPreparedCommand(this.orderId!!, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderPreparedEvent) {
        val courierOrderId = "courierOrder_" + this.orderId!!
        SagaLifecycle.associateWith("courierOrderId", courierOrderId)

        val command = CreateCourierOrderCommand(courierOrderId, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "courierOrderId")
    fun on(event: CourierOrderCreatedEvent) {
        val command = MarkOrderAsReadyForDeliveryCommand(this.orderId!!, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "courierOrderId")
    fun on(event: CourierOrderDeliveredEvent) {
        val command = MarkOrderAsDeliveredCommand(this.orderId!!, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderDeliveredEvent) {
        val command = MarkCustomerOrderAsDeliveredCommand("customerOrder_" + this.orderId!!, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "customerOrderId")
    fun on(event: CustomerOrderRejectedEvent) {
        val command = MarkOrderAsRejectedCommand(this.orderId!!, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderRejectedEvent) {
        val command = MarkOrderAsRejectedCommand(this.orderId!!, event.auditEntry)
        commandGateway!!.send(command, LoggingCallback.INSTANCE)
    }

}
