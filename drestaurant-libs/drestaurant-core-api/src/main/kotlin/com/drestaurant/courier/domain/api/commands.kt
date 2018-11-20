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
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
abstract class CourierCommand(open val targetAggregateIdentifier: CourierId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * Abstract CourierOrder command
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
abstract class CourierOrderCommand(open val targetAggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * A command to create/hire a 'courier'
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property name full name of the person/courier
 * @property maxNumberOfActiveOrders maximal number of active orders that courier can deliver
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class CreateCourierCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierId, @field:Valid val name: PersonName, val maxNumberOfActiveOrders: Int, override val auditEntry: AuditEntry) : CourierCommand(targetAggregateIdentifier, auditEntry) {

    constructor(name: PersonName, maxNumberOfActiveOrders: Int, auditEntry: AuditEntry) : this(CourierId(), name, maxNumberOfActiveOrders, auditEntry)
}

/**
 * A command to create new 'courier order'/delivery
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 *
 */
data class CreateCourierOrderCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry) {

    constructor(auditEntry: AuditEntry) : this(CourierOrderId(), auditEntry)
}

/**
 * A command to assign 'courier order' ([targetAggregateIdentifier]) to 'courier' ([courierId])
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property courierId identifier of a courier to whom the courier order will be assigned
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class AssignCourierOrderToCourierCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, val courierId: CourierId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry)

/**
 * A command to mark 'courier order' ([targetAggregateIdentifier]) as delivered
 *
 * @property targetAggregateIdentifier target aggregate identifier
 * @property auditEntry audit entry holds the information of 'who' and 'when' performed the command
 */
data class MarkCourierOrderAsDeliveredCommand(@TargetAggregateIdentifier override val targetAggregateIdentifier: CourierOrderId, override val auditEntry: AuditEntry) : CourierOrderCommand(targetAggregateIdentifier, auditEntry)
