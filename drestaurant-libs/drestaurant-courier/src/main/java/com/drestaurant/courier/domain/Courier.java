package com.drestaurant.courier.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.drestaurant.common.domain.model.AuditEntry;
import com.drestaurant.common.domain.model.Money;
import com.drestaurant.common.domain.model.PersonName;
import com.drestaurant.courier.domain.api.CourierCreatedEvent;
import com.drestaurant.courier.domain.api.CreateCourierCommand;
import com.drestaurant.customer.domain.api.CustomerCreatedEvent;

/**
 *
 * Courier - aggregate root
 *
 * @author: idugalic 
 * Date: 5/2/18 Time: 11:21 PM
 */
@Aggregate
class Courier {

	/**
	 * Aggregates that are managed by Axon must have a unique identifier. The
	 * annotation 'AggregateIdentifier' identifies the id field as such.
	 */
	@AggregateIdentifier
	private String id;
	private PersonName name;
	private Integer maxNumberOfActiveOrders = 5;
	private Integer numberOfActiveOrders = 0;

	/**
	 * This default constructor is used by the Repository to construct a prototype
	 * {@link Courier}. Events are then used to set properties such as the
	 * Courier Id in order to make the Aggregate reflect it's true logical state.
	 */
	public Courier() {
	}
	
	/**
	 * This constructor is marked as a 'CommandHandler' for the
	 * {@link CreateCourierCommand}. This command can be used to construct new
	 * instances of the Aggregate. If successful a new {@link CourierCreatedEvent}
	 * is 'applied' to the aggregate using the Axon 'apply' method. The apply method
	 * appears to also propagate the Event to any other registered 'Event
	 * Listeners', who may take further action.
	 *
	 * @param command
	 */
	@CommandHandler
	public Courier(CreateCourierCommand command) {
		apply(new CourierCreatedEvent(command.getName(), command. getMaxNumberOfActiveOrders(), command.getTargetAggregateIdentifier(), command.getAuditEntry()));
	}
	
	@EventSourcingHandler
	public void on(CourierCreatedEvent event) {
		this.id = event.getAggregateIdentifier();
		this.name = event.getName();
		this.maxNumberOfActiveOrders = event.getMaxNumberOfActiveOrders();
		this.numberOfActiveOrders++;
	}
	
	public void validateOrder(String orderId, AuditEntry auditEntry) {
		if (numberOfActiveOrders+1>maxNumberOfActiveOrders) {
			apply(new OrderValidatedWithErrorByCourierEvent(this.id, orderId, auditEntry));

		} else {
			apply(new OrderValidatedWithSuccessByCourierEvent(this.id, orderId, auditEntry));
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
