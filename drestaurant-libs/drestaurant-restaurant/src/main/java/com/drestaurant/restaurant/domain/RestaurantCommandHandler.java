package com.drestaurant.restaurant.domain;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.AggregateNotFoundException;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;

class RestaurantCommandHandler {

	private Repository<Restaurant> repository;
	private EventBus eventBus;

	public RestaurantCommandHandler(Repository<Restaurant> repository, EventBus eventBus) {
		this.repository = repository;
		this.eventBus = eventBus;
	}

	@CommandHandler
	public void handle(ValidateOrderByRestaurantCommand command) {
		try {
			Aggregate<Restaurant> restaurantAggregate = repository.load(command.getRestaurantId());
			restaurantAggregate.execute(restaurant -> restaurant.validateOrder(command.getOrderId(), command.getLineItems(), command.getAuditEntry()));
		} catch (AggregateNotFoundException exception) {
			eventBus.publish(asEventMessage(new RestaurantNotFoundForOrderEvent(command.getRestaurantId(), command.getOrderId(), command.getAuditEntry())));
		}
	}
	
	

}
