package com.drestaurant.order.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.order.domain.api.*
import com.drestaurant.order.domain.model.OrderDetails
import com.drestaurant.order.domain.model.OrderInfo
import com.drestaurant.order.domain.model.OrderLineItem
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration

import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.*


class OrderAggregateTest {

    private lateinit var fixture: FixtureConfiguration<Order>
    private val who = "johndoe"
    private var auditEntry: AuditEntry = AuditEntry(who, Calendar.getInstance().time)

    private val lineItems: MutableList<OrderLineItem> = ArrayList()
    private val lineItem1: OrderLineItem = OrderLineItem("menuItemId1", "name1", Money(BigDecimal.valueOf(11)), 2)
    private val lineItem2: OrderLineItem = OrderLineItem("menuItemId2", "name2", Money(BigDecimal.valueOf(22)), 3)
    private var orderInfo: OrderInfo = OrderInfo("consumerId", "restaurantId", lineItems)
    private var orderDetails: OrderDetails = OrderDetails(orderInfo, lineItems.stream().map(OrderLineItem::total).reduce(Money(BigDecimal.ZERO), Money::add))
    private val orderId: String = "orderId"

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(Order::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())
        lineItems.add(lineItem1)
        lineItems.add(lineItem2)
        orderDetails = OrderDetails(orderInfo, lineItems.stream().map(OrderLineItem::total).reduce(Money(BigDecimal.ZERO), Money::add))
    }

    // ########## CREATE ###########
    @Test
    fun createOrderTest() {
        val createOrderCommand = CreateOrderCommand(orderInfo, auditEntry)
        val orderCreationInitiatedEvent = OrderCreationInitiatedEvent(orderDetails, createOrderCommand.targetAggregateIdentifier, createOrderCommand.auditEntry)

        fixture
                .given()
                .`when`(createOrderCommand)
                .expectEvents(orderCreationInitiatedEvent)
    }


    @Test
    fun markOrderAsVerifiedByCustomerCommandTest() {
        val orderCreationInitiatedEvent = OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
        val markOrderAsVerifiedByCustomerCommand = MarkOrderAsVerifiedByCustomerInternalCommand(orderCreationInitiatedEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.consumerId, auditEntry)
        val orderVerifiedByCustomerEvent = OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.consumerId, auditEntry)
        fixture
                .given(orderCreationInitiatedEvent)
                .`when`(markOrderAsVerifiedByCustomerCommand)
                .expectEvents(orderVerifiedByCustomerEvent)
    }

    @Test
    fun markOrderAsVerifiedByRestaurantCommandTest() {
        val orderCreationInitiatedEvent = OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
        val orderVerifiedByCustomerEvent = OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.consumerId, auditEntry)
        val markOrderAsVerifiedByRestaurantCommand = MarkOrderAsVerifiedByRestaurantInternalCommand(orderVerifiedByCustomerEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.restaurantId, auditEntry)
        val orderVerifiedByRestaurantEvent = OrderVerifiedByRestaurantEvent(orderVerifiedByCustomerEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.restaurantId, auditEntry)
        fixture
                .given(orderCreationInitiatedEvent, orderVerifiedByCustomerEvent)
                .`when`(markOrderAsVerifiedByRestaurantCommand)
                .expectEvents(orderVerifiedByRestaurantEvent)
    }

    @Test
    fun markOrderAsPreparedByRestaurantCommandTest() {
        val orderCreationInitiatedEvent = OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
        val orderVerifiedByCustomerEvent = OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.consumerId, auditEntry)
        val orderVerifiedByRestaurantEvent = OrderVerifiedByRestaurantEvent(orderVerifiedByCustomerEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.restaurantId, auditEntry)
        val markOrderAsPreparedCommand = MarkOrderAsPreparedInternalCommand(orderVerifiedByRestaurantEvent.aggregateIdentifier, auditEntry)
        val orderPreparedEvent = OrderPreparedEvent(orderVerifiedByCustomerEvent.aggregateIdentifier, auditEntry)
        fixture
                .given(orderCreationInitiatedEvent, orderVerifiedByCustomerEvent, orderVerifiedByRestaurantEvent)
                .`when`(markOrderAsPreparedCommand)
                .expectEvents(orderPreparedEvent)
    }

    @Test
    fun markOrderAsReadyForDeliveryTest() {
        val orderCreationInitiatedEvent = OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
        val orderVerifiedByCustomerEvent = OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.consumerId, auditEntry)
        val orderVerifiedByRestaurantEvent = OrderVerifiedByRestaurantEvent(orderVerifiedByCustomerEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.restaurantId, auditEntry)
        val orderPreparedEvent = OrderPreparedEvent(orderVerifiedByCustomerEvent.aggregateIdentifier, auditEntry)
        val markOrderAsReadyForDeliveryCommand = MarkOrderAsReadyForDeliveryInternalCommand(orderPreparedEvent.aggregateIdentifier, auditEntry)
        val orderReadyForDeliveryEvent = OrderReadyForDeliveryEvent(orderPreparedEvent.aggregateIdentifier, auditEntry)

        fixture
                .given(orderCreationInitiatedEvent, orderVerifiedByCustomerEvent, orderVerifiedByRestaurantEvent, orderPreparedEvent)
                .`when`(markOrderAsReadyForDeliveryCommand)
                .expectEvents(orderReadyForDeliveryEvent)
    }

    @Test
    fun markOrderAsDeliveredTest() {
        val orderCreationInitiatedEvent = OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
        val orderVerifiedByCustomerEvent = OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.consumerId, auditEntry)
        val orderVerifiedByRestaurantEvent = OrderVerifiedByRestaurantEvent(orderVerifiedByCustomerEvent.aggregateIdentifier, orderCreationInitiatedEvent.orderDetails.restaurantId, auditEntry)
        val orderPreparedEvent = OrderPreparedEvent(orderVerifiedByCustomerEvent.aggregateIdentifier, auditEntry)
        val orderReadyForDeliveryEvent = OrderReadyForDeliveryEvent(orderVerifiedByCustomerEvent.aggregateIdentifier, auditEntry)
        val markOrderAsDeliveredCommand = MarkOrderAsDeliveredInternalCommand(orderReadyForDeliveryEvent.aggregateIdentifier, auditEntry)
        val orderDeliveredEvent = OrderDeliveredEvent(orderReadyForDeliveryEvent.aggregateIdentifier, auditEntry)

        fixture
                .given(orderCreationInitiatedEvent, orderVerifiedByCustomerEvent, orderVerifiedByRestaurantEvent, orderPreparedEvent, orderReadyForDeliveryEvent)
                .`when`(markOrderAsDeliveredCommand)
                .expectEvents(orderDeliveredEvent)
    }

    // ########## REJECT ###########
    @Test
    fun markOrderAsRejectedCommandTest() {
        val orderCreationInitiatedEvent = OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
        val markOrderAsRejectedCommand = MarkOrderAsRejectedInternalCommand(orderCreationInitiatedEvent.aggregateIdentifier, auditEntry)
        val orderVerifiedByCustomerEvent = OrderRejectedEvent(orderCreationInitiatedEvent.aggregateIdentifier, auditEntry)
        fixture
                .given(orderCreationInitiatedEvent)
                .`when`(markOrderAsRejectedCommand)
                .expectEvents(orderVerifiedByCustomerEvent)
    }


}
