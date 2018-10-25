package com.drestaurant.customer.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.customer.domain.api.*
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class CustomerOrderAggregateTest {

    private lateinit var fixture: FixtureConfiguration<CustomerOrder>
    private val who = "johndoe"
    private val auditEntry: AuditEntry = AuditEntry(who, Calendar.getInstance().time)
    private val orderId: String = "orderId"
    private val customerId: String = "customerId"
    private val orderTotal: Money = Money(BigDecimal.valueOf(100))

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(CustomerOrder::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())
    }

    @Test
    fun createCustomerOrderTest() {
        val createCustomerOrderCommand = CreateCustomerOrderCommand(orderId, orderTotal, customerId, auditEntry)
        val customerOrderCreationInitiatedEvent = CustomerOrderCreationInitiatedInternalEvent(orderTotal, customerId, orderId, auditEntry)

        fixture
                .given()
                .`when`(createCustomerOrderCommand)
                .expectEvents(customerOrderCreationInitiatedEvent)
    }

    @Test
    fun markOrderAsCreatedTest() {
        val customerOrderCreationInitiatedEvent = CustomerOrderCreationInitiatedInternalEvent(orderTotal, customerId, orderId, auditEntry)
        val markCustomerOrderAsCreatedCommand = MarkCustomerOrderAsCreatedInternalCommand(orderId, auditEntry)
        val customertOrderCreatedEvent = CustomerOrderCreatedEvent(orderId, auditEntry)

        fixture
                .given(customerOrderCreationInitiatedEvent)
                .`when`(markCustomerOrderAsCreatedCommand)
                .expectEvents(customertOrderCreatedEvent)
    }

    @Test
    fun markOrderAsRejectedTest() {
        val customerOrderCreationInitiatedEvent = CustomerOrderCreationInitiatedInternalEvent(orderTotal, customerId, orderId, auditEntry)
        val markCustomerOrderAsRejectedCommand = MarkCustomerOrderAsRejectedInternalCommand(orderId, auditEntry)
        val customerOrderRejectedEvent = CustomerOrderRejectedEvent(orderId, auditEntry)

        fixture
                .given(customerOrderCreationInitiatedEvent)
                .`when`(markCustomerOrderAsRejectedCommand)
                .expectEvents(customerOrderRejectedEvent)
    }

    @Test
    fun markOrderAsDeliveredTest() {
        val customerOrderCreationInitiatedEvent = CustomerOrderCreationInitiatedInternalEvent(orderTotal, customerId, orderId, auditEntry)
        val customertOrderCreatedEvent = CustomerOrderCreatedEvent(orderId, auditEntry)

        val markCustomerOrderAsDeliveredCommand = MarkCustomerOrderAsDeliveredCommand(orderId, auditEntry)
        val customerOrderDeliveredEvent = CustomerOrderDeliveredEvent(orderId, auditEntry)

        fixture
                .given(customerOrderCreationInitiatedEvent, customertOrderCreatedEvent)
                .`when`(markCustomerOrderAsDeliveredCommand)
                .expectEvents(customerOrderDeliveredEvent)
    }

}
