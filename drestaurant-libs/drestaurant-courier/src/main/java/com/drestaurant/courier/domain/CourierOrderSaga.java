package com.drestaurant.courier.domain;

import static org.axonframework.eventhandling.saga.SagaLifecycle.associateWith;

import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class CourierOrderSaga {
	
	@Autowired
	private transient CommandGateway commandGateway;
	private String orderId;
	
	@StartSaga
	@SagaEventHandler(associationProperty = "aggregateIdentifier")
	public void on(CourierOrderAssigningInitiatedEvent event) {
		this.orderId = event.getAggregateIdentifier();
		associateWith("orderId", this.orderId);
		ValidateOrderByCourierCommand command = new ValidateOrderByCourierCommand(this.orderId, event.getCourierId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(CourierNotFoundForOrderEvent event) {
		MarkCourierOrderAsNotAssignedCommand command = new MarkCourierOrderAsNotAssignedCommand(event.getOrderId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderValidatedWithSuccessByCourierEvent event) {
		MarkCourierOrderAsAssignedCommand command = new MarkCourierOrderAsAssignedCommand(event.getOrderId(), event.getCourierId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderValidatedWithErrorByCourierEvent event) {
		MarkCourierOrderAsNotAssignedCommand command = new MarkCourierOrderAsNotAssignedCommand(event.getOrderId(), event.getAuditEntry());
		commandGateway.send(command, LoggingCallback.INSTANCE);
	}
	
	

}
