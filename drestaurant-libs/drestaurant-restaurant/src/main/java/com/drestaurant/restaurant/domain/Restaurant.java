package com.drestaurant.restaurant.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand;
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent;
import com.drestaurant.restaurant.domain.model.RestaurantMenu;
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem;
import com.drestaurant.restaurant.domain.model.RestaurantState;

/**
 *
 * Restaurant - aggregate root
 *
 * @author: idugalic 
 * Date: 5/15/18 
 * Time: 20:36 PM
 */

@Aggregate
class Restaurant {

	/**
	 * Aggregates that are managed by Axon must have a unique identifier. The
	 * annotation 'AggregateIdentifier' identifies the id field as such.
	 */
	@AggregateIdentifier
	private String id;
	private String name;
	private RestaurantMenu menu;
	private RestaurantState state;

	/**
	 * This default constructor is used by the Repository to construct a prototype
	 * {@link Restaurant}. Events are then used to set properties such as the
	 * Restaurant's Id in order to make the Aggregate reflect it's true logical
	 * state.
	 */
	public Restaurant() {
	}

	/**
	 * This constructor is marked as a 'CommandHandler' for the
	 * {@link CreateRestaurantCommand}. This command can be used to construct new
	 * instances of the Aggregate. If successful a new {@link RestaurantCreatedEvent}
	 * is 'applied' to the aggregate using the Axon 'apply' method. The apply method
	 * appears to also propagate the Event to any other registered 'Event
	 * Listeners', who may take further action.
	 *
	 * @param command
	 */
	@CommandHandler
	public Restaurant(CreateRestaurantCommand command) {
		apply(new RestaurantCreatedEvent(command.getName(), command.getMenu(), command.getTargetAggregateIdentifier(), command.getAuditEntry()));
	}
	
	/**
	 * This method is marked as an EventSourcingHandler and is therefore used by the
	 * Axon framework to handle events of the specified type
	 * {@link RestaurantCreatedEvent}. The {@link RestaurantCreatedEvent} can be raised
	 * either by the constructor during Restaurant(CreateRestaurantCommand) or by the
	 * eventsourcing repository when 're-loading' the aggregate.
	 *
	 * @param event
	 */
	@EventSourcingHandler
	public void on(RestaurantCreatedEvent event) {
		this.id = event.getAggregateIdentifier();
		this.name = event.getName();
		this.menu = event.getMenu();
		this.state = RestaurantState.OPEN;
	}
	/**
	 * Validate order by restaurant. 
	 * Checking if order contain line items that are on the menu only.
	 * 
	 * @param orderId
	 * @param lineItems
	 * @param auditEntry
	 */
	public void validateOrder(String orderId, List<RestaurantOrderLineItem> lineItems, AuditEntry auditEntry) {
		if (menu.getMenuItems().stream().map(mi->mi.getId()).collect(Collectors.toList())
				.containsAll(lineItems.stream().map(li->li.getMenuItemId()).collect(Collectors.toList()))) {
			apply(new OrderValidatedWithSuccessByRestaurantEvent(this.id, orderId, auditEntry));

		} else {
			apply(new OrderValidatedWithErrorByRestaurantEvent(this.id, orderId, auditEntry));
		}
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
