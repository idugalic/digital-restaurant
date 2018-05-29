package com.drestaurant.courier.domain;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.AggregateNotFoundException;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;

public class CourierCommandHandler {

	private Repository<Courier> repository;
	private EventBus eventBus;

	public CourierCommandHandler(Repository<Courier> repository, EventBus eventBus) {
		this.repository = repository;
		this.eventBus = eventBus;
	}

	@CommandHandler
	public void handle(ValidateOrderByCourierCommand command) {
		try {
			Aggregate<Courier> courierAggregate = repository.load(command.getCourierId());
			courierAggregate.execute(courier -> courier.validateOrder(command.getOrderId(), command.getAuditEntry()));
		} catch (AggregateNotFoundException exception) {
			eventBus.publish(asEventMessage(new CourierNotFoundForOrderEvent(command.getCourierId(), command.getOrderId(), command.getAuditEntry())));
		}
	}

}
