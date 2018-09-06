package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.order.domain.api.RestaurantOrderCreationRequestedEvent
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.util.*

class RestaurantOrderSagaTest {

    private lateinit var testFixture: FixtureConfiguration

    private val orderId: String = "orderId"
    private val restuarantId: String = "restuarantId"
    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "name")
    private var lineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private val orderDetails: RestaurantOrderDetails = RestaurantOrderDetails(lineItems)
    private val WHO = "johndoe"
    private val auditEntry: AuditEntry = AuditEntry(WHO, Calendar.getInstance().time)


    @Before
    fun setUp() {

        testFixture = SagaTestFixture(RestaurantOrderSaga::class.java)
        lineItems.add(lineItem)
    }

    @Test
    fun restaurantOrderCreationRequestedTest() {

        testFixture.givenNoPriorActivity()
                .whenAggregate(orderId)
                .publishes(RestaurantOrderCreationRequestedEvent(orderId, orderDetails, restuarantId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(CreateRestaurantOrderCommand(orderId, orderDetails, restuarantId, auditEntry))
    }

    @Test
    fun restaurantOrderCreationInitiatedTest() {

        testFixture.givenAggregate(orderId)
                .published(RestaurantOrderCreationRequestedEvent(orderId, orderDetails, restuarantId, auditEntry))
                .whenPublishingA(RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(ValidateOrderByRestaurantCommand(orderId, restuarantId, orderDetails.lineItems, auditEntry))
    }

    @Test
    fun restaurantNotFoundTest() {

        testFixture.givenAggregate(orderId)
                .published(RestaurantOrderCreationRequestedEvent(orderId, orderDetails, restuarantId, auditEntry), RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry))
                .whenPublishingA(RestaurantNotFoundForOrderEvent(restuarantId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsRejectedCommand(orderId, auditEntry))
    }

    @Test
    fun restaurantOrderNotValidAndRejected() {

        testFixture.givenAggregate(orderId)
                .published(RestaurantOrderCreationRequestedEvent(orderId, orderDetails, restuarantId, auditEntry), RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry))
                .whenPublishingA(OrderValidatedWithErrorByRestaurantEvent(restuarantId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsRejectedCommand(orderId, auditEntry))
    }

    @Test
    fun restaurantOrderValidAndCreated() {

        testFixture.givenAggregate(orderId)
                .published(RestaurantOrderCreationRequestedEvent(orderId, orderDetails, restuarantId, auditEntry), RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry))
                .whenPublishingA(OrderValidatedWithSuccessByRestaurantEvent(restuarantId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsCreatedCommand(orderId, auditEntry))
    }
}
