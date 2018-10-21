package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.command.AuditableAbstractCommand
import com.drestaurant.common.domain.model.AuditEntry
import com.drestaurant.common.domain.model.PersonName
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid

/**
 * This command is used to construct/hire a courier
 */
class CreateCourierCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:Valid val name: PersonName, val maxNumberOfActiveOrders: Int, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(name: PersonName, maxNumberOfActiveOrders: Int, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), name, maxNumberOfActiveOrders, auditEntry)
}

/**
 * This command is used to construct new courier order/delivery
 */
class CreateCourierOrderCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), auditEntry)
}

/**
 * This command is used to assign/claim courier order (targetAggregateIdentifier) to/by courier
 */
class AssignCourierOrderToCourierCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val courierId: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * This command is used to mark courier order (targetAggregateIdentifier) as delivered
 */
class MarkCourierOrderAsDeliveredCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

