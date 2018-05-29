package com.drestaurant.order.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.drestaurant.common.domain.model.Money;
import com.drestaurant.order.domain.api.CreateOrderCommand;
import com.drestaurant.order.domain.api.OrderCreationInitiatedEvent;
import com.drestaurant.order.domain.api.OrderDeliveredEvent;
import com.drestaurant.order.domain.api.OrderPreparedEvent;
import com.drestaurant.order.domain.api.OrderReadyForDeliveryEvent;
import com.drestaurant.order.domain.api.OrderRejectedEvent;
import com.drestaurant.order.domain.api.OrderVerifiedByCustomerEvent;
import com.drestaurant.order.domain.api.OrderVerifiedByRestaurantEvent;
import com.drestaurant.order.domain.model.OrderDetails;
import com.drestaurant.order.domain.model.OrderLineItem;
import com.drestaurant.order.domain.model.OrderState;

/**
 *
 * Order - aggregate root
 *
 * @author: idugalic
 * Date: 4/29/18
 * Time: 12:21 PM
 */

@Aggregate
class Order {

	/**
	 * Aggregates that are managed by Axon must have a unique identifier. The annotation 'AggregateIdentifier'
	 * identifies the id field as such.
	 */
	@AggregateIdentifier
	private String id;

	private List<OrderLineItem> lineItems;
	private String restaurantId;
	private String consumerId;
	private OrderState state;

	/**
	 * This default constructor is used by the Repository to construct a prototype
	 * {@link Order}. Events are then used to set properties such as the
	 * Order's Id in order to make the Aggregate reflect it's true
	 * logical state.
	 */
	public Order() {
	}

	// CREATE
	/**
	 * This constructor is marked as a 'CommandHandler' for the {@link CreateOrderCommand}.
	 * This command can be used to construct new instances of the Aggregate. If
	 * successful a new {@link OrderCreationInitiatedEvent} is 'applied' to the aggregate using the
	 * Axon 'apply' method. The apply method appears to also propagate the Event to
	 * any other registered 'Event Listeners', who may take further action.
	 *
	 * @param command
	 */
	@CommandHandler
	public Order(CreateOrderCommand command) {
		apply(new OrderCreationInitiatedEvent(new OrderDetails(command.getOrderInfo(), this.calculateOrderTotal(command.getOrderInfo().getLineItems())), command.getTargetAggregateIdentifier(), command.getAuditEntry()));
	}

	/**
	 * This method is marked as an EventSourcingHandler and is therefore used by the
	 * Axon framework to handle events of the specified type {@link OrderCreatedEvent}.
	 * The {@link OrderCreationInitiatedEvent} can be raised either by the constructor during
	 * Order(CreateOrderCommand) or by the evensourcing zepository when 're-loading' the aggregate.
	 *
	 * @param event
	 */
	@EventSourcingHandler
	public void on(OrderCreationInitiatedEvent event) {
		this.id = event.getAggregateIdentifier();
		this.consumerId = event.getOrderDetails().getConsumerId();
		this.restaurantId = event.getOrderDetails().getRestaurantId();
		this.lineItems = event.getOrderDetails().getLineItems();
		this.state = OrderState.CREATE_PENDING;
	}
	
	@CommandHandler
	public void markOrderAsVerifiedByCustomer(MarkOrderAsVerifiedByCustomerCommand command) {
		if (OrderState.CREATE_PENDING.equals(state)) {
			apply(new OrderVerifiedByCustomerEvent(command.getTargetAggregateIdentifier(), command.getCustomerId(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not CREATE_PENDING");
		}
	}
	
	@EventSourcingHandler
	public void on(OrderVerifiedByCustomerEvent event) {
		this.state = OrderState.VERIFIED_BY_CUSTOMER;
	}
	
	@CommandHandler
	public void markOrderAsVerifiedByRestaurant(MarkOrderAsVerifiedByRestaurantCommand command) {
		if (OrderState.VERIFIED_BY_CUSTOMER.equals(state)) {
			apply(new OrderVerifiedByRestaurantEvent(command.getTargetAggregateIdentifier(), command.getRestaurantId(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not VERIFIED_BY_CUSTOMER");
		}
	}
	
	@EventSourcingHandler
	public void on(OrderVerifiedByRestaurantEvent event) {
		this.state = OrderState.VERIFIED_BY_RESTAURANT;
	}
	
	@CommandHandler
	public void markOrderAsPrepared(MarkOrderAsPreparedCommand command) {
		if (OrderState.VERIFIED_BY_RESTAURANT.equals(state)) {
			apply(new OrderPreparedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not VERIFIED_BY_RESTAURANT");
		}
	}
	@EventSourcingHandler
	public void on(OrderPreparedEvent event) {
		this.state = OrderState.PREPARED;
	}
	
	@CommandHandler
	public void markOrderAsReadyForDelivery(MarkOrderAsReadyForDeliveryCommand command) {
		if (OrderState.PREPARED.equals(state)) {
			apply(new OrderReadyForDeliveryEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not PREPARED");
		}
	}
	@EventSourcingHandler
	public void on(OrderReadyForDeliveryEvent event) {
		this.state = OrderState.READY_FOR_DELIVERY;
	}
	
	@CommandHandler
	public void markOrderAsDelivered(MarkOrderAsDeliveredCommand command) {
		if (OrderState.READY_FOR_DELIVERY.equals(state)) {
			apply(new OrderDeliveredEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not READY_FOR_DELIVERY");
		}
	}
	@EventSourcingHandler
	public void on(OrderDeliveredEvent event) {
		this.state = OrderState.DELIVERED;
	}
	// REJECT
	@CommandHandler
	public void markOrderAsRejected(MarkOrderAsRejectedCommand command) {
		if (OrderState.VERIFIED_BY_CUSTOMER.equals(state) || OrderState.CREATE_PENDING.equals(state)) {
			apply(new OrderRejectedEvent(command.getTargetAggregateIdentifier(), command.getAuditEntry()));
		} else {
			throw new UnsupportedOperationException("The current state is not VERIFIED_BY_CUSTOMER or CREATE_PENDING");
		}
	}
	@EventSourcingHandler
	public void on(OrderRejectedEvent event) {
		this.state = OrderState.REJECTED;
	}

	private Money calculateOrderTotal(List<OrderLineItem> lineItems) {
		return lineItems.stream().map(OrderLineItem::getTotal).reduce(Money.ZERO, Money::add);
	}

	public Money getOrderTotal() {
		return this.calculateOrderTotal(this.lineItems);
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
