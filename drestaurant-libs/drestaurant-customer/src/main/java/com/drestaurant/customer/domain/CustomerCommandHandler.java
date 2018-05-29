package com.drestaurant.customer.domain;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.AggregateNotFoundException;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;

public class CustomerCommandHandler {

	private Repository<Customer> repository;
	private EventBus eventBus;

	public CustomerCommandHandler(Repository<Customer> repository, EventBus eventBus) {
		this.repository = repository;
		this.eventBus = eventBus;
	}

	@CommandHandler
	public void handle(ValidateOrderByCustomerCommand command) {
		try {
			Aggregate<Customer> customerAggregate = repository.load(command.getCustomerId());
			customerAggregate.execute(customer -> customer.validateOrder(command.getOrderId(), command.getOrderTotal(), command.getAuditEntry()));
		} catch (AggregateNotFoundException exception) {
			eventBus.publish(asEventMessage(new CustomerNotFoundForOrderEvent(command.getCustomerId(), command.getOrderId(), command.getOrderTotal(), command.getAuditEntry())));
		}

	}

}
