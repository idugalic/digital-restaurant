package com.drestaurant.courier.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.courier.domain.api.*
import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.util.*

class CourierOrderAggregateTest {

    private lateinit var fixture: FixtureConfiguration<CourierOrder>
    private lateinit var auditEntry: AuditEntry

    private lateinit var orderId: CourierOrderId
    private lateinit var courierId: CourierId

    private val who = "johndoe"

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(CourierOrder::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())
        auditEntry = AuditEntry(who, Calendar.getInstance().time)
        orderId = CourierOrderId("orderId")
        courierId = CourierId("courierId")
    }

    @Test
    fun createCourierOrderTest() {
        val createCourierOrderCommand = CreateCourierOrderCommand(orderId, auditEntry)
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId, auditEntry)

        fixture
                .given()
                .`when`(createCourierOrderCommand)
                .expectEvents(courierOrderCreatedEvent)
    }

    @Test
    fun assignOdrerToCourierTest() {
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId, auditEntry)
        val assignCourierOrderToCourierCommand = AssignCourierOrderToCourierCommand(orderId, courierId, auditEntry)
        val courierOrderAssigningInitiatedEvent = CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry)

        fixture
                .given(courierOrderCreatedEvent)
                .`when`(assignCourierOrderToCourierCommand)
                .expectEvents(courierOrderAssigningInitiatedEvent)
    }

    @Test
    fun markOrderAsAssignedTest() {
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId, auditEntry)
        val markCourierOrderAsAssignedCommand = MarkCourierOrderAsAssignedInternalCommand(orderId, courierId, auditEntry)
        val courierOrderAssigningInitiatedEvent = CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry)
        val courierOrderAssignedEvent = CourierOrderAssignedEvent(orderId, courierId, auditEntry)

        fixture
                .given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent)
                .`when`(markCourierOrderAsAssignedCommand)
                .expectEvents(courierOrderAssignedEvent)
    }

    @Test
    fun markOrderAsNotAssignedTest() {
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId, auditEntry)
        val markCourierOrderAsNotAssignedCommand = MarkCourierOrderAsNotAssignedInternalCommand(orderId, auditEntry)
        val courierOrderAssigningInitiatedEvent = CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry)
        val courierOrderNotAssignedEvent = CourierOrderNotAssignedEvent(orderId, auditEntry)

        fixture
                .given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent)
                .`when`(markCourierOrderAsNotAssignedCommand)
                .expectEvents(courierOrderNotAssignedEvent)
    }

    @Test
    fun markOrderAsDeliveredTest() {
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId, auditEntry)
        val courierOrderAssigningInitiatedEvent = CourierOrderAssigningInitiatedInternalEvent(courierId, orderId, auditEntry)
        val courierOrderAssignedEvent = CourierOrderAssignedEvent(orderId, courierId, auditEntry)
        val markCourierOrderAsDeliveredCommand = MarkCourierOrderAsDeliveredCommand(orderId, auditEntry)
        val courierOrderDeliveredEvent = CourierOrderDeliveredEvent(orderId, auditEntry)
        fixture
                .given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent, courierOrderAssignedEvent)
                .`when`(markCourierOrderAsDeliveredCommand)
                .expectEvents(courierOrderDeliveredEvent)
    }

}
