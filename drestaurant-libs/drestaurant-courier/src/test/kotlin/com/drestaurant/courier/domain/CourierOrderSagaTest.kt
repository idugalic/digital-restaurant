package com.drestaurant.courier.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.util.*

class CourierOrderSagaTest {

    private lateinit var testFixture: FixtureConfiguration
    private lateinit var auditEntry: AuditEntry
    private lateinit var orderId: CourierOrderId
    private lateinit var courierId: CourierId
    private val who = "johndoe"

    @Before
    fun setUp() {
        testFixture = SagaTestFixture(CourierOrderSaga::class.java)
        auditEntry = AuditEntry(who, Calendar.getInstance().time)
        orderId = CourierOrderId("orderId")
        courierId = CourierId("courierId")
    }

    @Test
    fun courierOrderAssigningInitiatedTest2() {

        testFixture.givenNoPriorActivity()
                .whenAggregate(orderId.toString())
                .publishes(CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(ValidateOrderByCourierInternalCommand(orderId, courierId, auditEntry))
    }

    @Test
    fun courierNotFoundTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .whenPublishingA(CourierNotFoundForOrderInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCourierOrderAsNotAssignedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun orderValidatedWithErrorByCourierTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .whenPublishingA(CourierValidatedOrderWithErrorInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCourierOrderAsNotAssignedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun orderValidatedWithSuccessByCourierTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry))
                .whenPublishingA(CourierValidatedOrderWithSuccessInternalEvent(courierId, orderId, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCourierOrderAsAssignedInternalCommand(orderId, courierId, auditEntry))
    }

}
