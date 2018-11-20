package com.drestaurant.order.domain

import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent
import com.drestaurant.courier.domain.api.CreateCourierOrderCommand
import com.drestaurant.courier.domain.api.model.CourierOrderId
import com.drestaurant.customer.domain.api.CreateCustomerOrderCommand
import com.drestaurant.customer.domain.api.CustomerOrderCreatedEvent
import com.drestaurant.customer.domain.api.CustomerOrderRejectedEvent
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId
import com.drestaurant.order.domain.api.OrderCreationInitiatedEvent
import com.drestaurant.order.domain.api.OrderPreparedEvent
import com.drestaurant.order.domain.api.OrderVerifiedByCustomerEvent
import com.drestaurant.order.domain.api.model.OrderDetails
import com.drestaurant.order.domain.api.model.OrderId
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderRejectedEvent
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderLineItem
import org.axonframework.commandhandling.callbacks.LoggingCallback
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle.associateWith
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

/**
 * Managing invariants (business transaction) of different orders that belong to other bounded contexts (CustomerOrder, RestaurantOrder and CourierOrder)
 *
 * We must maintain consistency between these different 'order' aggregates in different bounded contexts.
 * For example, once the Order saga has initiated order creation it must trigger the creation of CustomerOrder ('Customer' bounded context) and RestaurantOrder ('Restaurant' bounded context)
 * The creation is triggered by publishing commands, e.g [CreateCustomerOrderCommand], [CreateRestaurantOrderCommand]
 */
@Saga
@ProcessingGroup("ordersaga")
internal class OrderSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var restaurantId: RestaurantId
    private lateinit var customerId: CustomerId
    private lateinit var orderDetails: OrderDetails
    private lateinit var orderId: OrderId

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderCreationInitiatedEvent) {
        orderId = event.aggregateIdentifier
        restaurantId = RestaurantId(event.orderDetails.restaurantId)
        customerId = CustomerId(event.orderDetails.consumerId)
        orderDetails = event.orderDetails

        val customerOrderId = CustomerOrderId("customerOrder_$orderId")
        associateWith("customerOrderId", customerOrderId.toString())

        commandGateway.send(CreateCustomerOrderCommand(customerOrderId, orderDetails.orderTotal, customerId, event.auditEntry), LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "customerOrderId")
    fun on(event: CustomerOrderCreatedEvent) = commandGateway.send(MarkOrderAsVerifiedByCustomerInternalCommand(orderId, customerId, event.auditEntry), LoggingCallback.INSTANCE)

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderVerifiedByCustomerEvent) {
        val restaurantOrderId = RestaurantOrderId("restaurantOrder_$orderId")
        associateWith("restaurantOrderId", restaurantOrderId.toString())

        val restaurantLineItems = ArrayList<RestaurantOrderLineItem>()
        for (oli in orderDetails.lineItems) {
            val roli = RestaurantOrderLineItem(oli.quantity, oli.menuItemId, oli.name)
            restaurantLineItems.add(roli)
        }
        val restaurantOrderDetails = RestaurantOrderDetails(restaurantLineItems)
        commandGateway.send(CreateRestaurantOrderCommand(restaurantOrderId, restaurantOrderDetails, restaurantId, event.auditEntry), LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderCreatedEvent) = commandGateway.send(MarkOrderAsVerifiedByRestaurantInternalCommand(orderId, restaurantId, event.auditEntry), LoggingCallback.INSTANCE)

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderPreparedEvent) = commandGateway.send(MarkOrderAsPreparedInternalCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    fun on(event: OrderPreparedEvent) {
        val courierOrderId = CourierOrderId("courierOrder_$orderId")
        associateWith("courierOrderId", courierOrderId.toString())
        commandGateway.send(CreateCourierOrderCommand(courierOrderId, event.auditEntry), LoggingCallback.INSTANCE)
    }

    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "courierOrderId")
    fun on(event: CourierOrderCreatedEvent) = commandGateway.send(MarkOrderAsReadyForDeliveryInternalCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "courierOrderId")
    fun on(event: CourierOrderDeliveredEvent) = commandGateway.send(MarkOrderAsDeliveredInternalCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "customerOrderId")
    fun on(event: CustomerOrderRejectedEvent) = commandGateway.send(MarkOrderAsRejectedInternalCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)

    @EndSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier", keyName = "restaurantOrderId")
    fun on(event: RestaurantOrderRejectedEvent) = commandGateway.send(MarkOrderAsRejectedInternalCommand(orderId, event.auditEntry), LoggingCallback.INSTANCE)
}
