package com.drestaurant.courier.domain;

import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand;
import com.drestaurant.courier.domain.api.CourierOrderAssignedEvent;
import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent;
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent;
import com.drestaurant.courier.domain.api.CreateCourierOrderCommand;
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand;

public class CourierOrderAggregateTest {

	private FixtureConfiguration<CourierOrder> fixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";
	
	private String orderId;
	private String courierId;

	@Before
	public void setUp() throws Exception {
		fixture = new AggregateTestFixture<>(CourierOrder.class);
		fixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
		auditEntry = new AuditEntry(WHO);
		orderId = "orderId";
		courierId = "courierId";
	}
	
	@Test
	public void createCourierOrderTest() throws Exception {
		CreateCourierOrderCommand createCourierOrderCommand = new CreateCourierOrderCommand(orderId, auditEntry);
		CourierOrderCreatedEvent courierOrderCreatedEvent = new CourierOrderCreatedEvent( orderId, auditEntry);

		fixture
		.given()
		.when(createCourierOrderCommand)
		.expectEvents(courierOrderCreatedEvent);
	}

	@Test
	public void assignOdrerToCourierTest() throws Exception {
		CourierOrderCreatedEvent courierOrderCreatedEvent = new CourierOrderCreatedEvent(orderId, auditEntry);
		AssignCourierOrderToCourierCommand assignCourierOrderToCourierCommand = new AssignCourierOrderToCourierCommand(orderId, courierId, auditEntry);
		CourierOrderAssigningInitiatedEvent courierOrderAssigningInitiatedEvent = new CourierOrderAssigningInitiatedEvent(courierId, orderId, auditEntry);

		fixture
		.given(courierOrderCreatedEvent)
		.when(assignCourierOrderToCourierCommand)
		.expectEvents(courierOrderAssigningInitiatedEvent);
	}
	
	@Test
	public void markOrderAsAssignedTest() throws Exception {
		CourierOrderCreatedEvent courierOrderCreatedEvent = new CourierOrderCreatedEvent(orderId, auditEntry);
		MarkCourierOrderAsAssignedCommand markCourierOrderAsAssignedCommand = new MarkCourierOrderAsAssignedCommand(orderId, courierId, auditEntry);
		CourierOrderAssigningInitiatedEvent courierOrderAssigningInitiatedEvent = new CourierOrderAssigningInitiatedEvent(courierId, orderId, auditEntry);
		CourierOrderAssignedEvent courierOrderAssignedEvent = new CourierOrderAssignedEvent(orderId, courierId, auditEntry);
		
		fixture
		.given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent)
		.when(markCourierOrderAsAssignedCommand)
		.expectEvents(courierOrderAssignedEvent);
	}
	
	@Test
	public void markOrderAsNotAssignedTest() throws Exception {
		CourierOrderCreatedEvent courierOrderCreatedEvent = new CourierOrderCreatedEvent(orderId, auditEntry);
		MarkCourierOrderAsNotAssignedCommand markCourierOrderAsNotAssignedCommand = new MarkCourierOrderAsNotAssignedCommand(orderId, auditEntry);
		CourierOrderAssigningInitiatedEvent courierOrderAssigningInitiatedEvent = new CourierOrderAssigningInitiatedEvent(courierId, orderId, auditEntry);
		CourierOrderNotAssignedEvent courierOrderNotAssignedEvent = new CourierOrderNotAssignedEvent(orderId, auditEntry);
		
		fixture
		.given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent)
		.when(markCourierOrderAsNotAssignedCommand)
		.expectEvents(courierOrderNotAssignedEvent);
	}
	
	@Test
	public void markOrderAsDeliveredTest() throws Exception {
		CourierOrderCreatedEvent courierOrderCreatedEvent = new CourierOrderCreatedEvent(orderId, auditEntry);
		CourierOrderAssigningInitiatedEvent courierOrderAssigningInitiatedEvent = new CourierOrderAssigningInitiatedEvent(courierId, orderId, auditEntry);
		CourierOrderAssignedEvent courierOrderAssignedEvent = new CourierOrderAssignedEvent(orderId, courierId, auditEntry);
		MarkCourierOrderAsDeliveredCommand markCourierOrderAsDeliveredCommand = new MarkCourierOrderAsDeliveredCommand(orderId, auditEntry); 
		CourierOrderDeliveredEvent courierOrderDeliveredEvent = new CourierOrderDeliveredEvent(orderId, auditEntry);
		fixture
		.given(courierOrderCreatedEvent, courierOrderAssigningInitiatedEvent, courierOrderAssignedEvent)
		.when(markCourierOrderAsDeliveredCommand)
		.expectEvents(courierOrderDeliveredEvent);
	}

}
