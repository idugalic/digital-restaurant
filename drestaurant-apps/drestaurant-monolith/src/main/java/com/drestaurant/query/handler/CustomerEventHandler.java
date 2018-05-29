package com.drestaurant.query.handler;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.SequenceNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drestaurant.customer.domain.api.CustomerCreatedEvent;
import com.drestaurant.query.model.CustomerEntity;
import com.drestaurant.query.repository.CustomerRepository;

@ProcessingGroup("default")
@Component
class CustomerEventHandler {

	private CustomerRepository repository;

	@Autowired
	public CustomerEventHandler(CustomerRepository repository) {
		this.repository = repository;
	}

	@EventHandler
	public void handle(CustomerCreatedEvent event, @SequenceNumber Long aggregateVersion) {
		repository.save(new CustomerEntity(event.getAggregateIdentifier(), aggregateVersion, event.getName().getFirstName(), event.getName().getLastName(), event.getOrderLimit().getAmount()));
	}

}
