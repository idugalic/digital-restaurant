package com.drestaurant.customer.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.drestaurant.common.domain.model.Money;
import com.drestaurant.customer.domain.api.CreateCustomerOrderCommand;
import com.drestaurant.customer.domain.api.CustomerOrderCreatedEvent;
import com.drestaurant.customer.domain.api.CustomerOrderDeliveredEvent;
import com.drestaurant.customer.domain.api.CustomerOrderRejectedEvent;
import com.drestaurant.customer.domain.api.MarkCustomerOrderAsDeliveredCommand;
import com.drestaurant.customer.domain.model.CustomerOrderState;

@Aggregate
class CustomerOrder {

	@AggregateIdentifier
	private String id;
	private String customerId;
	private CustomerOrderState state;
	private Money orderTotal;

	public CustomerOrder() {
	}

	@CommandHandler
	public CustomerOrder(CreateCustomerOrderCommand command) {
		apply(new CustomerOrderCreationInitiatedEvent(command.getOrderTotal(), command.getCustomerId(), command.getTargetAggregateIdentifier(), command.getAuditEntry()));
	}

	@EventSourcingHandler
	public void on(CustomerOrderCreationInitiatedEvent event) {
		this.id = event.getAggregateIdentifier();
		this.customerId = event.getCustomerId();
		this.orderTotal = event.getOrderTotal();
		this.state = CustomerOrderState.CREATE_PENDING;
	}

	@CommandHandler
	public void markOrderAsCreated(MarkCustomerOrderAsCreatedCommand command) {
		if (CustomerOrderState.CREATE_PENDING.equals(state)) {
			apply(new CustomerOrderCreatedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not CREATE_PENDING");
		}

	}

	@EventSourcingHandler
	public void on(CustomerOrderCreatedEvent event) {
		this.state = CustomerOrderState.CREATED;
	}

	@CommandHandler
	public void markOrderAsRejected(MarkCustomerOrderAsRejectedCommand command) {
		if (CustomerOrderState.CREATE_PENDING.equals(state)) {
			apply(new CustomerOrderRejectedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not CREATE_PENDING");
		}
	}

	@EventSourcingHandler
	public void on(CustomerOrderRejectedEvent event) {
		this.state = CustomerOrderState.REJECTED;
	}
	
	@CommandHandler
	public void markOrderAsDelivered(MarkCustomerOrderAsDeliveredCommand command) {
		if (CustomerOrderState.CREATED.equals(state)) {
			apply(new CustomerOrderDeliveredEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not CREATED");
		}
	}

	@EventSourcingHandler
	public void on(CustomerOrderDeliveredEvent event) {
		this.state = CustomerOrderState.DELIVERED;
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
