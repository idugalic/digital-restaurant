package com.drestaurant.courier.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.drestaurant.courier.domain.api.AssignCourierOrderToCourierCommand;
import com.drestaurant.courier.domain.api.CourierOrderAssignedEvent;
import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent;
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent;
import com.drestaurant.courier.domain.api.CreateCourierOrderCommand;
import com.drestaurant.courier.domain.api.MarkCourierOrderAsDeliveredCommand;
import com.drestaurant.courier.domain.model.CourierOrderState;

@Aggregate
class CourierOrder {

	@AggregateIdentifier
	private String id;
	private String cuourierId;
	private CourierOrderState state;

	public CourierOrder() {
	}

	@CommandHandler
	public CourierOrder(CreateCourierOrderCommand command) {
		apply(new CourierOrderCreatedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
	}

	@EventSourcingHandler
	public void on(CourierOrderCreatedEvent event) {
		this.id = event.getAggregateIdentifier();
		this.state = CourierOrderState.CREATED;
	}

	@CommandHandler
	public void assignToCourier(AssignCourierOrderToCourierCommand command) {
		if (CourierOrderState.CREATED.equals(state)) {
			apply(new CourierOrderAssigningInitiatedEvent(command.getCourierId(), command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not CREATED");
		}
	}

	@EventSourcingHandler
	public void on(CourierOrderAssigningInitiatedEvent event) {
		this.state = CourierOrderState.ASSIGN_PENDING;
	}

	@CommandHandler
	public void markOrderAsAssigned(MarkCourierOrderAsAssignedCommand command) {
		if (CourierOrderState.ASSIGN_PENDING.equals(state)) {
			apply(new CourierOrderAssignedEvent(command.getTargetAggregateIdentifier(), command.getCourierId(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not ASSIGN_PENDING");
		}
	}

	@EventSourcingHandler
	public void on(CourierOrderAssignedEvent event) {
		this.cuourierId = event.getCourierId();
		this.state = CourierOrderState.ASSIGNED;
	}
	
	@CommandHandler
	public void markOrderAsNotAssigned(MarkCourierOrderAsNotAssignedCommand command) {
		if (CourierOrderState.ASSIGN_PENDING.equals(state)) {
			apply(new CourierOrderNotAssignedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not ASSIGN_PENDING");
		}
	}
	
	@EventSourcingHandler
	public void on(CourierOrderNotAssignedEvent event) {
		this.state = CourierOrderState.CREATED;
	}
	
	@CommandHandler
	public void markOrderAsDelivered(MarkCourierOrderAsDeliveredCommand command) {
		if (CourierOrderState.ASSIGNED.equals(state)) {
			apply(new CourierOrderDeliveredEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not ASSIGNED");
		}
	}

	@EventSourcingHandler
	public void on(CourierOrderDeliveredEvent event) {
		this.state = CourierOrderState.DELIVERED;
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
