package com.drestaurant.customer.domain

import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.customer.domain.api.CreateCustomerCommand
import com.drestaurant.customer.domain.api.CustomerCreatedEvent
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

/**
 *
 * Customer - aggregate root
 *
 * @author: idugalic
 *
 */

@Aggregate(snapshotTriggerDefinition = "customerSnapshotTriggerDefinition")
internal class Customer {
    /**
     * Aggregates that are managed by Axon must have a unique identifier. The
     * annotation 'AggregateIdentifier' identifies the id field as such.
     */
    @AggregateIdentifier
    private lateinit var id: CustomerId
    private lateinit var name: PersonName
    private lateinit var orderLimit: Money

    /**
     * This default constructor is used by the Repository to construct a prototype
     * [Customer]. Events are then used to set properties such as the
     * Customer's Id in order to make the Aggregate reflect it's true logical state.
     */
    constructor()

    /**
     * This constructor is marked as a 'CommandHandler' for the
     * [CreateCustomerCommand]. This command can be used to construct new
     * instances of the Aggregate. If successful a new [CustomerCreatedEvent]
     * is 'applied' to the aggregate using the Axon 'apply' method. The apply method
     * appears to also propagate the Event to any other registered 'Event
     * Listeners', who may take further action.
     *
     * @param command
     */
    @CommandHandler
    constructor(command: CreateCustomerCommand) {
        apply(CustomerCreatedEvent(command.name, command.orderLimit, command.targetAggregateIdentifier, command.auditEntry))
    }

    fun validateOrder(orderId: CustomerOrderId, orderTotal: Money, auditEntry: AuditEntry) {
        if (orderTotal.isGreaterThanOrEqual(orderLimit)) {
            apply(CustomerValidatedOrderWithErrorInternalEvent(id, orderId, orderTotal, auditEntry))

        } else {
            apply(CustomerValidatedOrderWithSuccessInternalEvent(id, orderId, orderTotal, auditEntry))
        }
    }

    /**
     * This method is marked as an EventSourcingHandler and is therefore used by the
     * Axon framework to handle events of the specified type
     * [CustomerCreatedEvent]. The [CustomerCreatedEvent] can be raised
     * either by the constructor during Customer(CustomerCreatedEvent) or by the
     * eventsourcing repository when 're-loading' the aggregate.
     *
     * @param event
     */
    @EventSourcingHandler
    fun on(event: CustomerCreatedEvent) {
        id = event.aggregateIdentifier
        name = event.name
        orderLimit = event.orderLimit
    }

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

}
