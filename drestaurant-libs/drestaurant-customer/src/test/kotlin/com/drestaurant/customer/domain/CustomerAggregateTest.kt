package com.drestaurant.customer.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.customer.domain.api.CreateCustomerCommand
import com.drestaurant.customer.domain.api.CustomerCreatedEvent
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.AxonAssertionError
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CustomerAggregateTest {

    private var fixture: FixtureConfiguration<Customer>? = null
    private val WHO = "johndoe"
    private val auditEntry: AuditEntry? = AuditEntry(WHO)
    private val auditEntry2: AuditEntry? = AuditEntry(WHO + "2")
    private val orderLimit = Money(BigDecimal.valueOf(1000000))

    @Before
    fun setUp() {
        fixture = AggregateTestFixture(Customer::class.java)
        fixture!!.registerCommandDispatchInterceptor(BeanValidationInterceptor())
    }

    @Test
    fun createCustomerTest() {
        val name = PersonName("Ivan", "Dugalic")
        val createCustomerCommand = CreateCustomerCommand(name, orderLimit, auditEntry!!)
        val customerCreatedEvent = CustomerCreatedEvent(name, orderLimit, createCustomerCommand.targetAggregateIdentifier, auditEntry!!)

        fixture!!
                .given()
                .`when`(createCustomerCommand)
                .expectEvents(customerCreatedEvent)
    }

    @Test(expected = AxonAssertionError::class)
    fun createCustomerAxonAssertionErrorTest() {
        val name = PersonName("Ivan", "Dugalic")
        val createCustomerCommand = CreateCustomerCommand(name, orderLimit, auditEntry!!)
        // Setting the WRONG auditEntry
        val customerCreatedEvent = CustomerCreatedEvent(name, orderLimit, createCustomerCommand.targetAggregateIdentifier, auditEntry2!!)

        fixture!!
                .given()
                .`when`(createCustomerCommand)
                .expectException(AxonAssertionError::class.java)

    }

}
