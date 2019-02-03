package com.drestaurant.restaurant.domain

import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import com.drestaurant.restaurant.domain.api.MarkRestaurantOrderAsPreparedCommand
import com.drestaurant.restaurant.domain.api.RestaurantOrderCreatedEvent
import com.drestaurant.restaurant.domain.api.RestaurantOrderPreparedEvent
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderId
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderLineItem
import com.drestaurant.restaurant.domain.api.model.RestaurantOrderState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

/**
 * Restaurant Order - aggregate root
 *
 * @author: idugalic
 */
@Aggregate(snapshotTriggerDefinition = "restaurantOrderSnapshotTriggerDefinition")
internal class RestaurantOrder {

    @AggregateIdentifier
    private lateinit var id: RestaurantOrderId
    private lateinit var restaurantId: RestaurantId
    private lateinit var state: RestaurantOrderState
    private lateinit var lineItems: List<RestaurantOrderLineItem>

    constructor()

    constructor(command: CreateRestaurantOrderCommand) {
        apply(RestaurantOrderCreatedEvent(command.orderDetails.lineItems, command.restaurantOrderId, command.targetAggregateIdentifier, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: RestaurantOrderCreatedEvent) {
        id = event.restaurantOrderId;
        restaurantId = event.aggregateIdentifier;
        lineItems = event.lineItems;
        state = RestaurantOrderState.CREATED
    }

    @CommandHandler
    fun handle(command: MarkRestaurantOrderAsPreparedCommand) {
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
