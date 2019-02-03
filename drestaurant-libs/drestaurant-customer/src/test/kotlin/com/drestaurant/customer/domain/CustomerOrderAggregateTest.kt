package com.drestaurant.customer.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.customer.domain.api.CustomerOrderCreatedEvent
import com.drestaurant.customer.domain.api.CustomerOrderDeliveredEvent
import com.drestaurant.customer.domain.api.MarkCustomerOrderAsDeliveredCommand
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId
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
    private val orderId: CustomerOrderId = CustomerOrderId("orderId")
    private val customerId: CustomerId = CustomerId("customerId")
    private val orderTotal: Money = Money(BigDecimal.valueOf(100))

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(CustomerOrder::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())
    }

    @Test
    fun markOrderAsDeliveredTest() {
        val customerOrderCreatedEvent = CustomerOrderCreatedEvent(orderTotal, customerId, orderId, auditEntry)

        val markCustomerOrderAsDeliveredCommand = MarkCustomerOrderAsDeliveredCommand(orderId, auditEntry)
        val customerOrderDeliveredEvent = CustomerOrderDeliveredEvent(orderId, auditEntry)

        fixture
                .given(customerOrderCreatedEvent)
                .`when`(markCustomerOrderAsDeliveredCommand)
                .expectEvents(customerOrderDeliveredEvent)
    }
}
