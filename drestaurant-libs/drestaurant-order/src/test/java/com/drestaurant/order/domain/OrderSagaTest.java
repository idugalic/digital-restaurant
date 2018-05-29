package com.drestaurant.order.domain;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent;
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent;
import com.drestaurant.courier.domain.api.CreateCourierOrderCommand;
import com.drestaurant.customer.domain.api.CreateCustomerOrderCommand;
import com.drestaurant.customer.domain.api.CustomerOrderCreatedEvent;
import com.drestaurant.customer.domain.api.CustomerOrderRejectedEvent;
import com.drestaurant.customer.domain.api.MarkCustomerOrderAsDeliveredCommand;
import com.drestaurant.order.domain.api.OrderCreationInitiatedEvent;
import com.drestaurant.order.domain.api.OrderDeliveredEvent;
import com.drestaurant.order.domain.api.OrderPreparedEvent;
import com.drestaurant.order.domain.api.OrderVerifiedByCustomerEvent;
import com.drestaurant.order.domain.model.OrderDetails;
import com.drestaurant.order.domain.model.OrderInfo;
import com.drestaurant.order.domain.model.OrderLineItem;
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand;
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent;
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent;
import com.drestaurant.restaurant.domain.api.RestaurantOrderRejectedEvent;
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails;
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem;

public class OrderSagaTest {

	private FixtureConfiguration testFixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";
	private String orderId;
	private String customerId;
	private String restaurantId;
	
	private List<OrderLineItem> lineItems;
	private OrderLineItem lineItem1;
	private OrderLineItem lineItem2;
	private OrderInfo orderInfo;
	private OrderDetails orderDetails;
	private List<RestaurantOrderLineItem> restaurantLineItems;
	private RestaurantOrderDetails restaurantOrderDetails;

	@Before
	public void setUp() throws Exception {
		testFixture = new SagaTestFixture<>(OrderSaga.class);
		orderId = "orderId";
		customerId = "customerId";
		restaurantId = "restaurantId";
		
		auditEntry = new AuditEntry(WHO);		
		lineItems = new ArrayList<OrderLineItem>();
		lineItem1 = new OrderLineItem("menuItemId1","name1", new Money(11), 2);
		lineItem2 = new OrderLineItem("menuItemId2","name2", new Money(22), 3);
		lineItems.add(lineItem1);
		lineItems.add(lineItem2);
		orderInfo = new OrderInfo(customerId, restaurantId, lineItems);
		orderDetails = new OrderDetails(orderInfo, lineItems.stream().map(OrderLineItem::getTotal).reduce(Money.ZERO, Money::add));
		
		restaurantLineItems = new ArrayList<>();
		for(OrderLineItem oli : this.orderDetails.getLineItems()) {
			RestaurantOrderLineItem roli = new RestaurantOrderLineItem(oli.getQuantity(), oli.getMenuItemId(), oli.getName());
			restaurantLineItems.add(roli);
		}
	    restaurantOrderDetails = new RestaurantOrderDetails(restaurantLineItems);
		
	}
	
	@Test
	public void orderCreationInitiatedTest() throws Exception {

		testFixture.givenNoPriorActivity()
		           .whenAggregate(orderId)
				   .publishes(
						   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
						   )
				   .expectActiveSagas(1)
				   .expectDispatchedCommands(new CreateCustomerOrderCommand("customerOrder_" + orderId, orderDetails.getOrderTotal(), customerId, auditEntry));
	}
	
	@Test
	public void customerOrderCreatedTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
		        		   )
		           .whenPublishingA(new CustomerOrderCreatedEvent("customerOrder_" + orderId, auditEntry))
		           .expectActiveSagas(1)
		           .expectDispatchedCommands(new MarkOrderAsVerifiedByCustomerCommand(orderId, customerId, auditEntry));
	}
	
	@Test
	public void orderVerifiedByCustomerTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
		        		   )
		           .whenPublishingA(new OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry))
		           .expectActiveSagas(1)
		           .expectDispatchedCommands(new CreateRestaurantOrderCommand("restaurantOrder_" + orderId, restaurantOrderDetails, restaurantId, auditEntry));
	}
	
	@Test
	public void restaurantOrderCreatedEventTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
		        		   new CustomerOrderCreatedEvent("customerOrder_" + orderId, auditEntry),
		        		   new OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry)
		        		   )
		           .whenPublishingA(new RestaurantOrderCreatedEvent("restaurantOrder_" + orderId, auditEntry))
		           .expectActiveSagas(1)
		           .expectDispatchedCommands(new MarkOrderAsVerifiedByRestaurantCommand(orderId, restaurantId, auditEntry));
	}
	
	@Test
	public void restaurantOrderPreparedEventTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
		        		   new CustomerOrderCreatedEvent("customerOrder_" + orderId, auditEntry),
		        		   new OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
		        		   new RestaurantOrderCreatedEvent("restaurantOrder_"+ orderId, auditEntry)
		        		   )
		           .whenPublishingA(new RestaurantOrderPreparedEvent("restaurantOrder_" + orderId, auditEntry))
		           .expectActiveSagas(1)
		           .expectDispatchedCommands(new MarkOrderAsPreparedCommand(orderId, auditEntry));
	}
	@Test
	public void orderPreparedEventTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
		        		   new CustomerOrderCreatedEvent("customerOrder_" + orderId, auditEntry),
		        		   new OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
		        		   new RestaurantOrderCreatedEvent("restaurantOrder_" + orderId, auditEntry),
		        		   new RestaurantOrderPreparedEvent("restaurantOrder_" + orderId, auditEntry)
		        		   )
		           .whenPublishingA(new OrderPreparedEvent(orderId, auditEntry))
		           .expectActiveSagas(1)
		           .expectDispatchedCommands(new CreateCourierOrderCommand("courierOrder_" + orderId, auditEntry));
	}
	@Test
	public void courierOrderCreatedEventTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
		        		   new CustomerOrderCreatedEvent("customerOrder_" + orderId, auditEntry),
		        		   new OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
		        		   new RestaurantOrderCreatedEvent("restaurantOrder_" + orderId, auditEntry),
		        		   new RestaurantOrderPreparedEvent("restaurantOrder_" + orderId, auditEntry),
		        		   new OrderPreparedEvent(orderId, auditEntry)
		        		   )
		           .whenPublishingA(new CourierOrderCreatedEvent("courierOrder_" + orderId, auditEntry))
		           .expectActiveSagas(1)
		           .expectDispatchedCommands(new MarkOrderAsReadyForDeliveryCommand(orderId, auditEntry));
	}
	
	@Test
	public void courierOrderDeliveredEventTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
		        		   new CustomerOrderCreatedEvent("customerOrder_" + orderId, auditEntry),
		        		   new OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
		        		   new RestaurantOrderCreatedEvent("restaurantOrder_" + orderId, auditEntry),
		        		   new RestaurantOrderPreparedEvent("restaurantOrder_" + orderId, auditEntry),
		        		   new OrderPreparedEvent(orderId, auditEntry),
		        		   new CourierOrderCreatedEvent("courierOrder_" + orderId, auditEntry)
		        		   )
		           .whenPublishingA(new CourierOrderDeliveredEvent("courierOrder_" + orderId, auditEntry))
		           .expectActiveSagas(1)
		           .expectDispatchedCommands(new MarkOrderAsDeliveredCommand(orderId, auditEntry));
	}
	
	@Test
	public void customerOrderDeliveredEventTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
		        		   new CustomerOrderCreatedEvent("customerOrder_" + orderId, auditEntry),
		        		   new OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry),
		        		   new RestaurantOrderCreatedEvent("restaurantOrder_" + orderId, auditEntry),
		        		   new RestaurantOrderPreparedEvent("restaurantOrder_" + orderId, auditEntry),
		        		   new OrderPreparedEvent(orderId, auditEntry),
		        		   new CourierOrderCreatedEvent("courierOrder_" + orderId, auditEntry),
		        		   new CourierOrderDeliveredEvent("courierOrder_" + orderId, auditEntry)
		        		   )
		           .whenPublishingA(new OrderDeliveredEvent(orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkCustomerOrderAsDeliveredCommand("customerOrder_" + orderId, auditEntry));
	}
	
	@Test
	public void customerOrderRejectedEventTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry)
		        		   )
		           .whenPublishingA(new CustomerOrderRejectedEvent("customerOrder_" + orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkOrderAsRejectedCommand(orderId, auditEntry));
	}
	
	@Test
	public void restaurantOrderRejectedEventTest() throws Exception {

		testFixture.givenAggregate(orderId)
		           .published(
		        		   new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry),
		        		   new CustomerOrderCreatedEvent("customerOrder_" + orderId, auditEntry),
		        		   new OrderVerifiedByCustomerEvent(orderId, customerId, auditEntry)
		        		   )
		           .whenPublishingA(new RestaurantOrderRejectedEvent("restaurantOrder_" + orderId, auditEntry))
		           .expectActiveSagas(0)
		           .expectDispatchedCommands(new MarkOrderAsRejectedCommand(orderId, auditEntry));
	}
}
