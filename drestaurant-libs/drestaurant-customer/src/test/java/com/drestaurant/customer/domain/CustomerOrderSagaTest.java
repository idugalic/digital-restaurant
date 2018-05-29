package com.drestaurant.customer.domain;

import java.math.BigDecimal;

import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.customer.domain.CustomerOrderCreationInitiatedEvent;
import com.drestaurant.customer.domain.CustomerOrderSaga;
import com.drestaurant.customer.domain.MarkCustomerOrderAsCreatedCommand;
import com.drestaurant.customer.domain.MarkCustomerOrderAsRejectedCommand;

public class CustomerOrderSagaTest {

	private FixtureConfiguration testFixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";
	private String orderId;
	private String customerId;
	private Money orderTotal;

	@Before
	public void setUp() throws Exception {
		testFixture = new SagaTestFixture<>(CustomerOrderSaga.class);
		auditEntry = new AuditEntry(WHO);
		orderId = "orderId";
		customerId = "customerId";
		orderTotal = new Money(BigDecimal.valueOf(100));
	}
	
	@Test
	public void customerOrderCreationInitiatedTest() throws Exception {

		testFixture.givenNoPriorActivity()
		           .whenAggregate(orderId)
				   .publishes(new CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry))
				   .expectActiveSagas(1)
				   .expectDispatchedCommands(new ValidateOrderByCustomerCommand(orderId, customerId, orderTotal, auditEntry));
	}
	
	@Test
	public void customerNotFoundTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry))
		           .whenPublishingA(new CustomerNotFoundForOrderEvent (customerId, orderId, orderTotal, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkCustomerOrderAsRejectedCommand(orderId, auditEntry));
	}

	@Test
	public void customerOrderNotValidAndRejected() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry))
		           .whenPublishingA(new OrderValidatedWithErrorByCustomerEvent (customerId, orderId, orderTotal, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkCustomerOrderAsRejectedCommand(orderId, auditEntry));
	}
	
	@Test
	public void customerOrderValidAndCreated() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry))
		           .whenPublishingA(new OrderValidatedWithSuccessByCustomerEvent (customerId, orderId, orderTotal, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkCustomerOrderAsCreatedCommand(orderId, auditEntry));
	}
}
