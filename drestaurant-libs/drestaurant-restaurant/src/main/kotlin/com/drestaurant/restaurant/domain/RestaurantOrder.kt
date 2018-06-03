package com.drestaurant.restaurant.domain

import com.drestaurant.restaurant.domain.api.*
import com.drestaurant.restaurant.domain.model.RestaurantOrderDetails
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import com.drestaurant.restaurant.domain.model.RestaurantOrderState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
internal class RestaurantOrder {

    @AggregateIdentifier
    private var id: String? = null
    private var restaurantId: String? = null
    private var state: RestaurantOrderState? = null
    private var lineItems: List<RestaurantOrderLineItem>? = null

    constructor() {}

    @CommandHandler constructor(command: CreateRestaurantOrderCommand) {
        apply(RestaurantOrderCreationInitiatedEvent(RestaurantOrderDetails(command.orderDetails.getLineItems()), command.restaurantId, command.targetAggregateIdentifier, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreationInitiatedEvent) {
        this.id = event.aggregateIdentifier
        this.restaurantId = event.restaurantId
        this.lineItems = event.orderDetails.getLineItems()
        this.state = RestaurantOrderState.CREATE_PENDING
    }

    @CommandHandler
    fun markOrderAsCreated(command: MarkRestaurantOrderAsCreatedCommand) {
        if (RestaurantOrderState.CREATE_PENDING == state) {
            apply(RestaurantOrderCreatedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATE_PENDING")
        }

    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreatedEvent) {
        this.state = RestaurantOrderState.CREATED
    }

    @CommandHandler
    fun markOrderAsRejected(command: MarkRestaurantOrderAsRejectedCommand) {
        if (RestaurantOrderState.CREATE_PENDING == state) {
            apply(RestaurantOrderRejectedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATE_PENDING")
        }
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderRejectedEvent) {
        this.state = RestaurantOrderState.REJECTED
    }

    @CommandHandler
    fun markOrderAsPrepared(command: MarkRestaurantOrderAsPreparedCommand) {
        if (RestaurantOrderState.CREATED == state) {
            apply(RestaurantOrderPreparedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATED")
        }
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderPreparedEvent) {
        this.state = RestaurantOrderState.PREPARED
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    override fun equals(o: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, o)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }

}
