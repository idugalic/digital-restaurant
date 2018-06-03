package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.util.*

class RestaurantOrderSagaTest {

    private var testFixture: FixtureConfiguration? = null

    private val orderId: String = "orderId"
    private val restuarantId: String = "restuarantId"
    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "name")
    private var lineItems: MutableList<RestaurantOrderLineItem> = ArrayList()
    private val orderDetails: RestaurantOrderDetails = RestaurantOrderDetails(lineItems)
    private val WHO = "johndoe"
    private val auditEntry: AuditEntry = AuditEntry(WHO)


    @Before
    fun setUp() {
        testFixture = SagaTestFixture(RestaurantOrderSaga::class.java)
        lineItems!!.add(lineItem)
    }

    @Test
    fun restaurantOrderCreationInitiatedTest() {

        testFixture!!.givenNoPriorActivity().whenAggregate(orderId)
                .publishes(RestaurantOrderCreationInitiatedEvent(orderDetails!!, restuarantId!!, orderId!!, auditEntry!!)).expectActiveSagas(1)
                .expectDispatchedCommands(ValidateOrderByRestaurantCommand(orderId!!, restuarantId!!, orderDetails!!.getLineItems(), auditEntry!!))
    }

    @Test
    fun restaurantNotFoundTest() {

        testFixture!!.givenAggregate(orderId).published(RestaurantOrderCreationInitiatedEvent(orderDetails!!, restuarantId!!, orderId!!, auditEntry!!))
                .whenPublishingA(RestaurantNotFoundForOrderEvent(restuarantId!!, orderId!!, auditEntry!!)).expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsRejectedCommand(orderId!!, auditEntry!!))
    }

    @Test
    fun restaurantOrderNotValidAndRejected() {

        testFixture!!.givenAggregate(orderId).published(RestaurantOrderCreationInitiatedEvent(orderDetails!!, restuarantId!!, orderId!!, auditEntry!!))
                .whenPublishingA(OrderValidatedWithErrorByRestaurantEvent(restuarantId!!, orderId!!, auditEntry!!)).expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsRejectedCommand(orderId!!, auditEntry!!))
    }

    @Test
    fun restaurantOrderValidAndCreated() {

        testFixture!!.givenAggregate(orderId).published(RestaurantOrderCreationInitiatedEvent(orderDetails!!, restuarantId!!, orderId!!, auditEntry!!))
                .whenPublishingA(OrderValidatedWithSuccessByRestaurantEvent(restuarantId!!, orderId!!, auditEntry!!)).expectActiveSagas(0)
                .expectDispatchedCommands(MarkRestaurantOrderAsCreatedCommand(orderId!!, auditEntry!!))
    }
}
