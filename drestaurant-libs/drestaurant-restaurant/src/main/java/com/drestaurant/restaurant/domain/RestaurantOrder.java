package com.drestaurant.restaurant.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand;
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand;
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent;
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent;
import com.drestaurant.restaurant.domain.api.RestaurantOrderRejectedEvent;
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails;
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem;
import com.drestaurant.restaurant.domain.model.RestaurantOrderState;

@Aggregate
class RestaurantOrder {

	@AggregateIdentifier
	private String id;
	private String restaurantId;
	private RestaurantOrderState state;
	private List<RestaurantOrderLineItem> lineItems;

	public RestaurantOrder() {
	}

	@CommandHandler
	public RestaurantOrder(CreateRestaurantOrderCommand command) {
		apply(new RestaurantOrderCreationInitiatedEvent(
				new RestaurantOrderDetails(command.getOrderDetails().getLineItems()), command.getRestaurantId(),
				command.getTargetAggregateIdentifier(), command.getAuditEntry()));
	}

	@EventSourcingHandler
	public void on(RestaurantOrderCreationInitiatedEvent event) {
		this.id = event.getAggregateIdentifier();
		this.restaurantId = event.getRestaurantId();
		this.lineItems = event.getOrderDetails().getLineItems();
		this.state = RestaurantOrderState.CREATE_PENDING;
	}

	@CommandHandler
	public void markOrderAsCreated(MarkRestaurantOrderAsCreatedCommand command) {
		if (RestaurantOrderState.CREATE_PENDING.equals(state)) {
			apply(new RestaurantOrderCreatedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not CREATE_PENDING");
		}

	}

	@EventSourcingHandler
	public void on(RestaurantOrderCreatedEvent event) {
		this.state = RestaurantOrderState.CREATED;
	}

	@CommandHandler
	public void markOrderAsRejected(MarkRestaurantOrderAsRejectedCommand command) {
		if (RestaurantOrderState.CREATE_PENDING.equals(state)) {
			apply(new RestaurantOrderRejectedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not CREATE_PENDING");
		}
	}
	
	@EventSourcingHandler
	public void on(RestaurantOrderRejectedEvent event) {
		this.state = RestaurantOrderState.REJECTED;
	}
	
	@CommandHandler
	public void markOrderAsPrepared(MarkRestaurantOrderAsPreparedCommand command) {
		if (RestaurantOrderState.CREATED.equals(state)) {
			apply(new RestaurantOrderPreparedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not CREATED");
		}
	}
	
	@EventSourcingHandler
	public void on(RestaurantOrderPreparedEvent event) {
		this.state = RestaurantOrderState.PREPARED;
	}

	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
