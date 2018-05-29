package com.drestaurant.courier.domain;

import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.PersonName;
import com.drestaurant.courier.domain.Courier;
import com.drestaurant.courier.domain.api.CourierCreatedEvent;
import com.drestaurant.courier.domain.api.CreateCourierCommand;

public class CourierAggregateTest {

	private FixtureConfiguration<Courier> fixture;
	private AuditEntry auditEntry;
	private AuditEntry auditEntry2;
	private static final String WHO = "johndoe";
	private Integer maxNumberOfActiveOrders;

	@Before
	public void setUp() throws Exception {
		fixture = new AggregateTestFixture<>(Courier.class);
		fixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
		auditEntry = new AuditEntry(WHO);
		auditEntry2 = new AuditEntry(WHO + "2");
		maxNumberOfActiveOrders = 5;
	}

	@Test
	public void createCourierTest() throws Exception {
		PersonName name = new PersonName("Ivan", "Dugalic");
		CreateCourierCommand createCourierCommand = new CreateCourierCommand(name, maxNumberOfActiveOrders, auditEntry);
		CourierCreatedEvent courierCreatedEvent = new CourierCreatedEvent(name, maxNumberOfActiveOrders, createCourierCommand.getTargetAggregateIdentifier(), auditEntry);

		fixture
		.given()
		.when(createCourierCommand)
		.expectEvents(courierCreatedEvent);
	}

	@Test(expected = AxonAssertionError.class)
	public void createCourierAxonAssertionErrorTest() throws Exception {
		PersonName name = new PersonName("Ivan", "Dugalic");
		CreateCourierCommand createCourierCommand = new CreateCourierCommand(name, maxNumberOfActiveOrders, auditEntry);
		// Setting the WRONG auditEntry
		CourierCreatedEvent courierCreatedEvent = new CourierCreatedEvent(name, maxNumberOfActiveOrders, createCourierCommand.getTargetAggregateIdentifier(), auditEntry2);

		fixture
		.given()
		.when(createCourierCommand)
		.expectException(AxonAssertionError.class);

	}

	@Test(expected = JSR303ViolationException.class)
	public void createCourierJSR303ViolationTest() throws Exception {
		// Setting NULL for a name
		PersonName name = new PersonName(null, "Dugalic");
		CreateCourierCommand courierCustomerCommand = new CreateCourierCommand(name, maxNumberOfActiveOrders, auditEntry);

		fixture
		.given()
		.when(courierCustomerCommand)
		.expectException(JSR303ViolationException.class);
	}

}
