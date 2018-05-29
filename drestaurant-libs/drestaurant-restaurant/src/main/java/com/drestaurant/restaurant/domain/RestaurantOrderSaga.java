package com.drestaurant.restaurant.domain;

import static org.axonframework.eventhandling.saga.SagaLifecycle.associateWith;

import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class RestaurantOrderSaga {
	
	@Autowired
	private transient CommandGateway commandGateway;
	private String orderId;
	
	@StartSaga
	@SagaEventHandler(associationProperty = "aggregateIdentifier")
	public void on(RestaurantOrderCreationInitiatedEvent event) {
		this.orderId = event.getAggregateIdentifier();
		associateWith("orderId", this.orderId);
		ValidateOrderByRestaurantCommand command = new ValidateOrderByRestaurantCommand(this.orderId, event.getRestaurantId(), event.getOrderDetails().getLineItems(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(RestaurantNotFoundForOrderEvent event) {
		MarkRestaurantOrderAsRejectedCommand command = new MarkRestaurantOrderAsRejectedCommand(event.getOrderId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderValidatedWithSuccessByRestaurantEvent event) {
		MarkRestaurantOrderAsCreatedCommand command = new MarkRestaurantOrderAsCreatedCommand(event.getOrderId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderValidatedWithErrorByRestaurantEvent event) {
		MarkRestaurantOrderAsRejectedCommand command = new MarkRestaurantOrderAsRejectedCommand(event.getOrderId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	

}
