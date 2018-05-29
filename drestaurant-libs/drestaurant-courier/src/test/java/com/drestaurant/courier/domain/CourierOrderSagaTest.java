package com.drestaurant.courier.domain;

import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;

public class CourierOrderSagaTest {

	private FixtureConfiguration testFixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";
	private String orderId;
	private String courierId;

	@Before
	public void setUp() throws Exception {
		testFixture = new SagaTestFixture<>(CourierOrderSaga.class);
		auditEntry = new AuditEntry(WHO);
		orderId = "orderId";
		courierId = "courierId";
	}
	
	@Test
	public void courierOrderAssigningInitiatedTest() throws Exception {

		testFixture.givenNoPriorActivity()
		           .whenAggregate(orderId)
				   .publishes(new CourierOrderAssigningInitiatedEvent(courierId, orderId, auditEntry))
				   .expectActiveSagas(1)
				   .expectDispatchedCommands(new ValidateOrderByCourierCommand(orderId, courierId, auditEntry));
	}
	@Test
	public void courierNotFoundTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new CourierOrderAssigningInitiatedEvent(courierId, orderId, auditEntry))
		           .whenPublishingA(new CourierNotFoundForOrderEvent (courierId, orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkCourierOrderAsNotAssignedCommand(orderId, auditEntry));
	}
	@Test
	public void orderValidatedWithErrorByCourierTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new CourierOrderAssigningInitiatedEvent(courierId, orderId, auditEntry))
		           .whenPublishingA(new OrderValidatedWithErrorByCourierEvent (courierId, orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkCourierOrderAsNotAssignedCommand(orderId, auditEntry));
	}
	@Test
	public void orderValidatedWithSuccessByCourierTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new CourierOrderAssigningInitiatedEvent(courierId, orderId, auditEntry))
		           .whenPublishingA(new OrderValidatedWithSuccessByCourierEvent (courierId, orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkCourierOrderAsAssignedCommand(orderId, courierId, auditEntry));
	}
	
}
