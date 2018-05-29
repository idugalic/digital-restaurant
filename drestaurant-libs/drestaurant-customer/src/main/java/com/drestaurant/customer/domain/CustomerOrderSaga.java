package com.drestaurant.customer.domain;

import static org.axonframework.eventhandling.saga.SagaLifecycle.associateWith;

import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class CustomerOrderSaga {
	
	@Autowired
	private transient CommandGateway commandGateway;
	private String orderId;
	
	@StartSaga
	@SagaEventHandler(associationProperty = "aggregateIdentifier")
	public void on(CustomerOrderCreationInitiatedEvent event) {
		this.orderId = event.getAggregateIdentifier();
		associateWith("orderId", this.orderId);
		ValidateOrderByCustomerCommand command = new ValidateOrderByCustomerCommand(this.orderId, event.getCustomerId(), event.getOrderTotal(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(CustomerNotFoundForOrderEvent event) {
		MarkCustomerOrderAsRejectedCommand command = new MarkCustomerOrderAsRejectedCommand(event.getOrderId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderValidatedWithSuccessByCustomerEvent event) {
		MarkCustomerOrderAsCreatedCommand command = new MarkCustomerOrderAsCreatedCommand(event.getOrderId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderValidatedWithErrorByCustomerEvent event) {
		MarkCustomerOrderAsRejectedCommand command = new MarkCustomerOrderAsRejectedCommand(event.getOrderId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	

}
