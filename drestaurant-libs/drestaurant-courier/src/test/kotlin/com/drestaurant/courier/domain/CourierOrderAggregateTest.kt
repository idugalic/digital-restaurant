package com.drestaurant.courier.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.courier.domain.api.*
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test

class CourierOrderAggregateTest {

    private var fixture: FixtureConfiguration<CourierOrder>? = null
    private var auditEntry: AuditEntry? = null

    private var orderId: String? = null
    private var courierId: String? = null

    private val WHO = "johndoe"

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(CourierOrder::class.java)
        fixture!!.registerCommandDispatchInterceptor(BeanValidationInterceptor())
        auditEntry = AuditEntry(WHO)
        orderId = "orderId"
        courierId = "courierId"
    }

    @Test
    fun createCourierOrderTest() {
        val createCourierOrderCommand = CreateCourierOrderCommand(orderId!!, auditEntry!!)
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId!!, auditEntry!!)

        fixture!!
                .given()
                .`when`(createCourierOrderCommand)
                .expectEvents(courierOrderCreatedEvent)
    }

    @Test
    fun assignOdrerToCourierTest() {
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId!!, auditEntry!!)
        val assignCourierOrderToCourierCommand = AssignCourierOrderToCourierCommand(orderId!!, courierId!!, auditEntry!!)
        val courierOrderAssigningInitiatedEvent = CourierOrderAssigningInitiatedEvent(courierId!!, orderId!!, auditEntry!!)

        fixture!!
                .given(courierOrderCreatedEvent)
                .`when`(assignCourierOrderToCourierCommand)
                .expectEvents(courierOrderAssigningInitiatedEvent)
    }

    @Test
    fun markOrderAsAssignedTest() {
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId!!, auditEntry!!)
        val markCourierOrderAsAssignedCommand = MarkCourierOrderAsAssignedCommand(orderId!!, courierId!!, auditEntry!!)
        val courierOrderAssigningInitiatedEvent = CourierOrderAssigningInitiatedEvent(courierId!!, orderId!!, auditEntry!!)
        val courierOrderAssignedEvent = CourierOrderAssignedEvent(orderId!!, courierId!!, auditEntry!!)

        fixture!!
                .given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent)
                .`when`(markCourierOrderAsAssignedCommand)
                .expectEvents(courierOrderAssignedEvent)
    }

    @Test
    fun markOrderAsNotAssignedTest() {
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId!!, auditEntry!!)
        val markCourierOrderAsNotAssignedCommand = MarkCourierOrderAsNotAssignedCommand(orderId!!, auditEntry!!)
        val courierOrderAssigningInitiatedEvent = CourierOrderAssigningInitiatedEvent(courierId!!, orderId!!, auditEntry!!)
        val courierOrderNotAssignedEvent = CourierOrderNotAssignedEvent(orderId!!, auditEntry!!)

        fixture!!
                .given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent)
                .`when`(markCourierOrderAsNotAssignedCommand)
                .expectEvents(courierOrderNotAssignedEvent)
    }

    @Test
    fun markOrderAsDeliveredTest() {
        val courierOrderCreatedEvent = CourierOrderCreatedEvent(orderId!!, auditEntry!!)
        val courierOrderAssigningInitiatedEvent = CourierOrderAssigningInitiatedEvent(courierId!!, orderId!!, auditEntry!!)
        val courierOrderAssignedEvent = CourierOrderAssignedEvent(orderId!!, courierId!!, auditEntry!!)
        val markCourierOrderAsDeliveredCommand = MarkCourierOrderAsDeliveredCommand(orderId!!, auditEntry!!)
        val courierOrderDeliveredEvent = CourierOrderDeliveredEvent(orderId!!, auditEntry!!)
        fixture!!
                .given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent, courierOrderAssignedEvent)
                .`when`(markCourierOrderAsDeliveredCommand)
                .expectEvents(courierOrderDeliveredEvent)
    }

}
