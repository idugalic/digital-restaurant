package com.drestaurant.order.domain;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.SagaLifecycle;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.drestaurant.order.domain.model.OrderLineItem;
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand;
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent;
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent;
import com.drestaurant.restaurant.domain.api.RestaurantOrderRejectedEvent;
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails;
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem;

@Saga
public class OrderSaga {
	
	@Autowired
	private transient CommandGateway commandGateway;
	private String restaurantId;
	private String customerId;
	private OrderDetails orderDetails;
	private String orderId;
	
	// CREATE
	@StartSaga
	@SagaEventHandler(associationProperty = "aggregateIdentifier")
	public void on(OrderCreationInitiatedEvent event) {
		this.orderId = event.getAggregateIdentifier();
		
		String customerOrderId = "customerOrder_" + this.orderId;
	    SagaLifecycle.associateWith("customerOrderId", customerOrderId);
	    
	    this.restaurantId = event.getOrderDetails().getRestaurantId();
		this.customerId = event.getOrderDetails().getConsumerId();
		this.orderDetails = event.getOrderDetails();
		
		CreateCustomerOrderCommand command = new CreateCustomerOrderCommand(customerOrderId, event.getOrderDetails().getOrderTotal(), this.customerId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}

	@SagaEventHandler(associationProperty = "aggregateIdentifier", keyName="customerOrderId")
	public void on(CustomerOrderCreatedEvent event) {
		MarkOrderAsVerifiedByCustomerCommand command = new MarkOrderAsVerifiedByCustomerCommand(this.orderId, this.customerId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@SagaEventHandler(associationProperty = "aggregateIdentifier")
	public void on(OrderVerifiedByCustomerEvent event) {
		String restaurantOrderId = "restaurantOrder_" + this.orderId;
	    SagaLifecycle.associateWith("restaurantOrderId", restaurantOrderId);
	    
		List<RestaurantOrderLineItem> restaurantLineItems = new ArrayList<>();
		for(OrderLineItem oli : this.orderDetails.getLineItems()) {
			RestaurantOrderLineItem roli = new RestaurantOrderLineItem(oli.getQuantity(), oli.getMenuItemId(), oli.getName());
			restaurantLineItems.add(roli);
		}
		RestaurantOrderDetails restaurantOrderDetails = new RestaurantOrderDetails(restaurantLineItems);
		
		CreateRestaurantOrderCommand command = new CreateRestaurantOrderCommand(restaurantOrderId, restaurantOrderDetails, this.restaurantId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@SagaEventHandler(associationProperty = "aggregateIdentifier", keyName="restaurantOrderId")
	public void on(RestaurantOrderCreatedEvent event) {
		MarkOrderAsVerifiedByRestaurantCommand command = new MarkOrderAsVerifiedByRestaurantCommand(this.orderId, this.restaurantId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	// RESTAURANT prepared
	@SagaEventHandler(associationProperty = "aggregateIdentifier", keyName="restaurantOrderId")
	public void on(RestaurantOrderPreparedEvent event) {
		MarkOrderAsPreparedCommand command = new MarkOrderAsPreparedCommand(this.orderId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@SagaEventHandler(associationProperty = "aggregateIdentifier")
	public void on(OrderPreparedEvent event) {
		String courierOrderId = "courierOrder_" + this.orderId;
	    SagaLifecycle.associateWith("courierOrderId", courierOrderId);
	    
		CreateCourierOrderCommand command = new CreateCourierOrderCommand(courierOrderId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@SagaEventHandler(associationProperty = "aggregateIdentifier", keyName="courierOrderId")
	public void on(CourierOrderCreatedEvent event) {
		MarkOrderAsReadyForDeliveryCommand command = new MarkOrderAsReadyForDeliveryCommand(this.orderId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	// DELIVERY 
	
	@SagaEventHandler(associationProperty = "aggregateIdentifier", keyName="courierOrderId")
	public void on(CourierOrderDeliveredEvent event) {
		MarkOrderAsDeliveredCommand command = new MarkOrderAsDeliveredCommand(this.orderId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "aggregateIdentifier")
	public void on(OrderDeliveredEvent event) {
		MarkCustomerOrderAsDeliveredCommand command = new MarkCustomerOrderAsDeliveredCommand("customerOrder_" + this.orderId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	// REJECT
	@EndSaga
	@SagaEventHandler(associationProperty = "aggregateIdentifier", keyName="customerOrderId")
	public void on(CustomerOrderRejectedEvent event) {
		MarkOrderAsRejectedCommand command = new MarkOrderAsRejectedCommand(this.orderId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "aggregateIdentifier", keyName="restaurantOrderId")
	public void on(RestaurantOrderRejectedEvent event) {
		MarkOrderAsRejectedCommand command = new MarkOrderAsRejectedCommand(this.orderId, event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}

}
