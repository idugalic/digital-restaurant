package com.drestaurant.restaurant.domain;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.restaurant.domain.MarkRestaurantOrderAsCreatedCommand;
import com.drestaurant.restaurant.domain.MarkRestaurantOrderAsRejectedCommand;
import com.drestaurant.restaurant.domain.RestaurantOrder;
import com.drestaurant.restaurant.domain.RestaurantOrderCreationInitiatedEvent;
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand;
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand;
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent;
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent;
import com.drestaurant.restaurant.domain.api.RestaurantOrderRejectedEvent;
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails;
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem;

public class RestaurantOrderAggregateTest {

	private FixtureConfiguration<RestaurantOrder> fixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";
	
	private String orderId;
	private String restuarantId;
	private RestaurantOrderLineItem lineItem;
	private List<RestaurantOrderLineItem> lineItems;
	private RestaurantOrderDetails orderDetails;

	@Before
	public void setUp() throws Exception {
		fixture = new AggregateTestFixture<>(RestaurantOrder.class);
		fixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
		auditEntry = new AuditEntry(WHO);
		
		orderId = "orderId";
		restuarantId = "restuarantId";
	    lineItem = new RestaurantOrderLineItem(1, "menuItemId", "name");
		lineItems = new ArrayList<>();
		lineItems.add(lineItem);
		orderDetails = new RestaurantOrderDetails(lineItems);
	}

	@Test
	public void createRestaurantOrderTest() throws Exception {
		CreateRestaurantOrderCommand createRestaurantOrderCommand = new CreateRestaurantOrderCommand(orderId, orderDetails, restuarantId, auditEntry);
		RestaurantOrderCreationInitiatedEvent restaurantOrderCreationInitiatedEvent = new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry);

		fixture
		.given()
		.when(createRestaurantOrderCommand)
		.expectEvents(restaurantOrderCreationInitiatedEvent);
	}
	
	@Test
	public void markOrderAsCreatedTest() throws Exception {
		RestaurantOrderCreationInitiatedEvent restaurantOrderCreationInitiatedEvent = new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry);
		MarkRestaurantOrderAsCreatedCommand markRestaurantOrderAsCreatedCommand = new MarkRestaurantOrderAsCreatedCommand(orderId, auditEntry);
		RestaurantOrderCreatedEvent restaurantOrderCreatedEvent = new RestaurantOrderCreatedEvent(orderId, auditEntry);
		
		fixture
		.given(restaurantOrderCreationInitiatedEvent)
		.when(markRestaurantOrderAsCreatedCommand)
		.expectEvents(restaurantOrderCreatedEvent);
	}
	
	@Test
	public void markOrderAsRejectedTest() throws Exception {
		RestaurantOrderCreationInitiatedEvent restaurantOrderCreationInitiatedEvent = new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry);
		MarkRestaurantOrderAsRejectedCommand markRestaurantOrderAsRejectedCommand = new MarkRestaurantOrderAsRejectedCommand(orderId, auditEntry);
		RestaurantOrderRejectedEvent restaurantOrderRejectedEvent = new RestaurantOrderRejectedEvent(orderId, auditEntry);
		
		fixture
		.given(restaurantOrderCreationInitiatedEvent)
		.when(markRestaurantOrderAsRejectedCommand)
		.expectEvents(restaurantOrderRejectedEvent);
	}
	@Test
	public void markOrderAsRejectedFaildTest() throws Exception {
		RestaurantOrderCreationInitiatedEvent restaurantOrderCreationInitiatedEvent = new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry);
		MarkRestaurantOrderAsRejectedCommand markRestaurantOrderAsRejectedCommand = new MarkRestaurantOrderAsRejectedCommand(orderId, auditEntry);
		RestaurantOrderRejectedEvent restaurantOrderRejectedEvent = new RestaurantOrderRejectedEvent(orderId, auditEntry);
		
		fixture
		.given(restaurantOrderCreationInitiatedEvent, restaurantOrderRejectedEvent) //Order already REJECTED
		.when(markRestaurantOrderAsRejectedCommand)
		.expectException(UnsupportedOperationException.class);
	}
	
	@Test
	public void markOrderAsPreparedTest() throws Exception {
		RestaurantOrderCreationInitiatedEvent restaurantOrderCreationInitiatedEvent = new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry);
		RestaurantOrderCreatedEvent restaurantOrderCreatedEvent = new RestaurantOrderCreatedEvent(orderId, auditEntry);

		MarkRestaurantOrderAsPreparedCommand markRestaurantOrderAsPreparedCommand = new MarkRestaurantOrderAsPreparedCommand(orderId, auditEntry);
		RestaurantOrderPreparedEvent restaurantOrderPreparedEvent = new RestaurantOrderPreparedEvent(orderId, auditEntry);

		fixture
		.given(restaurantOrderCreationInitiatedEvent, restaurantOrderCreatedEvent)
		.when(markRestaurantOrderAsPreparedCommand)
		.expectEvents(restaurantOrderPreparedEvent);
	}
	
	@Test
	public void markOrderAsPreparedFaildTest() throws Exception {
		RestaurantOrderCreationInitiatedEvent restaurantOrderCreationInitiatedEvent = new RestaurantOrderCreationInitiatedEvent(orderDetails, restuarantId, orderId, auditEntry);
		MarkRestaurantOrderAsPreparedCommand markRestaurantOrderAsPreparedCommand = new MarkRestaurantOrderAsPreparedCommand(orderId, auditEntry);

		fixture
		.given(restaurantOrderCreationInitiatedEvent) //Creation initialized ,but not yet CREATED
		.when(markRestaurantOrderAsPreparedCommand)
		.expectException(UnsupportedOperationException.class);
	}


}
