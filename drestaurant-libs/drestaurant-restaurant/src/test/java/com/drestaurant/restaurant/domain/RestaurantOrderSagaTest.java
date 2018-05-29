package com.drestaurant.restaurant.domain;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.restaurant.domain.MarkRestaurantOrderAsCreatedCommand;
import com.drestaurant.restaurant.domain.MarkRestaurantOrderAsRejectedCommand;
import com.drestaurant.restaurant.domain.RestaurantOrderCreationInitiatedEvent;
import com.drestaurant.restaurant.domain.RestaurantOrderSaga;
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails;
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem;

public class RestaurantOrderSagaTest {

	private FixtureConfiguration testFixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";
	private String orderId;
	private String restuarantId;
	private RestaurantOrderLineItem lineItem;
	private List<RestaurantOrderLineItem> lineItems;
	private RestaurantOrderDetails orderDetails;

	@Before
	public void setUp() throws Exception {
		testFixture = new SagaTestFixture<>(RestaurantOrderSaga.class);
		auditEntry = new AuditEntry(WHO);
		orderId = "orderId";
		restuarantId = "restuarantId";
	    lineItem = new RestaurantOrderLineItem(1, "menuItemId", "name");
		lineItems = new ArrayList<>();
		lineItems.add(lineItem);
		orderDetails = new RestaurantOrderDetails(lineItems);
	}
	
	@Test
	public void restaurantOrderCreationInitiatedTest() throws Exception {

		testFixture.givenNoPriorActivity()
		           .whenAggregate(orderId)
				   .publishes(new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry))
				   .expectActiveSagas(1)
				   .expectDispatchedCommands(new ValidateOrderByRestaurantCommand(orderId, restuarantId, orderDetails.getLineItems(), auditEntry));
	}
	
	@Test
	public void restaurantNotFoundTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry))
		           .whenPublishingA(new RestaurantNotFoundForOrderEvent (restuarantId, orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkRestaurantOrderAsRejectedCommand(orderId, auditEntry));
	}

	@Test
	public void restaurantOrderNotValidAndRejected() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry))
		           .whenPublishingA(new OrderValidatedWithErrorByRestaurantEvent (restuarantId, orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkRestaurantOrderAsRejectedCommand(orderId, auditEntry));
	}
	
	@Test
	public void restaurantOrderValidAndCreated() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry))
		           .whenPublishingA(new OrderValidatedWithSuccessByRestaurantEvent (restuarantId, orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkRestaurantOrderAsCreatedCommand(orderId, auditEntry));
	}
}
