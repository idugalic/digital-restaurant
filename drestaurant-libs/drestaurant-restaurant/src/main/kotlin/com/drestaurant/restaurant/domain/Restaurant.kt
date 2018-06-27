package com.drestaurant.restaurant.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import com.drestaurant.restaurant.domain.model.RestaurantMenu
import com.drestaurant.restaurant.domain.model.RestaurantOrderLineItem
import com.drestaurant.restaurant.domain.model.RestaurantState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import java.util.stream.Collectors

/**
 *
 * Restaurant - aggregate root
 *
 * @author: idugalic
 */

@Aggregate
internal class Restaurant {

    /**
     * Aggregates that are managed by Axon must have a unique identifier. The
     * annotation 'AggregateIdentifier' identifies the id field as such.
     */
    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var name: String
    private lateinit var menu: RestaurantMenu
    private lateinit var state: RestaurantState

    /**
     * This default constructor is used by the Repository to construct a prototype
     * [Restaurant]. Events are then used to set properties such as the
     * Restaurant's Id in order to make the Aggregate reflect it's true logical
     * state.
     */
    constructor()

    /**
     * This constructor is marked as a 'CommandHandler' for the
     * [CreateRestaurantCommand]. This command can be used to construct new
     * instances of the Aggregate. If successful a new [RestaurantCreatedEvent]
     * is 'applied' to the aggregate using the Axon 'apply' method. The apply method
     * appears to also propagate the Event to any other registered 'Event
     * Listeners', who may take further action.
     *
     * @param command
     */
    @CommandHandler constructor(command: CreateRestaurantCommand) {
        apply(RestaurantCreatedEvent(command.name, command.menu, command.targetAggregateIdentifier, command.auditEntry))
    }

    /**
     * This method is marked as an EventSourcingHandler and is therefore used by the
     * Axon framework to handle events of the specified type
     * [RestaurantCreatedEvent]. The [RestaurantCreatedEvent] can be raised
     * either by the constructor during Restaurant(CreateRestaurantCommand) or by the
     * eventsourcing repository when 're-loading' the aggregate.
     *
     * @param event
     */
    @EventSourcingHandler
    fun on(event: RestaurantCreatedEvent) {
        this.id = event.aggregateIdentifier
        this.name = event.name
        this.menu = event.menu
        this.state = RestaurantState.OPEN
    }

    /**
     * Validate order by restaurant.
     * Checking if order contain line items that are on the menu only.
     *
     * @param orderId
     * @param lineItems
     * @param auditEntry
     */
    fun validateOrder(orderId: String, lineItems: List<RestaurantOrderLineItem>, auditEntry: AuditEntry) {
        if (menu.menuItems.stream().map { mi -> mi.id }.collect(Collectors.toList()).containsAll(lineItems.stream().map { li -> li.menuItemId }.collect(Collectors.toList()))) {
            apply(OrderValidatedWithSuccessByRestaurantEvent(this.id, orderId, auditEntry))

        } else {
            apply(OrderValidatedWithErrorByRestaurantEvent(this.id, orderId, auditEntry))
        }
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }
}
