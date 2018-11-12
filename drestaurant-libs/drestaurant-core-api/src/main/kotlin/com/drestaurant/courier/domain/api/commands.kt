package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.PersonName
import com.drestaurant.courier.domain.api.model.CourierId
import com.drestaurant.courier.domain.api.model.CourierOrderId
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.Valid

/**
 * Abstract Courier command
 */
abstract class CourierCommand(open val targetAggregateIdentifier: CourierId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * Abstract CourierOrder command
 */
abstract class CourierOrderCommand(open val targetAggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * This command is used to construct/hire a courier
 */
data class CreateCourierCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierId, @field:Valid val name: PersonName, val maxNumberOfActiveOrders: Int, override val auditEntry: AuditEntry) : CourierCommand(targetAggregateIdentifier, auditEntry) {

    constructor(name: PersonName, maxNumberOfActiveOrders: Int, auditEntry: AuditEntry) : this(CourierId(), name, maxNumberOfActiveOrders, auditEntry)
}

/**
 * This command is used to construct new courier order/delivery
 */
data class CreateCourierOrderCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry) {

    constructor(auditEntry: AuditEntry) : this(CourierOrderId(), auditEntry)
}

/**
 * This command is used to assign/claim courier order (targetAggregateIdentifier) to/by courier
 */
data class AssignCourierOrderToCourierCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, val courierId: CourierId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry)

/**
 * This command is used to mark courier order (targetAggregateIdentifier) as delivered
 */
data class MarkCourierOrderAsDeliveredCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry)
