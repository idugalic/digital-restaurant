package com.drestaurant.courier.domain

import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName
import com.drestaurant.courier.domain.api.CourierCreatedEvent
import com.drestaurant.courier.domain.api.CreateCourierCommand
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate

/**
 *
 * Courier - aggregate root
 */
@Aggregate
internal class Courier {

    /**
     * Aggregates that are managed by Axon must have a unique identifier. The
     * annotation 'AggregateIdentifier' identifies the id field as such.
     */
    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var name: PersonName
    private var maxNumberOfActiveOrders: Int = 5
    private var numberOfActiveOrders: Int = 0

    /**
     * This default constructor is used by the Repository to construct a prototype
     * [Courier]. Events are then used to set properties such as the
     * Courier Id in order to make the Aggregate reflect it's true logical state.
     */
    constructor()

    /**
     * This constructor is marked as a 'CommandHandler' for the
     * [CreateCourierCommand]. This command can be used to construct new
     * instances of the Aggregate. If successful a new [CourierCreatedEvent]
     * is 'applied' to the aggregate using the Axon 'apply' method. The apply method
     * appears to also propagate the Event to any other registered 'Event
     * Listeners', who may take further action.
     *
     * @param command
     */
    @CommandHandler
    constructor(command: CreateCourierCommand) {
        apply(CourierCreatedEvent(command.name, command.maxNumberOfActiveOrders, command.targetAggregateIdentifier, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: CourierCreatedEvent) {
        this.id = event.aggregateIdentifier
        this.name = event.name
        this.maxNumberOfActiveOrders = event.maxNumberOfActiveOrders
        this.numberOfActiveOrders = this.numberOfActiveOrders + 1
    }

    fun validateOrder(orderId: String, auditEntry: AuditEntry) {
        if (numberOfActiveOrders + 1 > maxNumberOfActiveOrders) {
            apply(OrderValidatedWithErrorByCourierEvent(this.id, orderId, auditEntry))

        } else {
            apply(OrderValidatedWithSuccessByCourierEvent(this.id, orderId, auditEntry))
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
