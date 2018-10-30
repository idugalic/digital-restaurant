package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderLineItem
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.util.*

class RestaurantOrderSagaTest {

    private lateinit var testFixture: FixtureConfiguration

    private var orderId: RestaurantOrderId = RestaurantOrderId("orderId")
    private var restuarantId: RestaurantId = RestaurantId("restuarantId")
    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "name")
    private var lineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private val orderDetails: RestaurantOrderDetails = RestaurantOrderDetails(lineItems)
    private val who = "johndoe"
    private val auditEntry: AuditEntry = AuditEntry(who, Calendar.getInstance().time)


    @Before
    fun setUp() {

        testFixture = SagaTestFixture(RestaurantOrderSaga::class.java)
        lineItems.add(lineItem)
    }

    @Test
    fun restaurantOrderCreationInitiatedTest2() {

        testFixture.givenNoPriorActivity()
                .whenAggregate(orderId.toString())
                .publishes(RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(ValidateOrderByRestaurantInternalCommand(orderId, restuarantId, orderDetails.lineItems, auditEntry))
    }

    @Test
    fun restaurantNotFoundTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry))
                .whenPublishingA(RestaurantNotFoundForOrderInternalEvent(restuarantId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsRejectedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun restaurantOrderNotValidAndRejected() {

        testFixture.givenAggregate(orderId.toString())
                .published(RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry))
                .whenPublishingA(RestaurantValidatedOrderWithErrorInternalEvent(restuarantId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsRejectedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun restaurantOrderValidAndCreated() {

        testFixture.givenAggregate(orderId.toString())
                .published(RestaurantOrderCreationInitiatedInternalEvent(orderDetails, restuarantId, orderId, auditEntry))
                .whenPublishingA(RestaurantValidatedOrderWithSuccessInternalEvent(restuarantId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsCreatedInternalCommand(orderId, auditEntry))
    }
}
