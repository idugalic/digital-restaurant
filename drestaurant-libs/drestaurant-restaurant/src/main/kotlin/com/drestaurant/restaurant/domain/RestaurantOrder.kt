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

@Aggregate(snapshotTriggerDefinition = "restaurantOrderSnapshotTriggerDefinition")
internal class RestaurantOrder {

    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var restaurantId: String
    private lateinit var state: RestaurantOrderState
    private lateinit var lineItems: List<RestaurantOrderLineItem>

    constructor()

    @CommandHandler
    constructor(command: CreateRestaurantOrderCommand) {
        apply(RestaurantOrderCreationInitiatedEvent(RestaurantOrderDetails(command.orderDetails.lineItems), command.restaurantId, command.targetAggregateIdentifier, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreationInitiatedEvent) {
        id = event.aggregateIdentifier
        restaurantId = event.restaurantId
        lineItems = event.orderDetails.lineItems
        state = RestaurantOrderState.CREATE_PENDING
    }

    @CommandHandler
    fun markOrderAsCreated(command: MarkRestaurantOrderAsCreatedCommand) {
        if (RestaurantOrderState.CREATE_PENDING == state) {
            apply(RestaurantOrderCreatedEvent(lineItems, restaurantId, command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATE_PENDING")
        }

    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreatedEvent) {
        state = RestaurantOrderState.CREATED
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
        state = RestaurantOrderState.REJECTED
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
        state = RestaurantOrderState.PREPARED
    }

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}
