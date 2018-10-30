package com.drestaurant.courier.domain.api

import com.drestaurant.common.domain.api.AuditableAbstractCommand
import com.drestaurant.common.domain.api.model.AuditEntry
import com.drestaurant.common.domain.api.model.PersonName
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid

/**
 * This command is used to construct/hire a courier
 */
data class CreateCourierCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, @field:Valid val name: PersonName, val maxNumberOfActiveOrders: Int, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(name: PersonName, maxNumberOfActiveOrders: Int, auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), name, maxNumberOfActiveOrders, auditEntry)
}

/**
 * This command is used to construct new courier order/delivery
 */
data class CreateCourierOrderCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry) {

    constructor(auditEntry: AuditEntry) : this(UUID.randomUUID().toString(), auditEntry)
}

/**
 * This command is used to assign/claim courier order (targetAggregateIdentifier) to/by courier
 */
data class AssignCourierOrderToCourierCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, val courierId: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)

/**
 * This command is used to mark courier order (targetAggregateIdentifier) as delivered
 */
data class MarkCourierOrderAsDeliveredCommand(@TargetAggregateIdentifier val targetAggregateIdentifier: String, override val auditEntry: AuditEntry) : AuditableAbstractCommand(auditEntry)
