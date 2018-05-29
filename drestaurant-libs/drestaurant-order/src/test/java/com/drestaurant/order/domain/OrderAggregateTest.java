package com.drestaurant.order.domain;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.order.domain.api.CreateOrderCommand;
import com.drestaurant.order.domain.api.OrderCreationInitiatedEvent;
import com.drestaurant.order.domain.api.OrderDeliveredEvent;
import com.drestaurant.order.domain.api.OrderPreparedEvent;
import com.drestaurant.order.domain.api.OrderReadyForDeliveryEvent;
import com.drestaurant.order.domain.api.OrderRejectedEvent;
import com.drestaurant.order.domain.api.OrderVerifiedByCustomerEvent;
import com.drestaurant.order.domain.api.OrderVerifiedByRestaurantEvent;
import com.drestaurant.order.domain.model.OrderDetails;
import com.drestaurant.order.domain.model.OrderInfo;
import com.drestaurant.order.domain.model.OrderLineItem;


public class OrderAggregateTest {

	private FixtureConfiguration<Order> fixture;
	private AuditEntry auditEntry;
	private static final String WHO = "johndoe";
	
	private List<OrderLineItem> lineItems;
	private OrderLineItem lineItem1;
	private OrderLineItem lineItem2;
	private OrderInfo orderInfo;
	private OrderDetails orderDetails;
	private String orderId;

	@Before
	public void setUp() throws Exception {
		fixture = new AggregateTestFixture<>(Order.class);
		fixture.registerCommandDispatchInterceptor(new BeanValidationInterceptor<>());
		auditEntry = new AuditEntry(WHO);		
		lineItems = new ArrayList<OrderLineItem>();
		lineItem1 = new OrderLineItem("menuItemId1","name1", new Money(11), 2);
		lineItem2 = new OrderLineItem("menuItemId2","name2", new Money(22), 3);
		lineItems.add(lineItem1);
		lineItems.add(lineItem2);
		orderInfo = new OrderInfo("consumerId", "restaurantId", lineItems);
		orderDetails = new OrderDetails(orderInfo, lineItems.stream().map(OrderLineItem::getTotal).reduce(Money.ZERO, Money::add));
		orderId = "orderId";
	}

	// ########## CREATE ###########
	@Test
	public void createOrderTest() throws Exception {
		CreateOrderCommand createOrderCommand = new CreateOrderCommand(orderInfo, auditEntry);
		OrderCreationInitiatedEvent orderCreationInitiatedEvent = new OrderCreationInitiatedEvent(orderDetails, createOrderCommand.getTargetAggregateIdentifier(), createOrderCommand.getAuditEntry());
		
		fixture
				.given()
				.when(createOrderCommand)
				.expectEvents(orderCreationInitiatedEvent);
	}

	@Test (expected = JSR303ViolationException.class)
	public void createOrderJSR303ViolationTest() throws Exception {
		orderInfo = new OrderInfo(null, null, lineItems);
		CreateOrderCommand createOrderCommand = new CreateOrderCommand(orderInfo, auditEntry);

		fixture
				.given()
				.when(createOrderCommand)
				.expectException(JSR303ViolationException.class);
	}
	
	@Test
	public void markOrderAsVerifiedByCustomerCommandTest() throws Exception {
		OrderCreationInitiatedEvent orderCreationInitiatedEvent = new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry);
		MarkOrderAsVerifiedByCustomerCommand markOrderAsVerifiedByCustomerCommand = new MarkOrderAsVerifiedByCustomerCommand(orderCreationInitiatedEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getConsumerId(), auditEntry);
		OrderVerifiedByCustomerEvent orderVerifiedByCustomerEvent = new OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getConsumerId(), auditEntry);
		fixture
				.given(orderCreationInitiatedEvent)
				.when(markOrderAsVerifiedByCustomerCommand)
				.expectEvents(orderVerifiedByCustomerEvent);
	}

	@Test
	public void markOrderAsVerifiedByRestaurantCommandTest() throws Exception {
		OrderCreationInitiatedEvent orderCreationInitiatedEvent = new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry);
		OrderVerifiedByCustomerEvent orderVerifiedByCustomerEvent = new OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getConsumerId(), auditEntry);
		MarkOrderAsVerifiedByRestaurantCommand markOrderAsVerifiedByRestaurantCommand = new MarkOrderAsVerifiedByRestaurantCommand(orderVerifiedByCustomerEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getRestaurantId(), auditEntry);
		OrderVerifiedByRestaurantEvent orderVerifiedByRestaurantEvent = new OrderVerifiedByRestaurantEvent(orderVerifiedByCustomerEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getRestaurantId(), auditEntry);
		fixture
				.given(orderCreationInitiatedEvent, orderVerifiedByCustomerEvent)
				.when(markOrderAsVerifiedByRestaurantCommand)
				.expectEvents(orderVerifiedByRestaurantEvent);
	}

	@Test
	public void markOrderAsPreparedByRestaurantCommandTest() throws Exception {
		OrderCreationInitiatedEvent orderCreationInitiatedEvent = new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry);
		OrderVerifiedByCustomerEvent orderVerifiedByCustomerEvent = new OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getConsumerId(), auditEntry);
		OrderVerifiedByRestaurantEvent orderVerifiedByRestaurantEvent = new OrderVerifiedByRestaurantEvent(orderVerifiedByCustomerEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getRestaurantId(), auditEntry);
		MarkOrderAsPreparedCommand markOrderAsPreparedCommand = new MarkOrderAsPreparedCommand(orderVerifiedByRestaurantEvent.getAggregateIdentifier(), auditEntry);
		OrderPreparedEvent orderPreparedEvent = new OrderPreparedEvent(orderVerifiedByCustomerEvent.getAggregateIdentifier(), auditEntry);
		fixture
				.given(orderCreationInitiatedEvent, orderVerifiedByCustomerEvent, orderVerifiedByRestaurantEvent)
				.when(markOrderAsPreparedCommand)
				.expectEvents(orderPreparedEvent);
	}
	
	@Test
	public void markOrderAsReadyForDeliveryTest() throws Exception {
		OrderCreationInitiatedEvent orderCreationInitiatedEvent = new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry);
		OrderVerifiedByCustomerEvent orderVerifiedByCustomerEvent = new OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getConsumerId(), auditEntry);
		OrderVerifiedByRestaurantEvent orderVerifiedByRestaurantEvent = new OrderVerifiedByRestaurantEvent(orderVerifiedByCustomerEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getRestaurantId(), auditEntry);
		OrderPreparedEvent orderPreparedEvent = new OrderPreparedEvent(orderVerifiedByCustomerEvent.getAggregateIdentifier(), auditEntry);
		MarkOrderAsReadyForDeliveryCommand markOrderAsReadyForDeliveryCommand = new MarkOrderAsReadyForDeliveryCommand(orderPreparedEvent.getAggregateIdentifier(), auditEntry);
		OrderReadyForDeliveryEvent orderReadyForDeliveryEvent = new OrderReadyForDeliveryEvent(orderPreparedEvent.getAggregateIdentifier(), auditEntry);

		fixture
				.given(orderCreationInitiatedEvent, orderVerifiedByCustomerEvent, orderVerifiedByRestaurantEvent, orderPreparedEvent)
				.when(markOrderAsReadyForDeliveryCommand)
				.expectEvents(orderReadyForDeliveryEvent);
	}
	
	@Test
	public void markOrderAsDeliveredTest() throws Exception {
		OrderCreationInitiatedEvent orderCreationInitiatedEvent = new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry);
		OrderVerifiedByCustomerEvent orderVerifiedByCustomerEvent = new OrderVerifiedByCustomerEvent(orderCreationInitiatedEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getConsumerId(), auditEntry);
		OrderVerifiedByRestaurantEvent orderVerifiedByRestaurantEvent = new OrderVerifiedByRestaurantEvent(orderVerifiedByCustomerEvent.getAggregateIdentifier(), orderCreationInitiatedEvent.getOrderDetails().getRestaurantId(), auditEntry);
		OrderPreparedEvent orderPreparedEvent = new OrderPreparedEvent(orderVerifiedByCustomerEvent.getAggregateIdentifier(), auditEntry);
		OrderReadyForDeliveryEvent orderReadyForDeliveryEvent = new OrderReadyForDeliveryEvent(orderVerifiedByCustomerEvent.getAggregateIdentifier(), auditEntry);
		MarkOrderAsDeliveredCommand markOrderAsDeliveredCommand = new MarkOrderAsDeliveredCommand(orderReadyForDeliveryEvent.getAggregateIdentifier(), auditEntry);
		OrderDeliveredEvent orderDeliveredEvent = new OrderDeliveredEvent(orderReadyForDeliveryEvent.getAggregateIdentifier(), auditEntry);

		fixture
				.given(orderCreationInitiatedEvent, orderVerifiedByCustomerEvent, orderVerifiedByRestaurantEvent, orderPreparedEvent, orderReadyForDeliveryEvent)
				.when(markOrderAsDeliveredCommand)
				.expectEvents(orderDeliveredEvent);
	}
	
	// ########## REJECT ###########
	@Test
	public void markOrderAsRejectedCommandTest() throws Exception {
		OrderCreationInitiatedEvent orderCreationInitiatedEvent = new OrderCreationInitiatedEvent(orderDetails, orderId, auditEntry);
		MarkOrderAsRejectedCommand markOrderAsRejectedCommand = new MarkOrderAsRejectedCommand(orderCreationInitiatedEvent.getAggregateIdentifier(), auditEntry);
		OrderRejectedEvent orderVerifiedByCustomerEvent = new OrderRejectedEvent(orderCreationInitiatedEvent.getAggregateIdentifier(), auditEntry);
		fixture
				.given(orderCreationInitiatedEvent)
				.when(markOrderAsRejectedCommand)
				.expectEvents(orderVerifiedByCustomerEvent);
	}


}
