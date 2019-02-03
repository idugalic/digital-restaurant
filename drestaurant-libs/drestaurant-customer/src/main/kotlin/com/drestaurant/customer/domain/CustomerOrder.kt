package com.drestaurant.customer.domain

import com.drestaurant.common.domain.api.model.Money
import com.drestaurant.customer.domain.api.CreateCustomerOrderCommand
import com.drestaurant.customer.domain.api.CustomerOrderCreatedEvent
import com.drestaurant.customer.domain.api.CustomerOrderDeliveredEvent
import com.drestaurant.customer.domain.api.MarkCustomerOrderAsDeliveredCommand
import com.drestaurant.customer.domain.api.model.CustomerId
import com.drestaurant.customer.domain.api.model.CustomerOrderId
import com.drestaurant.customer.domain.api.model.CustomerOrderState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate(snapshotTriggerDefinition = "customerOrderSnapshotTriggerDefinition")
internal class CustomerOrder {

    @AggregateIdentifier
    private lateinit var id: CustomerOrderId
    private lateinit var customerId: CustomerId
    private lateinit var state: CustomerOrderState
    private lateinit var orderTotal: Money

    /**
     * This default constructor is used by the Repository to construct a prototype
     * [Customer]. Events are then used to set properties such as the
     * Customer's Id in order to make the Aggregate reflect it's true logical state.
     */
    constructor()

    constructor(command: CreateCustomerOrderCommand) {
        apply(CustomerOrderCreatedEvent(command.orderTotal, command.targetAggregateIdentifier, command.customerOrderId, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: CustomerOrderCreatedEvent) {
        this.id = event.customerOrderId
        this.customerId = event.aggregateIdentifier
        this.orderTotal = event.orderTotal
        this.state = CustomerOrderState.CREATED
    }

    @CommandHandler
    fun handle(command: MarkCustomerOrderAsDeliveredCommand) {
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
