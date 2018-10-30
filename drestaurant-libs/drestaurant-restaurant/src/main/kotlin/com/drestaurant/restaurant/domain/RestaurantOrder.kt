package com.drestaurant.restaurant.domain

import com.drestaurant.restaurant.domain.api.*
import com.drestaurant.restaurant.domain.api.model.*
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate(snapshotTriggerDefinition = "restaurantOrderSnapshotTriggerDefinition")
internal class RestaurantOrder {

    @AggregateIdentifier
    private lateinit var id: RestaurantOrderId
    private lateinit var restaurantId: RestaurantId
    private lateinit var state: RestaurantOrderState
    private lateinit var lineItems: List<RestaurantOrderLineItem>

    constructor()

    @CommandHandler
    constructor(command: CreateRestaurantOrderCommand) {
        apply(RestaurantOrderCreationInitiatedInternalEvent(RestaurantOrderDetails(command.orderDetails.lineItems), command.restaurantId, command.targetAggregateIdentifier, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreationInitiatedInternalEvent) {
        id = event.aggregateIdentifier
        restaurantId = event.restaurantId
        lineItems = event.orderDetails.lineItems
        state = RestaurantOrderState.CREATE_PENDING
    }

    @CommandHandler
    fun markOrderAsCreated(command: MarkRestaurantOrderAsCreatedInternalCommand) {
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
    fun markOrderAsRejected(command: MarkRestaurantOrderAsRejectedInternalCommand) {
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
