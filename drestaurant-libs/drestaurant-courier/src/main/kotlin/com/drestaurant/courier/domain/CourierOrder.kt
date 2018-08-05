package com.drestaurant.courier.domain

import com.drestaurant.courier.domain.api.*
import com.drestaurant.courier.domain.model.CourierOrderState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate

@Aggregate(snapshotTriggerDefinition = "courierOrderSnapshotTriggerDefinition")
internal class CourierOrder {

    @AggregateIdentifier
    private lateinit var id: String
    private lateinit var cuourierId: String
    private lateinit var state: CourierOrderState

    constructor()

    @CommandHandler
    constructor(command: CreateCourierOrderCommand) {
        apply(CourierOrderCreatedEvent(command.targetAggregateIdentifier, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: CourierOrderCreatedEvent) {
        this.id = event.aggregateIdentifier
        this.state = CourierOrderState.CREATED
    }

    @CommandHandler
    fun assignToCourier(command: AssignCourierOrderToCourierCommand) {
        if (CourierOrderState.CREATED == state) {
            apply(CourierOrderAssigningInitiatedEvent(command.courierId, command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATED")
        }
    }

    @EventSourcingHandler
    fun on(event: CourierOrderAssigningInitiatedEvent) {
        this.state = CourierOrderState.ASSIGN_PENDING
    }

    @CommandHandler
    fun markOrderAsAssigned(command: MarkCourierOrderAsAssignedCommand) {
        if (CourierOrderState.ASSIGN_PENDING == state) {
            apply(CourierOrderAssignedEvent(command.targetAggregateIdentifier, command.courierId, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not ASSIGN_PENDING")
        }
    }

    @EventSourcingHandler
    fun on(event: CourierOrderAssignedEvent) {
        this.cuourierId = event.courierId
        this.state = CourierOrderState.ASSIGNED
    }

    @CommandHandler
    fun markOrderAsNotAssigned(command: MarkCourierOrderAsNotAssignedCommand) {
        if (CourierOrderState.ASSIGN_PENDING == state) {
            apply(CourierOrderNotAssignedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not ASSIGN_PENDING")
        }
    }

    @EventSourcingHandler
    fun on(event: CourierOrderNotAssignedEvent) {
        this.state = CourierOrderState.CREATED
    }

    @CommandHandler
    fun markOrderAsDelivered(command: MarkCourierOrderAsDeliveredCommand) {
        if (CourierOrderState.ASSIGNED == state) {
            apply(CourierOrderDeliveredEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not ASSIGNED")
        }
    }

    @EventSourcingHandler
    fun on(event: CourierOrderDeliveredEvent) {
        this.state = CourierOrderState.DELIVERED
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
