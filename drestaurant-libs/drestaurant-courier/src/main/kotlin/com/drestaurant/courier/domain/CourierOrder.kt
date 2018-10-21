package com.drestaurant.courier.domain

import com.drestaurant.courier.domain.api.*
import com.drestaurant.courier.domain.model.CourierOrderState
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
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
        id = event.aggregateIdentifier
        state = CourierOrderState.CREATED
    }

    @CommandHandler
    fun assignToCourier(command: AssignCourierOrderToCourierCommand) {
        if (CourierOrderState.CREATED == state) {
            apply(CourierOrderAssigningInitiatedInternalEvent(command.courierId, command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not CREATED")
        }
    }

    @EventSourcingHandler
    fun on(event: CourierOrderAssigningInitiatedInternalEvent) {
        state = CourierOrderState.ASSIGN_PENDING
    }

    @CommandHandler
    fun markOrderAsAssigned(command: MarkCourierOrderAsAssignedInternalCommand) {
        if (CourierOrderState.ASSIGN_PENDING == state) {
            apply(CourierOrderAssignedEvent(command.targetAggregateIdentifier, command.courierId, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not ASSIGN_PENDING")
        }
    }

    @EventSourcingHandler
    fun on(event: CourierOrderAssignedEvent) {
        cuourierId = event.courierId
        state = CourierOrderState.ASSIGNED
    }

    @CommandHandler
    fun markOrderAsNotAssigned(command: MarkCourierOrderAsNotAssignedInternalCommand) {
        if (CourierOrderState.ASSIGN_PENDING == state) {
            apply(CourierOrderNotAssignedEvent(command.targetAggregateIdentifier, command.auditEntry))
        } else {
            throw UnsupportedOperationException("The current state is not ASSIGN_PENDING")
        }
    }

    @EventSourcingHandler
    fun on(event: CourierOrderNotAssignedEvent) {
        state = CourierOrderState.CREATED
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
        state = CourierOrderState.DELIVERED
    }

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}
