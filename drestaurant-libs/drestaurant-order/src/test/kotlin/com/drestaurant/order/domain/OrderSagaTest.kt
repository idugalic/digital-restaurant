package com.drestaurant.order.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
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
import com.drestaurant.order.domain.api.model.OrderInfo
import com.drestaurant.order.domain.api.model.OrderLineItem
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderRejectedEvent
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderLineItem
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class OrderSagaTest {

    private lateinit var testFixture: FixtureConfiguration

    private val who = "johndoe"
    private var auditEntry: AuditEntry = AuditEntry(who, Calendar.getInstance().time)

    private val lineItems: MutableList<OrderLineItem> = ArrayList()
    private val lineItem1: OrderLineItem = OrderLineItem("menuItemId1", "name1", Money(BigDecimal.valueOf(11)), 2)
    private val lineItem2: OrderLineItem = OrderLineItem("menuItemId2", "name2", Money(BigDecimal.valueOf(22)), 3)
    private var orderInfo: OrderInfo = OrderInfo("customerId", "restaurantId", lineItems)
    private val orderDetails: OrderDetails = OrderDetails(orderInfo, lineItems.stream().map(OrderLineItem::total).reduce(Money(BigDecimal.ZERO), Money::add))
    private val orderId: OrderId = OrderId("orderId")
    private var customerId: CustomerId = CustomerId("customerId")
    private var restaurantId: RestaurantId = RestaurantId("restaurantId")

    private var restaurantLineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private var restaurantOrderDetails: RestaurantOrderDetails = RestaurantOrderDetails(restaurantLineItems)

    @Before
    fun setUp() {

        testFixture = SagaTestFixture(OrderSaga::class.java)
        lineItems.add(lineItem1)
        lineItems.add(lineItem2)

        for (oli in this.orderDetails.lineItems) {
            val roli = RestaurantOrderLineItem(oli.quantity, oli.menuItemId, oli.name)
            restaurantLineItems.add(roli)
        }
    }

    @Test
    fun orderCreationInitiatedTest() {

        testFixture.givenNoPriorActivity()
                .whenAggregate(orderId.toString())
                .publishes(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
                )
                .expectActiveSagas(1)
                .expectDispatchedCommands(CreateCustomerOrderCommand(customerId, CustomerOrderId("customerOrder_$orderId"), orderDetails.orderTotal, auditEntry))
    }

    @Test
    fun customerOrderCreatedTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
                )
                .whenPublishingA(CustomerOrderCreatedEvent(orderDetails.orderTotal, customerId, CustomerOrderId("customerOrder_$orderId"), auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(MarkOrderAsVerifiedByCustomerInternalCommand(orderId, customerId, auditEntry))
    }

    @Test
    fun orderVerifiedByCustomerTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
                )
                .whenPublishingA(OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(CreateRestaurantOrderCommand(restaurantId, restaurantOrderDetails, RestaurantOrderId("restaurantOrder_$orderId"), auditEntry))
    }

    @Test
    fun restaurantOrderCreatedEventTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
                        CreateCustomerOrderCommand(customerId, CustomerOrderId("customerOrder_$orderId"), orderDetails.orderTotal, auditEntry),
                        OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry)
                )
                .whenPublishingA(RestaurantOrderCreatedEvent(restaurantLineItems, RestaurantOrderId("restaurantOrder_$orderId"), restaurantId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(MarkOrderAsVerifiedByRestaurantInternalCommand(orderId, restaurantId, auditEntry))
    }

    @Test
    fun restaurantOrderPreparedEventTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
                        CreateCustomerOrderCommand(customerId, CustomerOrderId("customerOrder_$orderId"), orderDetails.orderTotal, auditEntry),
                        OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
                        RestaurantOrderCreatedEvent(restaurantLineItems, RestaurantOrderId("restaurantOrder_$orderId"), restaurantId, auditEntry)
                )
                .whenPublishingA(RestaurantOrderPreparedEvent(RestaurantOrderId("restaurantOrder_$orderId"), auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(MarkOrderAsPreparedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun orderPreparedEventTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
                        CreateCustomerOrderCommand(customerId, CustomerOrderId("customerOrder_$orderId"), orderDetails.orderTotal, auditEntry),
                        OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
                        RestaurantOrderCreatedEvent(restaurantLineItems, RestaurantOrderId("restaurantOrder_$orderId"), restaurantId, auditEntry),
                        RestaurantOrderPreparedEvent(RestaurantOrderId("restaurantOrder_$orderId"), auditEntry)
                )
                .whenPublishingA(OrderPreparedEvent(orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(CreateCourierOrderCommand(CourierOrderId("courierOrder_$orderId"), auditEntry))
    }

    @Test
    fun courierOrderCreatedEventTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
                        CreateCustomerOrderCommand(customerId, CustomerOrderId("customerOrder_$orderId"), orderDetails.orderTotal, auditEntry),
                        OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
                        RestaurantOrderCreatedEvent(restaurantLineItems, RestaurantOrderId("restaurantOrder_$orderId"), restaurantId, auditEntry),
                        RestaurantOrderPreparedEvent(RestaurantOrderId("restaurantOrder_$orderId"), auditEntry),
                        OrderPreparedEvent(orderId, auditEntry)
                )
                .whenPublishingA(CourierOrderCreatedEvent(CourierOrderId("courierOrder_$orderId"), auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(MarkOrderAsReadyForDeliveryInternalCommand(orderId, auditEntry))
    }

    @Test
    fun courierOrderDeliveredEventTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
                        CreateCustomerOrderCommand(customerId, CustomerOrderId("customerOrder_$orderId"), orderDetails.orderTotal, auditEntry),
                        OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
                        RestaurantOrderCreatedEvent(restaurantLineItems, RestaurantOrderId("restaurantOrder_$orderId"), restaurantId, auditEntry),
                        RestaurantOrderPreparedEvent(RestaurantOrderId("restaurantOrder_$orderId"), auditEntry),
                        OrderPreparedEvent(orderId, auditEntry),
                        CourierOrderCreatedEvent(CourierOrderId("courierOrder_$orderId"), auditEntry)
                )
                .whenPublishingA(CourierOrderDeliveredEvent(CourierOrderId("courierOrder_$orderId"), auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkOrderAsDeliveredInternalCommand(orderId, auditEntry))
    }

    @Test
    fun customerOrderRejectedEventTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
                )
                .whenPublishingA(CustomerOrderRejectedEvent(CustomerOrderId("customerOrder_$orderId"), auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkOrderAsRejectedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun restaurantOrderRejectedEventTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(
                        OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
                        CreateCustomerOrderCommand(customerId, CustomerOrderId("customerOrder_$orderId"), orderDetails.orderTotal, auditEntry),
                        OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry)
                )
                .whenPublishingA(RestaurantOrderRejectedEvent(RestaurantOrderId("restaurantOrder_$orderId"), auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkOrderAsRejectedInternalCommand(orderId, auditEntry))
    }
}
