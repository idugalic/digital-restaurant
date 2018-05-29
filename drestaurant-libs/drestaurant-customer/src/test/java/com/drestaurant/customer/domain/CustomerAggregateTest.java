package com.drestaurant.customer.domain;

import java.math.BigDecimal;

import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.common.domain.model.PersonName;
import com.drestaurant.customer.domain.api.CreateCustomerCommand;
import com.drestaurant.customer.domain.api.CustomerCreatedEvent;

public class CustomerAggregateTest {

	private FixtureConfiguration<Customer> fixture;
	private AuditEntry auditEntry;
	private AuditEntry auditEntry2;
	private static final String WHO = "johndoe";
	private Money orderLimit = new Money(BigDecimal.valueOf(1000000));

	@Before
	public void setUp() throws Exception {
		fixture = new AggregateTestFixture<>(Customer.class);
		fixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
		auditEntry = new AuditEntry(WHO);
		auditEntry2 = new AuditEntry(WHO + "2");
	}

	@Test
	public void createCustomerTest() throws Exception {
		PersonName name = new PersonName("Ivan", "Dugalic");
		CreateCustomerCommand createCustomerCommand = new CreateCustomerCommand(name ,orderLimit, auditEntry);
		CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent(name, orderLimit, createCustomerCommand.getTargetAggregateIdentifier(), auditEntry);

		fixture
		.given()
		.when(createCustomerCommand)
		.expectEvents(customerCreatedEvent);
	}

	@Test(expected = AxonAssertionError.class)
	public void createCustomerAxonAssertionErrorTest() throws Exception {
		PersonName name = new PersonName("Ivan", "Dugalic");
		CreateCustomerCommand createCustomerCommand = new CreateCustomerCommand(name, orderLimit, auditEntry);
		// Setting the WRONG auditEntry
		CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent(name, orderLimit, createCustomerCommand.getTargetAggregateIdentifier(), auditEntry2);

		fixture
		.given()
		.when(createCustomerCommand)
		.expectException(AxonAssertionError.class);

	}

	@Test(expected = JSR303ViolationException.class)
	public void createCustomerJSR303ViolationTest() throws Exception {
		// Setting NULL for a name
		PersonName name = new PersonName(null, "Dugalic");
		CreateCustomerCommand createCustomerCommand = new CreateCustomerCommand(name, orderLimit, auditEntry);
		CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent(name, orderLimit, createCustomerCommand.getTargetAggregateIdentifier(), auditEntry);

		fixture
		.given()
		.when(createCustomerCommand)
		.expectException(JSR303ViolationException.class);
	}

}
