package com.drestaurant.query.handler;

import com.drestaurant.courier.domain.api.CourierCreatedEvent;
import com.drestaurant.query.model.CourierEntity;
import com.drestaurant.query.repository.CourierRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.SequenceNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ProcessingGroup("default") @Component class CourierEventHandler {

	private CourierRepository repository;

	@Autowired public CourierEventHandler(CourierRepository repository) {
		this.repository = repository;
	}

	@EventHandler public void handle(CourierCreatedEvent event, @SequenceNumber Long aggregateVersion) {
		repository.save(new CourierEntity(event.getAggregateIdentifier(), aggregateVersion, event.getName().getFirstName(), event.getName().getLastName(),
				event.getMaxNumberOfActiveOrders()));
	}

}
