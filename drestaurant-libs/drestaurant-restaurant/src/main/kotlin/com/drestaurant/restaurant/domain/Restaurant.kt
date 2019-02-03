package com.drestaurant.restaurant.domain

import com.drestaurant.restaurant.domain.api.CreateRestaurantCommand
import com.drestaurant.restaurant.domain.api.CreateRestaurantOrderCommand
import com.drestaurant.restaurant.domain.api.RestaurantCreatedEvent
import com.drestaurant.restaurant.domain.api.model.RestaurantId
import com.drestaurant.restaurant.domain.api.model.RestaurantMenu
import com.drestaurant.restaurant.domain.api.model.RestaurantState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateLifecycle.createNew
import org.axonframework.spring.stereotype.Aggregate
import java.util.stream.Collectors

/**
 *
 * Restaurant - aggregate root
 *
 * @author: idugalic
 */

@Aggregate(snapshotTriggerDefinition = "restaurantSnapshotTriggerDefinition")
internal class Restaurant {

    /**
     * Aggregates that are managed by Axon must have a unique identifier. The
     * annotation 'AggregateIdentifier' identifies the id field as such.
     */
    @AggregateIdentifier
    private lateinit var id: RestaurantId
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
    @CommandHandler
    constructor(command: CreateRestaurantCommand) {
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
        id = event.aggregateIdentifier
        name = event.name
        menu = event.menu
        state = RestaurantState.OPEN
    }


    /**
     * This method creates kitchen order for specific restaurant / Spawns new aggregate
     */
    @CommandHandler
    fun handle(command: CreateRestaurantOrderCommand) {
        if (menu.menuItems.stream().map { mi -> mi.id }.collect(Collectors.toList()).containsAll(command.orderDetails.lineItems.stream().map { li -> li.menuItemId }.collect(Collectors.toList()))) {
            createNew(RestaurantOrder::class.java) { RestaurantOrder(command) }
        } else {
            throw UnsupportedOperationException("Restaurant order invalid - not on the Menu")
        }
    }

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}
