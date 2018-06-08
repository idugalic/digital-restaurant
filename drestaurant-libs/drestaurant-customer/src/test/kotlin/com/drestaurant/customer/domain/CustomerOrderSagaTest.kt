package com.drestaurant.customer.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.Money
import org.axonframework.test.saga.FixtureConfiguration
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class CustomerOrderSagaTest {

    private lateinit var testFixture: FixtureConfiguration
    private val WHO = "johndoe"
    private val auditEntry: AuditEntry = AuditEntry(WHO, Calendar.getInstance().time)
    private val orderId: String = "orderId"
    private val customerId: String = "customerId"
    private val orderTotal: Money = Money(BigDecimal.valueOf(100))

    @Before
    fun setUp() {
        testFixture = SagaTestFixture(CustomerOrderSaga::class.java)
    }

    @Test
    fun customerOrderCreationInitiatedTest() {

        testFixture.givenNoPriorActivity()
                .whenAggregate(orderId)
                .publishes(CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry))
                .expectActiveSagas(1)
                .expectDispatchedCommands(ValidateOrderByCustomerCommand(orderId, customerId, orderTotal, auditEntry))
    }

    @Test
    fun customerNotFoundTest() {

        testFixture.givenAggregate(orderId)
                .published(CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry))
                .whenPublishingA(CustomerNotFoundForOrderEvent(customerId, orderId, orderTotal, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCustomerOrderAsRejectedCommand(orderId, auditEntry))
    }

    @Test
    fun customerOrderNotValidAndRejected() {

        testFixture.givenAggregate(orderId)
                .published(CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry))
                .whenPublishingA(OrderValidatedWithErrorByCustomerEvent(customerId, orderId, orderTotal, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCustomerOrderAsRejectedCommand(orderId, auditEntry))
    }

    @Test
    fun customerOrderValidAndCreated() {

        testFixture.givenAggregate(orderId)
                .published(CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry))
                .whenPublishingA(OrderValidatedWithSuccessByCustomerEvent(customerId, orderId, orderTotal, auditEntry))
                .expectActiveSagas(0)
                .expectDispatchedCommands(MarkCustomerOrderAsCreatedCommand(orderId, auditEntry))
    }

}
