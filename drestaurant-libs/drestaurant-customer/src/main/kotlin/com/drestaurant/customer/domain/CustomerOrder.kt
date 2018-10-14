package com.drestaurant.customer.domain

import com.drestaurant.common.domain.model.Money
import com.drestaurant.customer.domain.api.*
import com.drestaurant.customer.domain.model.CustomerOrderState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate

@Aggregate(snapshotTriggerDefinition = "customerOrderSnapshotTriggerDefinition")
internal class CustomerOrder {

    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var customerId: String
    private lateinit var state: CustomerOrderState
    private lateinit var orderTotal: Money

    /**
     * This default constructor is used by the Repository to construct a prototype
     * [Customer]. Events are then used to set properties such as the
     * Customer's Id in order to make the Aggregate reflect it's true logical state.
     */
    constructor()

    @CommandHandler
    constructor(command: CreateCustomerOrderCommand) {
        apply(CustomerOrderCreationInitiatedInternalEvent(command.orderTotal, command.customerId, command.targetAggregateIdentifier, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: CustomerOrderCreationInitiatedInternalEvent) {
        this.id = event.aggregateIdentifier
        this.customerId = event.customerId
        this.orderTotal = event.orderTotal
        this.state = CustomerOrderState.CREATE_PENDING
    }

    @CommandHandler
    fun markOrderAsCreated(command: MarkCustomerOrderAsCreatedInternalCommand) {
        if (CustomerOrderState.CREATE_PENDING == state) {
            apply(CustomerOrderCreatedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATE_PENDING")
        }

    }

    @EventSourcingHandler
    fun on(event: CustomerOrderCreatedEvent) {
        this.state = CustomerOrderState.CREATED
    }

    @CommandHandler
    fun markOrderAsRejected(command: MarkCustomerOrderAsRejectedInternalCommand) {
        if (CustomerOrderState.CREATE_PENDING == state) {
            apply(CustomerOrderRejectedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATE_PENDING")
        }
    }

    @EventSourcingHandler
    fun on(event: CustomerOrderRejectedEvent) {
        this.state = CustomerOrderState.REJECTED
    }

    @CommandHandler
    fun markOrderAsDelivered(command: MarkCustomerOrderAsDeliveredCommand) {
        if (CustomerOrderState.CREATED == state) {
            apply(CustomerOrderDeliveredEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATED")
        }
    }

    @EventSourcingHandler
    fun on(event: CustomerOrderDeliveredEvent) {
        this.state = CustomerOrderState.DELIVERED
    }

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

}
