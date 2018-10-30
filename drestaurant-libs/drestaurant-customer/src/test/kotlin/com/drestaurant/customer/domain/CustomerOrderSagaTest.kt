package com.drestaurant.customer.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class CustomerOrderSagaTest {

    private lateinit var testFixture: FixtureConfiguration
    private val who = "johndoe"
    private val auditEntry: AuditEntry = AuditEntry(who, Calendar.getInstance().time)
    private val orderId: CustomerOrderId = CustomerOrderId("orderId")
    private val customerId: CustomerId = CustomerId("customerId")
    private val orderTotal: Money = Money(BigDecimal.valueOf(100))

    @Before
    fun setUp() {
        testFixture = SagaTestFixture(CustomerOrderSaga::class.java)
    }

    @Test
    fun customerOrderCreationInitiatedTest2() {

        testFixture.givenNoPriorActivity()
                .whenAggregate(orderId.toString())
                .publishes(CustomerOrderCreationInitiatedInternalEvent(orderTotal, customerId, orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(ValidateOrderByCustomerInternalCommand(orderId, customerId, orderTotal, auditEntry))
    }

    @Test
    fun customerNotFoundTest() {

        testFixture.givenAggregate(orderId.toString())
                .published(CustomerOrderCreationInitiatedInternalEvent(orderTotal, customerId, orderId, auditEntry))
                .whenPublishingA(CustomerNotFoundForOrderInternalEvent(customerId, orderId, orderTotal, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCustomerOrderAsRejectedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun customerOrderNotValidAndRejected() {

        testFixture.givenAggregate(orderId.toString())
                .published(CustomerOrderCreationInitiatedInternalEvent(orderTotal, customerId, orderId, auditEntry))
                .whenPublishingA(CustomerValidatedOrderWithErrorInternalEvent(customerId, orderId, orderTotal, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCustomerOrderAsRejectedInternalCommand(orderId, auditEntry))
    }

    @Test
    fun customerOrderValidAndCreated() {

        testFixture.givenAggregate(orderId.toString())
                .published(CustomerOrderCreationInitiatedInternalEvent(orderTotal, customerId, orderId, auditEntry))
                .whenPublishingA(CustomerValidatedOrderWithSuccessInternalEvent(customerId, orderId, orderTotal, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCustomerOrderAsCreatedInternalCommand(orderId, auditEntry))
    }

}
