package com.drestaurant.customer.domain;

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
import com.drestaurant.customer.domain.api.CreateCustomerCommand;
import com.drestaurant.customer.domain.api.CustomerCreatedEvent;

/**
 *
 * Customer - aggregate root
 *
 * @author: idugalic 
 * Date: 5/2/18 
 * Time: 11:21 PM
 */

@Aggregate
class Customer {

	private static final Integer MILLION = 1000000;
	/**
	 * Aggregates that are managed by Axon must have a unique identifier. The
	 * annotation 'AggregateIdentifier' identifies the id field as such.
	 */
	@AggregateIdentifier
	private String id;
	private PersonName name;
	private Money orderLimit;

	/**
	 * This default constructor is used by the Repository to construct a prototype
	 * {@link Customer}. Events are then used to set properties such as the
	 * Customer's Id in order to make the Aggregate reflect it's true logical state.
	 */
	public Customer() {
	}

	/**
	 * This constructor is marked as a 'CommandHandler' for the
	 * {@link CreateCustomerCommand}. This command can be used to construct new
	 * instances of the Aggregate. If successful a new {@link CustomerCreatedEvent}
	 * is 'applied' to the aggregate using the Axon 'apply' method. The apply method
	 * appears to also propagate the Event to any other registered 'Event
	 * Listeners', who may take further action.
	 *
	 * @param command
	 */
	@CommandHandler
	public Customer(CreateCustomerCommand command) {
		apply(new CustomerCreatedEvent(command.getName(), command.getOrderLimit(), command.getTargetAggregateIdentifier(), command.getAuditEntry()));
	}

	public void validateOrder(String orderId, Money orderTotal, AuditEntry auditEntry) {
		if (orderTotal.isGreaterThanOrEqual(this.orderLimit)) {
			apply(new OrderValidatedWithErrorByCustomerEvent(this.id, orderId, orderTotal, auditEntry));

		} else {
			apply(new OrderValidatedWithSuccessByCustomerEvent(this.id, orderId, orderTotal, auditEntry));
		}
	}

	/**
	 * This method is marked as an EventSourcingHandler and is therefore used by the
	 * Axon framework to handle events of the specified type
	 * {@link CustomerCreatedEvent}. The {@link CustomerCreatedEvent} can be raised
	 * either by the constructor during Customer(CustomerCreatedEvent) or by the
	 * eventsourcing repository when 're-loading' the aggregate.
	 *
	 * @param event
	 */
	@EventSourcingHandler
	public void on(CustomerCreatedEvent event) {
		this.id = event.getAggregateIdentifier();
		this.name = event.getName();
		if (event.getOrderLimit() == null) {
			this.orderLimit = new Money(BigDecimal.valueOf(MILLION));
		} else {
			this.orderLimit = event.getOrderLimit();
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
