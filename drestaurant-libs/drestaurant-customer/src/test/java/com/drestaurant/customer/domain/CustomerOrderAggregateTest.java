package com.drestaurant.customer.domain;

import java.math.BigDecimal;

import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.customer.domain.CustomerOrder;
import com.drestaurant.customer.domain.CustomerOrderCreationInitiatedEvent;
import com.drestaurant.customer.domain.MarkCustomerOrderAsCreatedCommand;
import com.drestaurant.customer.domain.MarkCustomerOrderAsRejectedCommand;
import com.drestaurant.customer.domain.api.CreateCustomerOrderCommand;
import com.drestaurant.customer.domain.api.CustomerOrderCreatedEvent;
import com.drestaurant.customer.domain.api.CustomerOrderDeliveredEvent;
import com.drestaurant.customer.domain.api.CustomerOrderRejectedEvent;
import com.drestaurant.customer.domain.api.MarkCustomerOrderAsDeliveredCommand;

public class CustomerOrderAggregateTest {

	private FixtureConfiguration<CustomerOrder> fixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";
	
	private String orderId;
	private String customerId;
    private Money orderTotal;

	@Before
	public void setUp() throws Exception {
		fixture = new AggregateTestFixture<>(CustomerOrder.class);
		fixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
		auditEntry = new AuditEntry(WHO);
		orderId = "orderId";
		customerId = "customerId";
		orderTotal = new Money(BigDecimal.valueOf(100));
	}

	@Test
	public void createCustomerOrderTest() throws Exception {
		CreateCustomerOrderCommand createCustomerOrderCommand = new CreateCustomerOrderCommand(orderId, orderTotal,customerId, auditEntry);
		CustomerOrderCreationInitiatedEvent customerOrderCreationInitiatedEvent = new CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry);

		fixture
		.given()
		.when(createCustomerOrderCommand)
		.expectEvents(customerOrderCreationInitiatedEvent);
	}
	
	@Test
	public void markOrderAsCreatedTest() throws Exception {
		CustomerOrderCreationInitiatedEvent customerOrderCreationInitiatedEvent = new CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry);
		MarkCustomerOrderAsCreatedCommand markCustomerOrderAsCreatedCommand = new MarkCustomerOrderAsCreatedCommand(orderId, auditEntry);
		CustomerOrderCreatedEvent customertOrderCreatedEvent = new CustomerOrderCreatedEvent(orderId, auditEntry);
		
		fixture
		.given(customerOrderCreationInitiatedEvent)
		.when(markCustomerOrderAsCreatedCommand)
		.expectEvents(customertOrderCreatedEvent);
	}
	
	@Test
	public void markOrderAsRejectedTest() throws Exception {
		CustomerOrderCreationInitiatedEvent customerOrderCreationInitiatedEvent = new CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry);
		MarkCustomerOrderAsRejectedCommand markCustomerOrderAsRejectedCommand = new MarkCustomerOrderAsRejectedCommand(orderId, auditEntry);
		CustomerOrderRejectedEvent customerOrderRejectedEvent = new CustomerOrderRejectedEvent(orderId, auditEntry);
		
		fixture
		.given(customerOrderCreationInitiatedEvent)
		.when(markCustomerOrderAsRejectedCommand)
		.expectEvents(customerOrderRejectedEvent);
	}
	
	@Test
	public void markOrderAsDeliveredTest() throws Exception {
		CustomerOrderCreationInitiatedEvent customerOrderCreationInitiatedEvent = new CustomerOrderCreationInitiatedEvent(orderTotal, customerId, orderId, auditEntry);
		CustomerOrderCreatedEvent customertOrderCreatedEvent = new CustomerOrderCreatedEvent(orderId, auditEntry);

		MarkCustomerOrderAsDeliveredCommand markCustomerOrderAsDeliveredCommand = new MarkCustomerOrderAsDeliveredCommand(orderId, auditEntry);
		CustomerOrderDeliveredEvent customerOrderDeliveredEvent = new CustomerOrderDeliveredEvent(orderId, auditEntry);
		
		fixture
		.given(customerOrderCreationInitiatedEvent, customertOrderCreatedEvent)
		.when(markCustomerOrderAsDeliveredCommand)
		.expectEvents(customerOrderDeliveredEvent);
	}
	


}
