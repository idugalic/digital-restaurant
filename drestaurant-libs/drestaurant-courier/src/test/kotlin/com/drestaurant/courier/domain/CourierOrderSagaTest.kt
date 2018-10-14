package com.drestaurant.courier.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.courier.domain.api.CreateCourierOrderCommand
import com.drestaurant.order.domain.api.CourierOrderCreationRequestedEvent
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.util.*

class CourierOrderSagaTest {

    private lateinit var testFixture: FixtureConfiguration
    private lateinit var auditEntry: AuditEntry
    private lateinit var orderId: String
    private lateinit var courierId: String
    private val WHO = "johndoe"

    @Before
    fun setUp() {
        testFixture = SagaTestFixture(CourierOrderSaga::class.java)
        auditEntry = AuditEntry(WHO, Calendar.getInstance().time)
        orderId = "orderId"
        courierId = "courierId"
    }

    @Test
    fun courierOrderCreationRequestedTest() {

        testFixture.givenNoPriorActivity()
                .whenAggregate(orderId)
                .publishes(CourierOrderCreationRequestedEvent(orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(CreateCourierOrderCommand(orderId, auditEntry))
    }

    @Test
    fun courierOrderAssigningInitiatedTest() {

        testFixture.givenAggregate(orderId)
                .published(CourierOrderCreationRequestedEvent(orderId, auditEntry))
                .whenPublishingA(CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(ValidateOrderByCourierInternalCommand(orderId, courierId, auditEntry))
    }

    @Test
    fun courierOrderAssigningInitiatedTest2() {

        testFixture.givenNoPriorActivity()
                .whenAggregate(orderId)
                .publishes(CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(ValidateOrderByCourierInternalCommand(orderId, courierId, auditEntry))
    }

    @Test
    fun courierNotFoundTest() {

        testFixture.givenAggregate(orderId)
                .published(CourierOrderCreationRequestedEvent(orderId, auditEntry), CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .whenPublishingA(CourierNotFoundForOrderInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCourierOrderAsNotAssignedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun orderValidatedWithErrorByCourierTest() {

        testFixture.givenAggregate(orderId)
                .published(CourierOrderCreationRequestedEvent(orderId, auditEntry), CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .whenPublishingA(OrderValidatedWithErrorByCourierInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCourierOrderAsNotAssignedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun orderValidatedWithSuccessByCourierTest() {

        testFixture.givenAggregate(orderId)
                .published(CourierOrderCreationRequestedEvent(orderId, auditEntry), CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .whenPublishingA(OrderValidatedWithSuccessByCourierInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCourierOrderAsAssignedInternalCommand(orderId, courierId, auditEntry))
    }

}
