package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.api.*
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.util.*

class RestaurantOrderAggregateTest {

    private lateinit var fixture: FixtureConfiguration<RestaurantOrder>
    private var orderId: String = "orderId"
    private var restuarantId: String = "restuarantId"
    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "name")
    private var lineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private var orderDetails: RestaurantOrderDetails = RestaurantOrderDetails(lineItems)
    private val WHO = "johndoe"
    private var auditEntry: AuditEntry = AuditEntry(WHO, Calendar.getInstance().time)

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(RestaurantOrder::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())

        lineItems.add(lineItem)
    }

    @Test
    fun createRestaurantOrderTest() {
        val createRestaurantOrderCommand = CreateRestaurantOrderCommand(orderId, orderDetails, restuarantId, auditEntry)
        val restaurantOrderCreationInitiatedEvent = RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry)

        fixture.given().`when`(createRestaurantOrderCommand).expectEvents(restaurantOrderCreationInitiatedEvent)
    }

    @Test
    fun markOrderAsCreatedTest() {
        val restaurantOrderCreationInitiatedEvent = RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry)
        val markRestaurantOrderAsCreatedCommand = MarkRestaurantOrderAsCreatedInternalCommand(orderId, auditEntry)
        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(lineItems, restuarantId, orderId, auditEntry)

        fixture.given(restaurantOrderCreationInitiatedEvent).`when`(markRestaurantOrderAsCreatedCommand).expectEvents(restaurantOrderCreatedEvent)
    }

    @Test
    fun markOrderAsRejectedTest() {
        val restaurantOrderCreationInitiatedEvent = RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry)
        val markRestaurantOrderAsRejectedCommand = MarkRestaurantOrderAsRejectedInternalCommand(orderId, auditEntry)
        val restaurantOrderRejectedEvent = RestaurantOrderRejectedEvent(orderId, auditEntry)

        fixture.given(restaurantOrderCreationInitiatedEvent).`when`(markRestaurantOrderAsRejectedCommand).expectEvents(restaurantOrderRejectedEvent)
    }

    @Test
    fun markOrderAsRejectedFaildTest() {
        val restaurantOrderCreationInitiatedEvent = RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry)
        val markRestaurantOrderAsRejectedCommand = MarkRestaurantOrderAsRejectedInternalCommand(orderId, auditEntry)
        val restaurantOrderRejectedEvent = RestaurantOrderRejectedEvent(orderId, auditEntry)

        fixture.given(restaurantOrderCreationInitiatedEvent, restaurantOrderRejectedEvent) //Order already REJECTED
                .`when`(markRestaurantOrderAsRejectedCommand).expectException(UnsupportedOperationException::class.java)
    }

    @Test
    fun markOrderAsPreparedTest() {
        val restaurantOrderCreationInitiatedEvent = RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry)
        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(lineItems, restuarantId, orderId, auditEntry)

        val markRestaurantOrderAsPreparedCommand = MarkRestaurantOrderAsPreparedCommand(orderId, auditEntry)
        val restaurantOrderPreparedEvent = RestaurantOrderPreparedEvent(orderId, auditEntry)

        fixture.given(restaurantOrderCreationInitiatedEvent, restaurantOrderCreatedEvent).`when`(markRestaurantOrderAsPreparedCommand)
                .expectEvents(restaurantOrderPreparedEvent)
    }

    @Test
    fun markOrderAsPreparedFaildTest() {
        val restaurantOrderCreationInitiatedEvent = RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry)
        val markRestaurantOrderAsPreparedCommand = MarkRestaurantOrderAsPreparedCommand(orderId, auditEntry)

        fixture.given(restaurantOrderCreationInitiatedEvent) //Creation initialized ,but not yet CREATED
                .`when`(markRestaurantOrderAsPreparedCommand).expectException(UnsupportedOperationException::class.java)
    }
}
