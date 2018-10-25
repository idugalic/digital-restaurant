package com.drestaurant.order.domain

import com.drestaurant.common.domain.model.Money
import com.drestaurant.order.domain.api.*
import com.drestaurant.order.domain.model.OrderDetails
import com.drestaurant.order.domain.model.OrderLineItem
import com.drestaurant.order.domain.model.OrderState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal

/**
 *
 * Order - aggregate root
 *
 * @author: idugalic
 */

@Aggregate(snapshotTriggerDefinition = "orderSnapshotTriggerDefinition")
internal class Order {

    /**
     * Aggregates that are managed by Axon must have a unique identifier. The annotation 'AggregateIdentifier'
     * identifies the id field as such.
     */
    @AggregateIdentifier
    private var id: String? = null

    private lateinit var lineItems: List<OrderLineItem>
    private lateinit var restaurantId: String
    private lateinit var consumerId: String
    private lateinit var state: OrderState

    val orderTotal: Money get() = calculateOrderTotal(lineItems)

    /**
     * This default constructor is used by the Repository to construct a prototype
     * [Order]. Events are then used to set properties such as the
     * Order's Id in order to make the Aggregate reflect it's true
     * logical state.
     */
    constructor()

    // CREATE
    /**
     * This constructor is marked as a 'CommandHandler' for the [CreateOrderCommand].
     * This command can be used to construct new instances of the Aggregate. If
     * successful a new [OrderCreationInitiatedEvent] is 'applied' to the aggregate using the
     * Axon 'apply' method. The apply method appears to also propagate the Event to
     * any other registered 'Event Listeners', who may take further action.
     *
     * @param command
     */
    @CommandHandler
    constructor(command: CreateOrderCommand) {
        apply(OrderCreationInitiatedEvent(OrderDetails(command.orderInfo, calculateOrderTotal(command.orderInfo.lineItems)), command.targetAggregateIdentifier, command.auditEntry))
    }

    /**
     * This method is marked as an EventSourcingHandler and is therefore used by the
     * Axon framework to handle events of the specified type [OrderCreationInitiatedEvent].
     * The [OrderCreationInitiatedEvent] can be raised either by the constructor during
     * Order(CreateOrderCommand) or by the evensourcing zepository when 're-loading' the aggregate.
     *
     * @param event
     */
    @EventSourcingHandler
    fun on(event: OrderCreationInitiatedEvent) {
        id = event.aggregateIdentifier
        consumerId = event.orderDetails.consumerId
        restaurantId = event.orderDetails.restaurantId
        lineItems = event.orderDetails.lineItems
        state = OrderState.CREATE_PENDING
    }

    @CommandHandler
    fun markOrderAsVerifiedByCustomer(command: MarkOrderAsVerifiedByCustomerInternalCommand) {
        if (OrderState.CREATE_PENDING == state) {
            apply(OrderVerifiedByCustomerEvent(command.targetAggregateIdentifier, command.customerId, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATE_PENDING")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderVerifiedByCustomerEvent) {
        state = OrderState.VERIFIED_BY_CUSTOMER
    }

    @CommandHandler
    fun markOrderAsVerifiedByRestaurant(command: MarkOrderAsVerifiedByRestaurantInternalCommand) {
        if (OrderState.VERIFIED_BY_CUSTOMER == state) {
            apply(OrderVerifiedByRestaurantEvent(command.targetAggregateIdentifier, command.restaurantId, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not VERIFIED_BY_CUSTOMER")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderVerifiedByRestaurantEvent) {
        state = OrderState.VERIFIED_BY_RESTAURANT
    }

    @CommandHandler
    fun markOrderAsPrepared(command: MarkOrderAsPreparedInternalCommand) {
        if (OrderState.VERIFIED_BY_RESTAURANT == state) {
            apply(OrderPreparedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not VERIFIED_BY_RESTAURANT")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderPreparedEvent) {
        state = OrderState.PREPARED
    }

    @CommandHandler
    fun markOrderAsReadyForDelivery(command: MarkOrderAsReadyForDeliveryInternalCommand) {
        if (OrderState.PREPARED == state) {
            apply(OrderReadyForDeliveryEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not PREPARED")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderReadyForDeliveryEvent) {
        state = OrderState.READY_FOR_DELIVERY
    }

    @CommandHandler
    fun markOrderAsDelivered(command: MarkOrderAsDeliveredInternalCommand) {
        if (OrderState.READY_FOR_DELIVERY == state) {
            apply(OrderDeliveredEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not READY_FOR_DELIVERY")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderDeliveredEvent) {
        state = OrderState.DELIVERED
    }

    @CommandHandler
    fun markOrderAsRejected(command: MarkOrderAsRejectedInternalCommand) {
        if (OrderState.VERIFIED_BY_CUSTOMER == state || OrderState.CREATE_PENDING == state) {
            apply(OrderRejectedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not VERIFIED_BY_CUSTOMER or CREATE_PENDING")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderRejectedEvent) {
        state = OrderState.REJECTED
    }

    private fun calculateOrderTotal(lineItems: List<OrderLineItem>) = lineItems.stream().map(OrderLineItem::total).reduce(Money(BigDecimal.ZERO), Money::add)

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

}
